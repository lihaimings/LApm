package com.opt.lapm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.opt.lapm.ApmApplication
import com.opt.lapm.R
import com.opt.lapm.databinding.ActivityMemBinding

class MemActivity : AppCompatActivity() {

    private var dataBing: ActivityMemBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBing = DataBindingUtil.setContentView(this, R.layout.activity_mem)
        (application as ApmApplication).leakedViews.add(dataBing?.memBtn)
        dataBing?.memBtn?.setOnClickListener {
            finish()
        }
        dataBing?.btn?.setOnClickListener {
            startActivity(Intent(this, Mem2Activity::class.java))
        }

    }
}