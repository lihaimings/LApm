package com.opt.apm.memory

import android.app.Activity
import android.app.Application
import android.os.Debug
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import shark.*
import java.io.File
import java.lang.ref.ReferenceQueue
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy
import java.text.SimpleDateFormat
import java.util.*

object AppWatcher {


    /**
     * 给弱引用的队列
     */
    private val queue = ReferenceQueue<Any>()

    /**
     * 待观察的对象
     */
    private val watchedObjects = mutableMapOf<String, KeyedWeakReference>()

    private var application: Application? = null
    private val handler = Handler(Looper.getMainLooper())
    private val handlerThread = HandlerThread("Dump-Heap-Thread")
    private var backgroundHandler: Handler? = null
    private val TAG = "AppWatcher"

    private val lifecycleCallbacks =
        object : Application.ActivityLifecycleCallbacks by noOpDelegate() {
            override fun onActivityDestroyed(activity: Activity) {
                // 1. 将Activity传入弱引用
                val key = UUID.randomUUID().toString()
                val reference = KeyedWeakReference(activity, key, "activity", queue)
                //
                watchedObjects[reference.key] = reference
                backgroundHandler?.postDelayed({
                    checkRelease(reference)
                }, 5000)
            }
        }

    private fun checkRelease(keyedWeakReference: KeyedWeakReference) {
        removeWeaklyReferences()
        if (isRelease(keyedWeakReference)) {
            return
        }
        runGC()
        removeWeaklyReferences()
        if (!isRelease(keyedWeakReference)) {
            // dump 堆转储文件并分析
            dumpHeap()
        }
    }

    private fun removeWeaklyReferences() {
        var ref: KeyedWeakReference?
        while (((queue.poll() as? KeyedWeakReference).also { ref = it }) != null) {
            watchedObjects.remove(ref?.key)
        }
    }

    private fun isRelease(weakReference: KeyedWeakReference): Boolean {
        return !watchedObjects.containsKey(weakReference.key)
    }

    private fun runGC() {
        Runtime.getRuntime().gc()
        System.runFinalization()
    }


    // 是否可以在子线程dump和分析
    private fun dumpHeap() {
        backgroundHandler?.post {
            val storageDirectory = File(application?.cacheDir, "leakcanary")
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


    fun install(app: Application) {
        application = app
        app.registerActivityLifecycleCallbacks(lifecycleCallbacks)
        handlerThread.start()
        backgroundHandler = Handler(handlerThread.looper)
    }

    fun unInstall(app: Application) {
        app.unregisterActivityLifecycleCallbacks(lifecycleCallbacks)
    }
}

internal inline fun <reified T : Any> noOpDelegate(): T {
    val javaClass = T::class.java
    return Proxy.newProxyInstance(
        javaClass.classLoader, arrayOf(javaClass), NO_OP_HANDLER
    ) as T
}

private val NO_OP_HANDLER = InvocationHandler { _, _, _ ->

}