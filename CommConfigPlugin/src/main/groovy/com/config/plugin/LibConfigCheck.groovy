package com.config.plugin

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.ResolutionStrategy

class LibConfigCheck {

    static def SUPPORT_VERSION = "28.0.0"
    static def MULTIDEX_VERSION = "1.0.2"
    static def GSON_VERSION = "2.8.5"
    static def KOTLIN_VERSION = "1.3.50"

    static void checkLibVersion(Project project) {
        //配置容器
        ConfigurationContainer container = project.configurations
        //遍历所有配置
        container.all { Configuration conf ->
            //Defines the strategies around dependency resolution. For example,
            // forcing certain dependency versions, substitutions, conflict resolutions or snapshot timeouts
            ResolutionStrategy rs = conf.resolutionStrategy
            rs.force 'com.google.code.findbugs:jsr305:2.0.1'
            //统一第三方库的版本号
            rs.eachDependency { details ->
                def requested = details.requested
                if (requested.group == "com.android.support") {
                    //强制所有的 com.android.support 库采用固定版本
                    if (requested.name.startsWith("multidex")) {
                        details.useVersion(MULTIDEX_VERSION)
                    } else {
                        details.useVersion(SUPPORT_VERSION)
                    }
                } else if (requested.group == "com.google.code.gson") {
                    //统一 Gson 库的版本号
                    details.useVersion(GSON_VERSION)
                } else if (requested.group == "org.jetbrains.kotlin") {
                    //统一内部 kotlin 库的版本
                    details.useVersion(KOTLIN_VERSION)
                } else if (requested.group == "com.heima.iou") {
                    //内部的库版本
                    details.useVersion("1.0.0")
                }
            }
        }
    }

}