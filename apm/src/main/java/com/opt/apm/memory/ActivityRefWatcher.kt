package com.opt.apm.memory

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Debug
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.opt.apm.base.watcher.EmptyActivityLifecycleCallback
import com.opt.apm.base.util.HandlerThreadUtil
import com.opt.apm.base.watcher.RetryableTaskExecutor
import com.opt.apm.memory.model.DestroyedActivityInfo
import shark.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit

class ActivityRefWatcher(val application: Application?) {

    private var handlerThread: HandlerThread? = null
    private var handler: Handler? = null
    private var lastTriggeredTime: Long = 0
    private val maxRedetectTimes = 2
    private val lock = java.lang.Object()

    private val destroyedActivityInfos: ConcurrentLinkedQueue<DestroyedActivityInfo> by lazy { ConcurrentLinkedQueue() }

    private val retryableTaskExecutor: RetryableTaskExecutor by lazy {
        RetryableTaskExecutor(GC_TIME, handlerThread)
    }

    init {
        handlerThread =
            HandlerThreadUtil.getNewHandlerThread("ActivityRefWatcher", Thread.NORM_PRIORITY)
        handler = HandlerThreadUtil.getDefaultHandler()
    }

    private val removedActivityMonitor: ActivityLifecycleCallbacks =
        object : EmptyActivityLifecycleCallback() {
            override fun onActivityDestroyed(p0: Activity) {
                // 弱引用Activity，并收集相关信息
                pushDestroyedActivityInfo(p0)
                // 2s 后开始触发gc
                handler?.postDelayed({ triggerGc() }, delayTime)
            }
        }

    /**
     * 一分钟循环检查
     */
    private val scanDestroyedActivitiesTask: RetryableTaskExecutor.RetryableTask =
        object : RetryableTaskExecutor.RetryableTask {

            override fun execute(): RetryableTaskExecutor.RetryableTask.Status {
                return checkDestroyedActivities()
            }
        }

    fun start() {
        stopDetect()
        application?.registerActivityLifecycleCallbacks(removedActivityMonitor)
        scheduleDetectProcedure()
    }

    fun stop() {

    }

    private fun scheduleDetectProcedure() {
        retryableTaskExecutor.executeInBackground(scanDestroyedActivitiesTask)
    }

    private fun stopDetect() {
        application?.unregisterActivityLifecycleCallbacks(removedActivityMonitor)
        unscheduleDetectProcedure()
    }

    private fun unscheduleDetectProcedure() {
        retryableTaskExecutor.clearTasks()
        destroyedActivityInfos.clear()
    }


    private fun pushDestroyedActivityInfo(activity: Activity) {
        val activityName = activity.javaClass.name
        val uuid = UUID.randomUUID()
        val keyBuilder = java.lang.StringBuilder()
        keyBuilder.append(ACTIVITY_REFKEY_PREFIX)
            .append(activityName)
            .append("_")
            .append(java.lang.Long.toHexString(uuid.mostSignificantBits))
            .append(java.lang.Long.toHexString(uuid.leastSignificantBits))
        val key = keyBuilder.toString()
        val destroyedActivityInfo = DestroyedActivityInfo(key, activity, activityName)
        destroyedActivityInfos.add(destroyedActivityInfo)
        synchronized(lock) {
            lock.notifyAll()
        }
    }

    /**
     * 调用GC
     */
    private fun triggerGc() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTriggeredTime < GC_TIME / 2 - 100) {
            Log.d(TAG, "skip triggering gc for frequency")
            return
        }
        lastTriggeredTime = currentTime
        Log.d(TAG, "triggering gc...")
        Runtime.getRuntime().gc()
        try {
            Thread.sleep(100)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        Runtime.getRuntime().runFinalization()
        Log.d(TAG, "gc was triggered.")
    }

    private fun checkDestroyedActivities(): RetryableTaskExecutor.RetryableTask.Status {
        if (destroyedActivityInfos.isEmpty()) {
            synchronized(lock) {
                try {
                    while (destroyedActivityInfos.isEmpty())
                        lock.wait()
                } catch (ignored: Throwable) {
                    // Ignored.
                }
            }
            return RetryableTaskExecutor.RetryableTask.Status.RETRY
        }

        triggerGc()

        val infoIt = destroyedActivityInfos.iterator()
        while (infoIt.hasNext()) {
            val destroyedActivityInfo = infoIt.next()
            triggerGc()
            if (destroyedActivityInfo.activityRef.get() == null) {
                infoIt.remove()
                continue
            }
            ++destroyedActivityInfo.detectedCount
            if (destroyedActivityInfo.detectedCount < maxRedetectTimes) {
                triggerGc()
                continue
            }
            Log.i(
                TAG,
                "the leaked activity ${destroyedActivityInfo.activityName} with key ${destroyedActivityInfo.key} has been processed. stop polling",
            )
            dumpHeap()
            infoIt.remove()
        }
        return RetryableTaskExecutor.RetryableTask.Status.RETRY
    }


    private fun dumpHeap() {
        handler?.post {
            val storageDirectory = File(application?.cacheDir, "leakactivity")
            if (!storageDirectory.exists()) {
                storageDirectory.mkdir()
            }
            val fileName =
                SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS'.hprof'", Locale.US).format(Date())
            val file = File(storageDirectory, fileName)
            // dump 出堆转储文件
            Debug.dumpHprofData(file.absolutePath)
            Log.i(TAG, "dumpHeap: ${file.absolutePath}")
            // 使用 Shark 库里的 HeapAnalyzer 来分析
            val heapAnalyzer = HeapAnalyzer(OnAnalysisProgressListener { step ->
                Log.i(TAG, "Analysis in progress, working on: ${step.name}")
            })
            val heapAnalysis = heapAnalyzer.analyze(
                heapDumpFile = file,
                leakingObjectFinder = FilteringLeakingObjectFinder(
                    AndroidObjectInspectors.appLeakingObjectFilters
                ),
                referenceMatchers = AndroidReferenceMatchers.appDefaults,
                computeRetainedHeapSize = true,
                objectInspectors = AndroidObjectInspectors.appDefaults.toMutableList(),
                proguardMapping = null,
                metadataExtractor = AndroidMetadataExtractor
            )
            Log.i(TAG, "dumpHeap: \n$heapAnalysis")
        }
    }

    companion object {

        private const val TAG = "ActivityRefWatcher"

        private val delayTime: Long = 2_000

        private const val ACTIVITY_REFKEY_PREFIX = "ACTIVITY_RESCANARY_REFKEY_"

        private val GC_TIME = TimeUnit.MINUTES.toMillis(1)

    }


}