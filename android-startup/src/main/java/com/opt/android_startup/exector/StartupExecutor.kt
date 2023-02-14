package com.opt.android_startup.exector

import java.util.concurrent.Executor


interface StartupExecutor {
    fun createException(): Executor
}