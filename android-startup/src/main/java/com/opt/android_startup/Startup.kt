package com.opt.android_startup

import android.content.Context
import com.opt.android_startup.dispatcher.Dispatcher
import com.opt.android_startup.exector.StartupExecutor

interface Startup<T> : Dispatcher, StartupExecutor {

    /**
     * 执行任务
     */
    fun create(context: Context): T?

    /**
     * 依赖的所有任务
     */
    fun dependencies(): List<Class<out Startup<*>>>?

    /**
     * 依赖所有任务的数量
     */
    fun getDependenciesCount(): Int

    /**
     * 依赖的任务完成
     */
    fun onDependenciesCompleted(startup: Startup<*>, result: Any?)


    fun onDispatch()

    fun manualDispatch(): Boolean

    fun registerDispatcher(dispatcher: Dispatcher)

}