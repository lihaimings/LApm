package com.opt.android_startup.annotation

@MustBeDocumented
@Retention
@Target(AnnotationTarget.CLASS)
annotation class MultipleProcess(vararg val process: String)