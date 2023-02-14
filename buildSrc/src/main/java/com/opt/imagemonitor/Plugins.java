package com.opt.imagemonitor;


import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class Plugins implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        System.out.println("=========================Plugins_>apply()");
        TransformTest transformTest = new TransformTest();
        project.getExtensions().findByType(AppExtension.class).registerTransform(transformTest);
    }
}
