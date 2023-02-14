package com.opt.lapm.block

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.util.Printer
import com.opt.lapm.util.ThreadManager

/**
 * 慢函数的检测，实现的原理是基于 Looper 的 Printer
 */
class SlowMethodMonitor {

    /***
     * 执行超过200ms，定义为慢函数
     */
    private val TIME_BLOCK = 200
    private val START_STR = ">>>>>"
    private val START_END = "<<<<<"
    private val TAG = "SlowMethodMonitor"

    private var startTime = 0L

    /**
     * 收集掉帧堆栈
     */
    private var handlerThread: HandlerThread? = null
    private var stackCollectHandler: Handler? = null
    private var blockStackTraceMap = HashMap<String, BlockStackTraceInfo>()
    private val COLLECT_TIME: Long = 80L
    private var collectCount = 0

    init {
        handlerThread = HandlerThread("SlowMethodMonitor")
        handlerThread?.start()
        stackCollectHandler = Handler(handlerThread!!.looper)
    }

    private val PRINTER = object : Printer {
        override fun println(p0: String?) {
            if (p0?.startsWith(START_STR) == true) {
                startTime = System.currentTimeMillis()
                stackCollectHandler?.postDelayed(stackCollectTask, COLLECT_TIME)
            } else if (p0?.startsWith(START_END) == true) {
                stackCollectHandler?.removeCallbacks(stackCollectTask)
                val executeTime = System.currentTimeMillis() - startTime
                if (executeTime >= TIME_BLOCK) {
                    val traceList = blockStackTraceMap.values.toList()
                    traceList.forEach {
                        Log.e(TAG, "慢函数 \n ${it.stackTrace}")
                    }
                }
                blockStackTraceMap.clear()
            }
        }
    }

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

    fun start() {
        Looper.getMainLooper().setMessageLogging(PRINTER)
    }

    fun stop() {
        Looper.getMainLooper().setMessageLogging(null)
        handlerThread?.quitSafely()
        stackCollectHandler?.removeCallbacksAndMessages(null)
    }


}