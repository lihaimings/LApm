package com.opt.lapm

import android.app.ActivityManager
import android.content.Intent
import android.graphics.Color
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.opt.android_startup.time.AppMethodBeat
import com.opt.apm.util.SizeUtil
import com.opt.lapm.databinding.ActivityMainBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.lang.StringBuilder
import java.lang.ref.WeakReference
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.security.AccessController
import java.security.PrivilegedAction
import java.security.PrivilegedExceptionAction
import java.util.*
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.LinkedBlockingQueue

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "MainActivity"
    }

    private var dataBinding: ActivityMainBinding? = null
    private val fileNameList: LinkedBlockingQueue<String> by lazy { LinkedBlockingQueue() }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // 阻塞测试
        dataBinding?.blockBtn?.setOnClickListener {
            Thread.sleep(3000)
        }

        // 崩溃测试
        dataBinding?.crashBtn?.setOnClickListener {
            "jkhsfd".toInt()
        }

        // 内存泄漏测试
        dataBinding?.memBtn?.setOnClickListener {
            startActivity(Intent(this, MemActivity::class.java))
        }


        AppMethodBeat.at(this, false)


        dataBinding?.memBtn?.postDelayed({
            memoryInfoByActivityManager()
            memoryInfoByDebug()
            memoryInfoByAdb()
        }, 3000)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    private fun memoryInfoByActivityManager() {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as? ActivityManager

        val memoryInfo = ActivityManager.MemoryInfo()

        activityManager?.getMemoryInfo(memoryInfo)

        Log.d(TAG, "系统剩余内存= ${SizeUtil.sizeValueToString(memoryInfo.availMem, null)}")
        Log.d(TAG, "系统是否低内存= ${memoryInfo.lowMemory}")
        Log.d(TAG, "系统低内存的值= ${SizeUtil.sizeValueToString(memoryInfo.threshold, null)}")
        Log.d(TAG, "系统低总内存= ${SizeUtil.sizeValueToString(memoryInfo.totalMem, null)}")
        Log.d(TAG, "---------> >>>>>>>>>>")

        getRunningAppProcess(activityManager)
    }

    private fun getRunningAppProcess(activityManager: ActivityManager?) {
        activityManager ?: return

        val appProcessList = activityManager.runningAppProcesses
        Log.d(
            TAG, "appProcessList size := ${appProcessList?.size ?: 0}"
        )
        appProcessList?.forEach {
            val pid = it.pid
            val uid = it.uid
            val processName = it.processName
            val myMemPid = IntArray(1) { pid }
            val memoryInfo = activityManager.getProcessMemoryInfo(myMemPid)
            val memSize = memoryInfo[0].dalvikPrivateDirty
            Log.d(
                TAG,
                "包名:= ${processName} 内存大小 = ${
                    SizeUtil.sizeValueToString(
                        memSize.toLong(),
                        null
                    )
                } , pid = ${pid}, uid = ${uid}"
            )

            val packageList = it.pkgList
            Log.i(TAG, "process id is = ${pid} has ${packageList.size}")
            packageList?.forEach { packName ->
                Log.d(TAG, "packName ${packName} in process id is ${pid}")
            }

        }

    }

    private fun memoryInfoByDebug() {
        val memoryInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memoryInfo)
        Debug.getNativeHeapSize()
        Debug.getNativeHeapAllocatedSize()
        Debug.getNativeHeapFreeSize()
    }

    private fun memoryInfoByAdb() {
        try {
            val adb = "dumpsys meminfo com.opt.lapm"
            val process = Runtime.getRuntime().exec(adb)
        } catch (e: Exception) {
            Log.i(TAG, "${e.toString()}")
            e.printStackTrace()
        }
    }
}