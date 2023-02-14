package com.opt.apm.block

import android.os.Build
import android.os.Looper
import android.os.MessageQueue
import android.os.SystemClock
import android.util.Printer
import com.opt.apm.base.reflect.ReflectUtils
import java.lang.Exception
import java.lang.RuntimeException


object LooperMonitor {

    /**
     * 主线程的Looper
     */
    private var defaultMainLooper = Looper.getMainLooper()
    private var listeners: MutableList<LooperDispatchListener?> = mutableListOf()
    private var isReflectLoggingError = false
    private var printer: LooperPrinter? = null
    private var isStarted = false
    private val TAG = "LooperMonitor"
    private var lastCheckPrinterTime: Long = 0
    private const val CHECK_TIME = 60 * 1000L

    /**
     * 通过监听去分发的Looper的消息的开始和结束
     * 注册监听，如果有监听则自动给Looper设置Printer
     */
    fun addDispatchListener(listener: LooperDispatchListener?) {
        listener ?: return
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
        // 如果监听>0 ,则调用 start() 方法
        if (listeners.size > 0 && !isStarted) {
            start()
        }
    }

    /**
     * 移除监听，没有监听自动结束对移除Looper的Printer
     */
    fun removeDispatchListener(listener: LooperDispatchListener?) {
        listener ?: return
        if (listeners.contains(listener)) {
            listeners.remove(listener)
        }
        // 没有监听自动结束对移除Looper的Printer
        if (listeners.size <= 0 && isStarted) {
            stop()
        }
    }

    private fun start() {
        isStarted = true
        // 设置Looper的Printer
        resetPrinter()
        // 增加IdleHandler,避免Printer被覆盖
        addIdleHandler()
    }

    private fun stop() {
        isStarted = false
        if (printer != null) {
            // 移除监听
            synchronized(listeners) { listeners.clear() }
            // Printer设置成原来的Printer
            defaultMainLooper.setMessageLogging(printer?.origin)
            removeIdleHandler()
            defaultMainLooper = null
            printer = null
        }
    }

    /**
     * 检测Looper原本是否有Printer，如果有不要将原本的Printer覆盖,然后设置本次的Printer
     */
    private fun resetPrinter() {
        var originPrinter: Printer? = null
        try {
            if (!isReflectLoggingError) {
                // 通过反射，拿到Lopper的mLogging变量
                originPrinter =
                    ReflectUtils.get(defaultMainLooper::class.java, "mLogging", defaultMainLooper)
                // 检测Printer是否被后续的Printer覆盖
                if (originPrinter == printer && printer != null) {
                    return
                }
                if (originPrinter != null && printer != null) {
                    if (originPrinter.javaClass.name == printer?.javaClass?.name) {
                        return
                    }
                }
            }
        } catch (e: Exception) {
            isReflectLoggingError = true
            e.printStackTrace()
        }

        // 设置本次的Printer，并适配原本的Printer
        printer = LooperPrinter(originPrinter)
        defaultMainLooper.setMessageLogging(printer)
    }

    /**
     * 在MessageQueue空闲的时候,执行Looper的Printer的检查，避免被后续的Printer覆盖
     */
    private fun addIdleHandler() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            defaultMainLooper.queue.addIdleHandler(idleHandler)
        } else {
            try {
                val queue: MessageQueue? =
                    ReflectUtils.get(defaultMainLooper::class.java, "mQueue", defaultMainLooper)
                queue?.addIdleHandler(idleHandler)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun removeIdleHandler() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            defaultMainLooper.queue.removeIdleHandler(idleHandler)
        } else {
            try {
                val queue: MessageQueue? =
                    ReflectUtils.get(defaultMainLooper::class.java, "mQueue", defaultMainLooper)
                queue?.removeIdleHandler(idleHandler)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 每隔60s，检测一下本次的Printer是否被别的Printer覆盖
     */
    private val idleHandler = MessageQueue.IdleHandler {
        if (SystemClock.uptimeMillis() - lastCheckPrinterTime > CHECK_TIME) {
            resetPrinter()
            lastCheckPrinterTime = SystemClock.uptimeMillis()
        }
        true
    }


    /**
     * 新的Printer，要适配原本的已有的Printer
     */
    class LooperPrinter(var origin: Printer?) : Printer {
        private var isValid = false

        override fun println(x: String?) {
            // 给原本的Printer调用
            if (origin != null) {
                origin?.println(x)
                if (origin == this) {
                    throw RuntimeException("$TAG origin == this")
                }
            }

            // 分发给Listener
            isValid = x?.getOrNull(0) == '>' || x?.getOrNull(0) == '<'
            if (isValid) {
                dispatch(x?.getOrNull(0) == '>', x ?: "")
            }

        }

    }

    /**
     * 分发给Listener
     */
    private fun dispatch(isBegin: Boolean, log: String) {
        synchronized(listeners) {
            listeners.forEach {
                it ?: return
                if (isBegin) {
                    if (!it.isHasDispatchStart) {
                        it.onDispatchStart(log)
                    }
                } else {
                    if (it.isHasDispatchStart) {
                        it.onDispatchEnd(log)
                    }
                }
            }
        }
    }


    /**
     * 分发的接口
     */
    abstract class LooperDispatchListener {
        var isHasDispatchStart = false

        abstract fun dispatchStart()

        abstract fun dispatchEnd()

        fun onDispatchStart(x: String?) {
            isHasDispatchStart = true
            dispatchStart()
        }

        fun onDispatchEnd(x: String?) {
            isHasDispatchStart = false
            dispatchEnd()
        }
    }


}