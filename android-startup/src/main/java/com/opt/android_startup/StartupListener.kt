package com.opt.android_startup

import com.opt.android_startup.model.CostTimesModel

interface StartupListener {
    fun onCompleted(totalMainThreadCostTime: Long, costTimesModels: List<CostTimesModel>)
}