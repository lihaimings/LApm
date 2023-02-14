object Iyourcar {
    // IM 通信
    object PushSDK {
        const val version = "1.1.7"
        const val debug = "com.iyourcar.android:pushsdk-debug:$version"
        const val preRelease = "com.iyourcar.android:pushsdk-prerelease:$version"
        const val release = "com.iyourcar.android:pushsdk-release:$version"
    }

    val pushsdk = PushSDK

    // 埋点SDK
    object DataTracker {
        const val client = "com.iyourcar:dvt-android-client:3.1.4"
        const val library = "com.iyourcar:dvt-android-library:3.2.0"
//        const val gradlePlugin = "com.iyourcar:dvt-gradle-plugin:3.0.0"
    }

    val dataTracker = DataTracker

    object H5 {
        object NewsDetail {
            const val debug = "com.iyourcar.android.h5:news-dev:1.1.0"
            const val publish = "com.iyourcar.android.h5:news-release:1.1.0"
            const val release = "com.iyourcar.android.h5:news-prod:1.1.3"
        }

        val newsDetail = NewsDetail

        object IM {
            const val debug = "com.iyourcar.android.h5:im-dev:1.4.4"
            const val publish = "com.iyourcar.android.h5:im-release:1.4.4"
            const val release = "com.iyourcar.android.h5:im-prod:1.5.6"
        }

        val im = IM

        object AutobeeNews {
            const val debug = "com.iyourcar.android.h5:autobee_news-dev:0.0.5"
            const val publish = "com.iyourcar.android.h5:autobee_news-release:0.0.1"
            const val release = "com.iyourcar.android.h5:autobee_news-prod:0.0.1"
        }

        val autobeeNews = AutobeeNews
    }

    val h5 = H5

    val profilerLibrary = "com.iyourcar.android.plugin.profiler:library:0.1.7"

    val panoramaNative = "com.iyourcar.android:panorama-kotlin:1.0.3"
}