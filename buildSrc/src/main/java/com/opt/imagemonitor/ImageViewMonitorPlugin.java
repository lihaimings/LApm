package com.opt.imagemonitor;

import com.android.build.api.transform.Transform;
import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class ImageViewMonitorPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

//        Transform transform = new ImageMonitorTransform();
//        project.getExtensions().getByType(AppExtension.class).registerTransform(transform);
    }
}