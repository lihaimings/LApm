package com.opt.lapm.block

class BlockStackTraceInfo(val stackTrace: String = "", var collectCount: Int = 1) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BlockStackTraceInfo

        if (stackTrace != other.stackTrace) return false

        return true
    }

    override fun hashCode(): Int {
        return stackTrace.hashCode()
    }


    fun getMapKey() = hashCode().toString()
}