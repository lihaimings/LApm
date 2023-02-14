package com.opt.lib_image.plugin

import com.android.build.gradle.AppExtension
import com.opt.lib_image.TinkerPatchParams
import org.gradle.api.Plugin
import org.gradle.api.Project

class ImageMonitorPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        // 传递参数
        project.extensions.create(EXT_NAME, TinkerPatchParams::class.java)
        // 添加rTransform，对class文件插桩
        val android = project.extensions.getByType(AppExtension::class.java)
//        android.registerTransform(ImageMonitorTransform())
    }

    companion object {
        const val EXT_NAME = "tinkerPatch"
    }
}