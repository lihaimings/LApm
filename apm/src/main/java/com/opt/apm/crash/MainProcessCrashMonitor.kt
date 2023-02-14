package com.opt.apm.crash

import android.app.Application
import android.content.Intent
import android.os.Process
import android.util.Log
import kotlin.system.exitProcess

/**
 * 主线程崩溃处理
 */
object MainProcessCrashMonitor : Thread.UncaughtExceptionHandler {

    private var application: Application? = null
    private var listener: CrashListener? = null
    private var isDefaultHandle: Boolean = true
    private var defaultHandle: Thread.UncaughtExceptionHandler? = null


    fun start(listener: CrashListener?) {
        this.application = application
        if (isDefaultHandle) {
            val handler = Thread.getDefaultUncaughtExceptionHandler()
            if (handler::class.java.name.contains("com.android.internal.os.RuntimeInit")) {
                defaultHandle = handler
            }
        }
        this.listener = listener
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, e: Throwable) {
        e.printStackTrace()
        listener?.uncaughtException(thread, e)
    }

    interface CrashListener {
        fun uncaughtException(thread: Thread, e: Throwable)
    }


}