package com.opt.apm.base.util

import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.ArrayMap
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * App 信息相关工具类；
 */
object ApmUtil {
    fun isValidContext(c: Context): Boolean {
        try {
            val a = c as Activity
            return !a.isFinishing
        } catch (e: Exception) {
            //
        }
        return false
    }

    fun isShowingActivity(
        context: Context,
        activityName: String
    ): Boolean {
        try {
            val activityManager = context
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val tasksInfo =
                activityManager.getRunningTasks(1)
            if (tasksInfo.size > 0) {
                // Activity位于堆栈的顶层,如果Activity的类为空则判断的是当前应用是否在前台
                val cn = tasksInfo[0].topActivity
                val topActivityClsName = cn!!.className
                if (activityName == topActivityClsName) {
                    return true
                }
            }
        } catch (e: Exception) {
            //
        }
        return false
    }

    private fun isTopActivity(
        context: Context,
        packageName: String
    ): Boolean {
        try {
            val activityManager = context
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val tasksInfo =
                activityManager.getRunningTasks(1)
            if (tasksInfo.size > 0) {
                // Activity位于堆栈的顶层,如果Activity的类为空则判断的是当前应用是否在前台
                if (packageName == tasksInfo[0].topActivity!!.packageName) {
                    return true
                }
            }
        } catch (e: Exception) {
            //
        }
        return false
    }

    private fun isForgroundApp(
        context: Context,
        pkgName: String
    ): Boolean {
        // 获取当前正在运行进程列表
        try {
            val activityManager = context
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val appProcesses =
                activityManager.runningAppProcesses ?: return false
            for (appProcess in appProcesses) {
                // 通过进程名及进程所用到的包名来进行查找
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if (appProcess.processName == pkgName || Arrays.asList(
                            *appProcess.pkgList
                        ).contains(pkgName)
                    ) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            //
        }
        return false
    }

    /**
     * 判断当前程序是否运行于前台
     */
    fun isAppRunningInForground(
        context: Context,
        pkgName: String
    ): Boolean {
        return if (Build.VERSION.SDK_INT >= 21) {//sdk是否5.0或以上
            isForgroundApp(context, pkgName)
        } else {
            isTopActivity(context, pkgName)
        }
    }

    fun getTopActivity(): Any? {
        var activityThreadClass: Class<*>? = null
        try {
            activityThreadClass = Class.forName("android.app.ActivityThread")
            val activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null)
            val activitiesField: Field = activityThreadClass.getDeclaredField("mActivities")
            activitiesField.isAccessible = true
            val activities = activitiesField.get(activityThread) as Map<*, *>
            for (activityRecord in activities.values) {
                activityRecord ?: continue
                val activityRecordClass: Class<*> = activityRecord.javaClass
                val pausedField: Field = activityRecordClass.getDeclaredField("paused")
                pausedField.isAccessible = true
                if (!pausedField.getBoolean(activityRecord)) {
                    val activityField: Field = activityRecordClass.getDeclaredField("activity")
                    activityField.isAccessible = true
                    return activityField.get(activityRecord)
                }
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        return null
    }


    fun getTopActivityName(): String? {
        val start = System.currentTimeMillis()
        try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null)
            val activitiesField = activityThreadClass.getDeclaredField("mActivities")
            activitiesField.isAccessible = true
            val activities: Map<Any, Any> =
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    activitiesField[activityThread] as HashMap<Any, Any>
                } else {
                    activitiesField[activityThread] as ArrayMap<Any, Any>
                }
            if (activities.isEmpty()) {
                return null
            }
            for (activityRecord in activities.values) {
                val activityRecordClass: Class<*> = activityRecord.javaClass
                val pausedField = activityRecordClass.getDeclaredField("paused")
                pausedField.isAccessible = true
                if (!pausedField.getBoolean(activityRecord)) {
                    val activityField = activityRecordClass.getDeclaredField("activity")
                    activityField.isAccessible = true
                    val activity = activityField[activityRecord] as Activity
                    return activity.javaClass.name
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 判断是否处于锁屏状态
     */
    fun isScreenLocked(context: Context): Boolean {
        try {
            val keyguardManager =
                context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            return keyguardManager.inKeyguardRestrictedInputMode()
        } catch (e: Exception) {
            //
        }
        return false
    }

    /**
     * <br></br>功能简述:获取当前应用包名
     */
    fun getPackageName(context: Context): String? {
        return context.packageName
    }

    /**
     * 获取元数据值
     */
    fun getMetaValue(
        context: Context?,
        metaKey: String?
    ): String? {
        var metaData: Bundle? = null
        var apiKey: String? = null
        if (context == null || metaKey == null) {
            return null
        }
        try {
            val ai = context.packageManager
                .getApplicationInfo(
                    context.packageName,
                    PackageManager.GET_META_DATA
                )
            if (null != ai) {
                metaData = ai.metaData
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return apiKey
    }

    /**
     * @return 获得当前进程名
     */
    fun getCurProcessName(context: Context): String? {
        try {
            val pid = Process.myPid()
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            if (activityManager != null) {
                val processInfoList =
                    activityManager.runningAppProcesses
                if (processInfoList != null) {
                    for (appProcess in processInfoList) {
                        if (appProcess.pid == pid) {
                            return appProcess.processName
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun isProcessExist(context: Context, processName: String): Boolean {
        try {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            if (activityManager != null) {
                val processInfoList =
                    activityManager.runningAppProcesses
                if (processInfoList != null) {
                    for (appProcess in processInfoList) {
                        if (appProcess.processName == processName) {
                            return true
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun getAppIcon(context: Context): Bitmap? {
        val bitmapDrawable =
            context.applicationInfo.loadIcon(context.packageManager) as BitmapDrawable
        return bitmapDrawable.bitmap
    }

    fun getAppName(context: Context): CharSequence {
        return context.packageManager.getApplicationLabel(context.applicationInfo)
    }

    //========================================引导去设置=======================================by xf
    private const val SCHEME = "package"

    /**
     * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
     */
    private const val APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName"

    /**
     * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
     */
    private const val APP_PKG_NAME_22 = "pkg"

    /**
     * InstalledAppDetails所在包名
     */
    private const val APP_DETAILS_PACKAGE_NAME = "com.android.settings"

    /**
     * InstalledAppDetails类名
     */
    private const val APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails"

    /**
     * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level
     * 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
     *
     * @param packageName 应用程序的包名
     */
    fun showInstalledAppDetails(
        context: Context,
        packageName: String?
    ) {
        val intent = Intent()
        val apiLevel = Build.VERSION.SDK_INT
        if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts(SCHEME, packageName, null)
            intent.data = uri
        } else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
            // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
            val appPkgName =
                if (apiLevel == 8) APP_PKG_NAME_22 else APP_PKG_NAME_21
            intent.action = Intent.ACTION_VIEW
            intent.setClassName(
                APP_DETAILS_PACKAGE_NAME,
                APP_DETAILS_CLASS_NAME
            )
            intent.putExtra(appPkgName, packageName)
        }
        context.startActivity(intent)
    }
    //========================================引导去设置=======================================by xf
}