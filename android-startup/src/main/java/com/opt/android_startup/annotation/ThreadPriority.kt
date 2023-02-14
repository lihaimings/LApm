package com.opt.android_startup.annotation

import android.os.Process

@MustBeDocumented
@Retention
@Target(AnnotationTarget.CLASS)
annotation class ThreadPriority(val priority: Int = Process.THREAD_PRIORITY_DEFAULT) {
}