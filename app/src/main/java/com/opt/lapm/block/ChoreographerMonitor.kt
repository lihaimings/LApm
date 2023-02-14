package com.opt.lapm.block

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import com.opt.lapm.util.ThreadManager

/**
 * 掉帧的检测， 实现原理基于Choreographer的FrameCallBack
 */

class ChoreographerMonitor {

    private val choreographerPostFrameCallback: ChoreographerPostFrameCallback by lazy { ChoreographerPostFrameCallback() }

    /***
     *  定义掉帧时间
     */
    private val STANDARD_FRAME_NS = 16666666L
    var blockThresholdNs = 2 * STANDARD_FRAME_NS

    /**
     * 收集掉帧堆栈
     */
    private var handlerThread: HandlerThread? = null
    private var stackCollectHandler: Handler? = null
    private var blockStackTraceMap = HashMap<String, BlockStackTraceInfo>()
    private val COLLECT_TIME: Long = 16L
    private var collectCount = 0

    /**
     * 帧率
     */
    var openFrameRate = true // 开启帧率
    private var nowTime: Long = 1
    private var frameRateCount = 1


    private val TAG = "ChoreographerMonitor"

    init {
        handlerThread = HandlerThread("ChoreographerMonitor")
        handlerThread?.start()
        stackCollectHandler = Handler(handlerThread!!.looper)
    }

    /**
     * 开启掉帧检测
     */
    fun start() {
        choreographerPostFrameCallback.addFrameCallBackTimeListener(choreographerListener)
        choreographerPostFrameCallback.startPostFrameCallBack()
    }

    /**
     * 关闭掉帧检测
     */
    fun stop() {
        handlerThread?.quitSafely()
        stackCollectHandler?.removeCallbacksAndMessages(null)
        choreographerPostFrameCallback.removeFrameCallBackTimeListener(choreographerListener)
        choreographerPostFrameCallback.stopPostFrameCallBack()
    }

    private val choreographerListener =
        object : ChoreographerPostFrameCallback.FrameCallBackTimeListener {
            override fun doFrame(lastFrameCostTime: Long, frameTimeNanos: Long) {
                stackCollectHandler?.removeCallbacks(stackCollectTask)
                if (lastFrameCostTime >= blockThresholdNs && blockStackTraceMap.isNotEmpty()) {
                    // 输出堆栈信息
                    val traceList = blockStackTraceMap.values.toList()
                    traceList.forEach {
                        Log.e(TAG, "跳帧次数 = ${it.collectCount} \n ${it.stackTrace}")
                    }
                }

                stackCollectHandler?.postDelayed(stackCollectTask, COLLECT_TIME)
                blockStackTraceMap.clear()
                // 计算帧率
                if (openFrameRate) {
                    calculateFrameRate()
                }
            }

        }

    // 每隔16ms收集一次主线程的堆栈
    private val stackCollectTask = object : Runnable {
        override fun run() {
            collectCount++
            if (collectCount > 1) {
                val info =
                    BlockStackTraceInfo(ThreadManager.getJavaStackByName(Looper.getMainLooper().thread.name))
                val key = info.getMapKey()
                val value = blockStackTraceMap[key]
                if (value == null) {
                    blockStackTraceMap[key] = info
                } else {
                    value.collectCount++
                }
            }
            stackCollectHandler?.postDelayed(this, COLLECT_TIME)
        }
    }

    /**
     * 计算帧率
     */
    private fun calculateFrameRate() {
        val t = System.currentTimeMillis()
        if (nowTime == 1L) {
            nowTime = t
        }

        if (nowTime / 1000 == t / 1000) {
            frameRateCount++
        } else if (t / 1000 - nowTime / 1000 >= 1) {
            Log.d(TAG, "帧率 = ${frameRateCount} fps")
            frameRateCount = 1
            nowTime = t
        }
    }

}