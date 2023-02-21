package com.opt.apm.base.util

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import java.util.HashSet

object HandlerThreadUtil {

    private const val TAG = "HandlerThread"

    private const val HANDLER_THREAD_NAME = "handler_thread_name"


    @Volatile
    private var defaultHandler: Handler? = null

    @JvmStatic
    @Volatile
    var defaultHandlerThread: HandlerThread? = null
        get() {
            synchronized(HandlerThreadUtil::class.java) {
                if (null == field) {
                    field = HandlerThread(HANDLER_THREAD_NAME)
                    field?.let {
                        it.start()
                        defaultHandler = Handler(it.looper)
                        Log.d(
                            TAG,
                            "create default handler thread, we should use these thread norma"
                        )
                    }
                }
            }
            return field
        }
        private set

    @JvmStatic
    @Volatile
    var defaultMainHandler = Handler(Looper.getMainLooper())

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


    @JvmStatic
    fun getDefaultHandler(): Handler? {
        if (defaultHandler == null) {
            defaultHandlerThread
        }
        return defaultHandler
    }

    @JvmStatic
    fun getNewHandlerThread(name: String?, priority: Int): HandlerThread {
        val i = handlerThreads.iterator()
        while (i.hasNext()) {
            val element = i.next()
            if (!element.isAlive) {
                i.remove()
                Log.d(TAG, "warning: remove dead handler thread with name:$name")
            }
        }
        val handlerThread = HandlerThread(name)
        handlerThread.priority = priority
        handlerThread.start()
        handlerThreads.add(handlerThread)
        Log.d(
            TAG,
            "warning: create new handler thread with name $name, alive thread size:${handlerThreads.size}"
        )
        return handlerThread
    }

}