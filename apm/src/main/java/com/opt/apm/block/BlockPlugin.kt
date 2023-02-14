package com.opt.apm.block

import android.app.Application
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import com.opt.apm.base.*
import com.opt.apm.base.util.ApmUtil
import com.opt.apm.base.util.ThreadManagerUtil
import com.opt.apm.base.window.ApmWindowManager
import kotlin.math.roundToInt

/**
 * 卡顿监控，包括：ANR、慢函数、掉帧
 */
class BlockPlugin : Plugin {

    private var mainHandler: Handler? = null
    private var application: Application? = null
    private var apmWindowManager: ApmWindowManager? = null

    /**
     * 慢方法,耗时超过500ms
     */
    var SLOW_TIME_BLOCK = 500  // 定义慢方法的时间
    private var slowMethodHandlerThread: HandlerThread? = null
    private var slowMethodStackCollectHandler: Handler? = null
    private var slowMethodBlockStackTraceMap = HashMap<String, BlockStackTraceInfo>()
    private val SLOW_COLLECT_TIME: Long = 500L // 收集堆栈的时间

    /**
     * ANR,耗时超过5S
     */
    var ANR_TIME_BLOCK = 5_000  // 定义ANR的时间
    private var anrHandlerThread: HandlerThread? = null
    private var anrStackCollectHandler: Handler? = null
    private var anrBlockStackTraceMap = HashMap<String, BlockStackTraceInfo>()
    private val ANR_COLLECT_TIME: Long = 1000L // 收集堆栈的时间

    /**
     * 帧：掉帧、帧率
     */
    var dropFrameListenerThreshold = 3  // 掉帧上报阀值
    private var frameHandlerThread: HandlerThread? = null
    private var frameStackCollectHandler: Handler? = null
    private var frameBlockStackTraceMap = HashMap<String, BlockStackTraceInfo>()
    private var frame_collect_time: Long = 16L // 收集堆栈的时间
    private var collectCount = 0

    // 帧率
    private var sumFrameCost: Long = 0  // 帧数花费的总时间
    private var lastCost = LongArray(1)  // 上一次使用的时间
    private var sumFrames: Long = 0   // 总共的帧数
    private var lastFrames = LongArray(1)   // 上一次掉帧的数量
    private var maxFps = 0f  // 1s中最大的帧率

    /**
     * 开始
     */
    override fun start(application: Application?) {
        val sdkInt = Build.VERSION.SDK_INT
        if (sdkInt < Build.VERSION_CODES.JELLY_BEAN) {
            return
        }
        this.application = application
        // 在主线程中执行Runnable开始订阅主线程监控
        mainHandler = Handler(Looper.getMainLooper())
        mainHandler?.post(runnable)
    }

    /**
     * 结束
     */
    override fun stop() {
        mainHandler?.post {
            UIThreadMonitor.removeObserver(looperListener)
        }
        mainHandler?.removeCallbacksAndMessages(null)
        apmWindowManager?.dismiss()
    }

    /**
     * 开始的Runnable
     */
    private val runnable = Runnable {
        try {

            // 初始化子线程handler，用于收集堆栈
            initStackHandler()

            // 开始主线程监控
            UIThreadMonitor.addObserver(looperListener)
            if (!UIThreadMonitor.isAlive) {
                UIThreadMonitor.startMonitor()
            }
            frame_collect_time = UIThreadMonitor.frameIntervalNanos / Constants.TIME_MILLIS_TO_NANO
            maxFps = (1000f / frame_collect_time.toFloat()).roundToInt().toFloat()

            // 初始化显示信息的window
            apmWindowManager = ApmWindowManager().apply {
                init(application)
                show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 初始化收集：慢函数、ANR、掉帧的堆栈的子线程Handler
     */
    private fun initStackHandler() {
        slowMethodHandlerThread = HandlerThread("SlowMethodMonitor")
        slowMethodHandlerThread?.start()
        slowMethodStackCollectHandler = Handler(slowMethodHandlerThread!!.looper)

        anrHandlerThread = HandlerThread("ANRMonitor")
        anrHandlerThread?.start()
        anrStackCollectHandler = Handler(anrHandlerThread!!.looper)

        frameHandlerThread = HandlerThread("FrameMonitor")
        frameHandlerThread?.start()
        frameStackCollectHandler = Handler(frameHandlerThread!!.looper)
    }


    /**
     * 主线程监控的订阅者
     */
    private val looperListener = object : UIThreadMonitor.UIThreadMonitorObserver() {
        override fun dispatchBegin(beginNs: Long, cpuBeginNs: Long, token: Long) {
            super.dispatchBegin(beginNs, cpuBeginNs, token)
            //装炸弹 ，子线程延迟去收集堆栈
            slowMethodStackCollectHandler?.postDelayed(
                slowMethodStackCollectTask,
                SLOW_COLLECT_TIME
            )
            anrStackCollectHandler?.postDelayed(anrStackCollectTask, ANR_COLLECT_TIME)
            frameStackCollectHandler?.postDelayed(frameStackCollectTask, frame_collect_time)
        }

        override fun dispatchEnd(
            beginNs: Long,
            cpuBeginMs: Long,
            endNs: Long,
            cpuEndMs: Long,
            token: Long,
            isVsyncFrame: Boolean
        ) {
            super.dispatchEnd(beginNs, cpuBeginMs, endNs, cpuEndMs, token, isVsyncFrame)
            // 拆炸弹
            slowMethodStackCollectHandler?.removeCallbacks(slowMethodStackCollectTask)
            anrStackCollectHandler?.removeCallbacks(anrStackCollectTask)

            val costTime = (endNs - beginNs) / Constants.TIME_MILLIS_TO_NANO

            // 慢方法
            if (costTime >= SLOW_TIME_BLOCK && slowMethodBlockStackTraceMap.isNotEmpty()) {
                val traceList = slowMethodBlockStackTraceMap.values.toList()
                traceList.forEach {
                    Log.e(SLOW_METHOD, "慢函数 -> 耗时=${costTime}ms \n ${it.stackTrace}")
                }
                // 更新显示到window中
                apmWindowManager?.insertSlowMethodCount()
            }
            slowMethodBlockStackTraceMap.clear()

            // ANR
            if (costTime >= ANR_TIME_BLOCK && anrBlockStackTraceMap.isNotEmpty()) {
                val traceList = anrBlockStackTraceMap.values.toList()
                traceList.forEach {
                    Log.e(ANR, "ANR函数-> 耗时=${costTime}ms \n ${it.stackTrace}")
                }
                // 更新显示到window中
                apmWindowManager?.insertANRCount()
            }
            anrBlockStackTraceMap.clear()

        }

        override fun doFrame(
            focusedActivity: String?,
            startNs: Long,
            endNs: Long,
            isVsyncFrame: Boolean,
            intendedFrameTimeNs: Long,
            inputCostNs: Long,
            animationCostNs: Long,
            traversalCostNs: Long
        ) {
            // 拆炸弹
            frameStackCollectHandler?.removeCallbacks(frameStackCollectTask)
            application ?: return
            // App在前台
            if (ApmUtil.isAppRunningInForground(application!!, application!!.packageName)) {
                // 执行时间
                val jitter = endNs - intendedFrameTimeNs
                // 跳过的帧数
                val dropFrame = (jitter / UIThreadMonitor.frameIntervalNanos)

                if (apmWindowManager?.isShowing == false) {
                    apmWindowManager?.show()
                }

                // 跳帧
                if (dropFrame > dropFrameListenerThreshold && frameBlockStackTraceMap.isNotEmpty()) {
                    val traceList = frameBlockStackTraceMap.values.toMutableList()
                    val string = StringBuffer()
                    traceList.forEach {
                        string.append(it.stackTrace.toString())
                    }
                    Log.e(DROP_FRAME, "掉帧函数 = ${dropFrame}次 \n ${string}")
                    // 更新显示到window中
                    apmWindowManager?.dropFrameCount(dropFrame.toInt())
                }
                frameBlockStackTraceMap.clear()

                // 帧率
                sumFrameCost += ((dropFrame + 1) * frame_collect_time)// 总时间相加
                sumFrames += 1  // 总帧数+1
                val duration = (sumFrameCost - lastCost[0]).toFloat()  // 这次帧数的时间
                val collectFrame = sumFrames - lastFrames[0]  // 总帧数 - 上次的帧数
                if (duration >= 200) {
                    val fps = maxFps.coerceAtMost(1000f * collectFrame / duration)
                    // 更新显示到window中
                    apmWindowManager?.updateFrameRate(fps.toInt())
                    lastCost[0] = sumFrameCost  // 上一次的时间 等于总时间
                    lastFrames[0] = sumFrames   // 上一次的总帧率
                }

            } else {
                if (apmWindowManager?.isShowing == true) {
                    apmWindowManager?.dismiss()
                }
            }
        }
    }


    /**
     * 慢函数定时收集堆栈任务
     */
    private val slowMethodStackCollectTask = object : Runnable {
        override fun run() {
            val info =
                BlockStackTraceInfo(ThreadManagerUtil.getJavaStackByName(Looper.getMainLooper().thread.name))
            val key = info.getMapKey()
            val value = slowMethodBlockStackTraceMap[key]
            if (value == null) {
                slowMethodBlockStackTraceMap[key] = info
            }
            slowMethodStackCollectHandler?.postDelayed(this, SLOW_COLLECT_TIME)
        }
    }

    /**
     * ANR定时收集堆栈任务
     */
    private val anrStackCollectTask = object : Runnable {
        override fun run() {
            val info =
                BlockStackTraceInfo(ThreadManagerUtil.getJavaStackByName(Looper.getMainLooper().thread.name))
            val key = info.getMapKey()
            val value = anrBlockStackTraceMap[key]
            if (value == null) {
                anrBlockStackTraceMap[key] = info
            }
            anrStackCollectHandler?.postDelayed(this, SLOW_COLLECT_TIME)
        }
    }

    /**
     * 掉帧定时收集堆栈任务
     */
    private val frameStackCollectTask = object : Runnable {
        override fun run() {
            collectCount++
            if (collectCount > 1) {
                val info =
                    BlockStackTraceInfo(ThreadManagerUtil.getJavaStackByName(Looper.getMainLooper().thread.name))
                val key = info.getMapKey()
                val value = frameBlockStackTraceMap[key]
                if (value == null) {
                    frameBlockStackTraceMap[key] = info
                } else {
                    value.collectCount++
                }
            }
            frameStackCollectHandler?.postDelayed(this, frame_collect_time)
        }
    }

    companion object {
        const val SLOW_METHOD = "SlowMethod"  // 慢方法堆栈的Log
        const val ANR = "ANR"  // ANR堆栈的Log
        const val DROP_FRAME = "DropFrame"   // 掉帧堆栈的Log
    }

}