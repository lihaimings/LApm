package com.opt.lapm

import android.util.Log

object TimeStatisticsUtil {
    private var startMap: HashMap<String, Long> = HashMap()

    fun startTime(string: String) {
        startMap[string] = System.currentTimeMillis()
    }

    fun endTime(string: String) {
        if (startMap.containsKey(string)) {
            Log.d(
                "TimeStatisticsUtil",
                " $string = ${System.currentTimeMillis() - (startMap[string] ?: 0)}"
            )
            startMap.remove(string)
        }
    }

}