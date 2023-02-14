
object AndroidX {
    const val appcompat = "androidx.appcompat:appcompat:1.0.0"
    // core-ktx 不能升太高，fragmentKtx 依赖的是旧版本
    const val coreKtx = "androidx.core:core-ktx:1.2.0"
    const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.1"
    const val paging = "androidx.paging:paging-runtime-ktx:3.0.0-beta03"
    const val viewPager2 = "androidx.viewpager2:viewpager2:1.0.0"
    const val cardview = "androidx.cardview:cardview:1.0.0"
    // 任务调度器
    const val workManager = "androidx.work:work-runtime-ktx:2.7.1"

    val activity = Activity

    object Activity {
        private const val activity_version = "1.2.2"
        const val activity = "androidx.activity:activity:$activity_version"
        const val activityKtx = "androidx.activity:activity-ktx:$activity_version"
    }

    val fragment = Fragment

    object Fragment {
        private const val fragment_version = "1.3.3"
        const val fragment = "androidx.fragment:fragment:$fragment_version"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:$fragment_version"
        const val fragmentTesting = "androidx.fragment:fragment-testing:$fragment_version"
    }

    val lifecyle = Lifecycle

    object Lifecycle {
        private const val lifecycle_version = "2.3.1"
        const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version"
        const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"
        const val extensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
        const val common = "androidx.lifecycle:lifecycle-common-java8:2.3.1"
    }

    val navigation = Navigation

    object Navigation {
        private const val navigation_version = "2.4.0-beta01"
        const val navigationFragmentKtx =
            "androidx.navigation:navigation-fragment-ktx:$navigation_version"
        const val navigationUiKtx = "androidx.navigation:navigation-ui-ktx:$navigation_version"
    }

    val room = Room

    object Room {
        private const val room_version = "2.4.0-beta02"
        const val roomRuntime = "androidx.room:room-runtime:$room_version"
        const val roomCompiler = "androidx.room:room-compiler:$room_version"
        const val roomKtx = "androidx.room:room-ktx:$room_version"
    }
}