package com.opt.apm.util

import java.lang.StringBuilder

object SizeUtil {

    fun sizeValueToString(number: Long, outBuilder: StringBuilder?): String? {
        var outBuilder = outBuilder
        if (outBuilder == null) {
            outBuilder = StringBuilder(32)
        }
        var result = number.toFloat()
        var suffix = ""
        if (result > 900) {
            suffix = "KB"
            result /= 1024
        }
        if (result > 900) {
            suffix = "MB"
            result /= 1024
        }
        if (result > 900) {
            suffix = "GB"
            result /= 1024
        }
        if (result > 900) {
            suffix = "TB"
            result /= 1024
        }
        if (result > 900) {
            suffix = "PB"
            result /= 1024
        }
        val value: String = if (result < 1) {
            String.format("%.2f", result)
        } else if (result < 10) {
            String.format("%.1f", result)
        } else if (result < 100) {
            String.format("%.0f", result)
        } else {
            String.format("%.0f", result)
        }
        outBuilder.append(value)
        outBuilder.append(suffix)
        return outBuilder.toString()
    }

}