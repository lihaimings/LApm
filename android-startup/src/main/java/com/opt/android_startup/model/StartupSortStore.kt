package com.opt.android_startup.model

import com.opt.android_startup.Startup

data class StartupSortStore(
    val result: MutableList<Startup<*>>,
    val startupMap: Map<String, Startup<*>>,
    val startupChildrenMap: Map<String, MutableList<String>>
)
