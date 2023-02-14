package com.opt.lapm.sdk

import android.content.Context
import com.opt.android_startup.AndroidStartup

class SampleFirstStartup : AndroidStartup<String>() {
    override fun create(context: Context): String? {
        return this.javaClass.simpleName
    }

    override fun callCreateOnMainThread(): Boolean {
        return true
    }

    override fun waitOnMainThread(): Boolean {
        return false
    }
}