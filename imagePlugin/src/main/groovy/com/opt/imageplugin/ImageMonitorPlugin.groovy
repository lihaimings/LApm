package com.opt.imageplugin

import com.android.build.gradle.AppExtension
import com.opt.imageplugin.transform.ImageMonitorTransform;
import org.gradle.api.Plugin
import org.gradle.api.Project

class ImageMonitorPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        // 插件属性设置
        project.extensions.create("imageMonitor", ImagePluginParams)
        project.afterEvaluate {
            // 获取属性
            def imageMonitor = project.extensions.getByType(ImagePluginParams)
            imageMonitor.getIsOpen()
        }

        // 获取android{}的属性
        def android = project.extensions.getByType(AppExtension)

        // 注册transform
        android.registerTransform(ImageMonitorTransform)
    }
}