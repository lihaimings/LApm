package com.opt.apm.base.util

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import java.util.HashSet

object HandlerThreadUtil {

    @Volatile
    private var defaultHandlerThread: HandlerThread? = null

    @Volatile
    private var defaultHandler: Handler? = null

    @Volatile
    private var defaultMainHandler = Handler(Looper.getMainLooper())


    private val handlerThreads = HashSet<HandlerThread>()

    @JvmStatic
    fun getDefaultHandler(): Handler? {
        if (defaultHandler == null) {
            defaultHandlerThread
        }
        return defaultHandler
    }

    @JvmStatic
    fun getNewHandlerThread(name: String?, priority: Int = Thread.NORM_PRIORITY): HandlerThread {
        val i = handlerThreads.iterator()
        while (i.hasNext()) {
            val element = i.next()
            if (!element.isAlive) {
                i.remove()
            }
        }
        val handlerThread = HandlerThread(name)
        handlerThread.priority = priority
        handlerThread.start()
        handlerThreads.add(handlerThread)
        return handlerThread
    }


}