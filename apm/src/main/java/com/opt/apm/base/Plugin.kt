package com.opt.apm.base

import android.app.Application

interface Plugin {

    fun start(application: Application?)

    fun stop()
}