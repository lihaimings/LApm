package com.opt.lib_version.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class VersionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        println("Hello.")
    }
}