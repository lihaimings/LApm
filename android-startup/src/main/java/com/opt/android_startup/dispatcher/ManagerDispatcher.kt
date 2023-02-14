package com.opt.android_startup.dispatcher

import com.opt.android_startup.Startup
import com.opt.android_startup.model.StartupSortStore

interface ManagerDispatcher {

    /**
     * dispatch prepare
     */
    fun prepare()

    /**
     * dispatch startup to executing.
     */
    fun dispatch(startup: Startup<*>, sortStore: StartupSortStore)

    /**
     * notify children when dependency startup completed.
     */
    fun notifyChildren(dependencyParent: Startup<*>, result: Any?, sortStore: StartupSortStore)
}