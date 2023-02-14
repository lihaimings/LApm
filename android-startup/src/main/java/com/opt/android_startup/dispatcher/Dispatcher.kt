package com.opt.android_startup.dispatcher

interface Dispatcher {

    /**
     * 是否运行在主线程
     */
    fun callCreateOnMainThread(): Boolean

    /**
     * 主线程是否需要等待此任务完成
     */
    fun waitOnMainThread(): Boolean

    /**
     * 等待依赖的任务完成
     */
    fun toWait()

    /**
     * 通知依赖的任务已经完成
     */
    fun toNotify()

}