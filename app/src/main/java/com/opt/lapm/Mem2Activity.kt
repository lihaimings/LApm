package com.opt.lapm

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.opt.lapm.ApmApplication
import com.opt.lapm.R
import com.opt.lapm.databinding.ActivityMemBinding

class Mem2Activity : AppCompatActivity() {

    private var dataBing: ActivityMemBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBing = DataBindingUtil.setContentView(this, R.layout.activity_mem)
        dataBing?.memBtn?.setOnClickListener {
            finish()
        }

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}