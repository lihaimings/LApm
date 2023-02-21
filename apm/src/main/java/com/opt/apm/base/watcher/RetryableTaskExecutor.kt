package com.opt.apm.base.watcher

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import com.opt.apm.base.util.HandlerThreadUtil

class RetryableTaskExecutor(delayMillis: Long, handlerThread: HandlerThread?) {
    private val backgroundHandler: Handler
    private val mainHandler: Handler
    var delayMills: Long


    init {

        backgroundHandler = Handler(
            handlerThread?.looper
                ?: HandlerThreadUtil.getNewHandlerThread("RetryableTaskExecutor").looper
        )
        mainHandler = Handler(Looper.getMainLooper())
        this.delayMills = delayMillis
    }

    fun executeInMainThread(task: RetryableTask) {
        postToMainThreadWithDelay(task, 0)
    }

    fun executeInBackground(task: RetryableTask) {
        postToBackgroundWithDelay(task, 0)
    }

    private fun postToMainThreadWithDelay(task: RetryableTask, failedAttempts: Int) {
        mainHandler.postDelayed({
            val status = task.execute()
            if (status == RetryableTask.Status.RETRY) {
                postToMainThreadWithDelay(task, failedAttempts + 1)
            }
        }, delayMills)
    }

    private fun postToBackgroundWithDelay(task: RetryableTask, failedAttempts: Int) {
        backgroundHandler.postDelayed({
            val status = task.execute()
            if (status == RetryableTask.Status.RETRY) {
                postToBackgroundWithDelay(task, failedAttempts + 1)
            }
        }, delayMills)
    }

    fun clearTasks() {
        backgroundHandler.removeCallbacksAndMessages(null)
        mainHandler.removeCallbacksAndMessages(null)
    }

    fun quit() {
        clearTasks()
    }


    interface RetryableTask {
        enum class Status {
            DONE, RETRY
        }

        fun execute(): Status
    }


}