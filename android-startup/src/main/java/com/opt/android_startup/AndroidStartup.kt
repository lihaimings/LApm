package com.opt.android_startup

import com.opt.android_startup.dispatcher.Dispatcher
import com.opt.android_startup.exector.ExecutorManager
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor


abstract class AndroidStartup<T> : Startup<T> {

    private val waitCountDown by lazy { CountDownLatch(getDependenciesCount()) }

    private val observers by lazy { mutableListOf<Dispatcher>() }

    override fun toWait() {
        try {
            waitCountDown.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun toNotify() {
        waitCountDown.countDown()
    }

    override fun createException(): Executor {
        return ExecutorManager.instance.ioExecutor
    }

    override fun dependencies(): List<Class<out Startup<*>>>? {
        return null
    }

    override fun getDependenciesCount(): Int {
        return dependencies()?.size ?: 0
    }

    override fun onDependenciesCompleted(startup: Startup<*>, result: Any?) {

    }

    override fun registerDispatcher(dispatcher: Dispatcher) {
        observers.add(dispatcher)
    }

    override fun manualDispatch(): Boolean {
        return false
    }

    override fun onDispatch() {
        observers.forEach {
            it.toNotify()
        }
    }


}