package com.opt.lapm.util

import android.os.HandlerThread
import android.os.Looper
import java.lang.StringBuilder

object ThreadManager {

    /**
     * 获取java线程的堆栈
     */
    fun getJavaStackByName(threadName: String?): String {
        val thread = getThreadByName(threadName) ?: return "[]"
        val stackArray = thread.stackTrace ?: return "[]"

        val b = StringBuilder()
        for (i in stackArray.indices) {
            b.append(stackArray[i])
            b.append("\n")
        }
        return b.toString()
    }


    /**
     * 根据线程名获取线程
     */
    fun getThreadByName(threadName: String?): Thread? {
        threadName ?: return null
        var __tmp: Thread? = null
        try {
            if (threadName == "main") {
                return Looper.getMainLooper().thread
            }
            val threadSet: Set<Thread> = Thread.getAllStackTraces().keys
            val threadArray = threadSet.toTypedArray()
            for (i in threadArray.indices) {
                if (threadArray[i].name == threadName) {
                    __tmp = threadArray[i]
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return __tmp
    }
}