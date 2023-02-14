import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.opt.apm.base.util.ApmUtil
import com.opt.apm.base.Plugin
import java.util.HashSet

/**
 * 车小蜂性能监控Apm
 */
object IYourAutoBeeApm {

    var plugins: HashSet<Plugin> = HashSet()

    fun startAllPlugins(application: Application?) {
        application ?: return
        registerLifecycleCallbacks(application)
        for (plugin in plugins) {
            plugin.start(application)
        }
    }

    fun stopAllPlugins() {
        for (plugin in plugins) {
            plugin.stop()
        }
    }

    private fun registerLifecycleCallbacks(application: Application) {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                if (!ApmUtil.isAppRunningInForground(application, application.packageName)) {
                    stopAllPlugins()
                }
            }
        })
    }


}