package com.opt.apm.memory

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

class KeyedWeakReference(
    referent: Any,
    val key: String,
    val description: String,
    referenceQueue: ReferenceQueue<Any>
) : WeakReference<Any>(referent, referenceQueue) {

}