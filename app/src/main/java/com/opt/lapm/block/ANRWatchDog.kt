package com.opt.lapm.block

import android.app.ActivityManager
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * ANR 异常信息监控
 */
class ANRWatchDog : Runnable {

    /**
     * anr 检测间隔时间
     */
    private val CHECK_INTERVAL: Long = 5000L

    private var anrHandler: Handler? = null
    private var mainHandlerChecker: HandlerChecker? = null
    private val TAG = "ANRWatchDog"
    private var context: Context? = null
    private var executor: ExecutorService? = null

    constructor(context: Context?) {
        context ?: return
        val handlerThread = HandlerThread("ANR_WATCHDOG")
        handlerThread.start()
        anrHandler = Handler(handlerThread.looper)
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandlerChecker = HandlerChecker(mainHandler)
        this.context = context.applicationContext
        executor = Executors.newCachedThreadPool()
    }

    fun start() {
        anrHandler?.post(this)
    }

    fun stop() {
        anrHandler?.removeCallbacksAndMessages(null)
        mainHandlerChecker?.stop()
        anrHandler = null
        mainHandlerChecker = null
        this.context = null
    }

    // 往主线程中插入一条信息，然后阻塞5s,查看主线程是否已经执行消息，没有执行则说明阻塞
    override fun run() {
        synchronized(this) {
            // 往主线程中插入一条信息
            mainHandlerChecker?.scheduleCheckLocked()

            val start = SystemClock.uptimeMillis()
            var timeout = CHECK_INTERVAL
            while (timeout > 0) {
                try {
                    Thread.sleep(timeout)
                } catch (e: InterruptedException) {
                    e.printStackTrace()

                }
                timeout = CHECK_INTERVAL - (SystemClock.uptimeMillis() - start)
            }

            val isBlocked = mainHandlerChecker?.isBlocked()
            if (isBlocked == true) {
                Log.e(TAG, "可能存在ANR")
                mainHandlerChecker?.restoreBlocked()
                doubtANR()
            }
            anrHandler?.post(this)
        }
    }

    /**
     * 怀疑有ANR
     */
    private fun doubtANR() {
        executor?.execute {
            val processErrorStateInfo = getANRInfo()
            processErrorStateInfo?.let {
                val runtimeException = RuntimeException(it.shortMsg)
                runtimeException.stackTrace = mainHandlerChecker?.getHandlerThread()?.stackTrace
                runtimeException.printStackTrace()
            }
        }
    }

    private fun getANRInfo(): ActivityManager.ProcessErrorStateInfo? {
        try {
            val sleepTime = 500L
            val loop = 20
            var times = 0
            do {
                val activityManager =
                    context?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
                val errorStateInfo = activityManager?.processesInErrorState
                errorStateInfo?.let {
                    it.forEach { stateInfo ->
                        if (stateInfo.condition == ActivityManager.ProcessErrorStateInfo.NOT_RESPONDING) {
                            return stateInfo
                        }
                    }
                }
                Thread.sleep(sleepTime)
            } while (times++ < loop)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    class HandlerChecker : Runnable {
        private var handler: Handler? = null
        private var isCompleted = true

        constructor(handler: Handler?) {
            handler ?: return
            this.handler = handler
        }

        override fun run() {
            isCompleted = true
        }

        fun scheduleCheckLocked() {
            if (!isCompleted) {
                return
            }
            isCompleted = false
            handler?.postAtFrontOfQueue(this)
        }

        fun isBlocked(): Boolean {
            return !isCompleted
        }

        fun restoreBlocked() {
            isCompleted = true
        }

        fun getHandlerThread(): Thread? {
            return handler?.looper?.thread
        }

        fun stop() {
            handler?.removeCallbacksAndMessages(null)
        }
    }

}