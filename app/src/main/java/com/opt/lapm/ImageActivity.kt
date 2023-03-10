package com.opt.lapm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.opt.lapm.ApmApplication
import com.opt.lapm.R
import com.opt.lapm.databinding.ActivityImageBinding
import com.opt.lapm.databinding.ActivityMemBinding

class ImageActivity : AppCompatActivity() {

    private var dataBing: ActivityImageBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBing = DataBindingUtil.setContentView(this, R.layout.activity_image)

        dataBing?.img?.setImageDrawable(resources.getDrawable(R.mipmap.wms_startup))
    }
}