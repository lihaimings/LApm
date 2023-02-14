package com.opt.lapm.util

import android.app.Application
import android.content.Context

class TestApplication : Application() {

    var context: Context? = null

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

}