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


}