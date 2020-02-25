package com.config.plugin

import com.android.build.gradle.BaseExtension
import org.gradle.api.GradleException
import org.gradle.api.Project

class CompileConfigUtil {

    static def MIN_SDK = 19
    static def TARGET_SDK = 28
    static def COMPILE_SDK = "android-28"

    /**
     * 检查编译相关
     * @param project
     */
    static void checkCompileConfig(Project project) {

        project.afterEvaluate {

            BaseExtension android = project.extensions.getByName("android")

            //强制统一 compileSdkVersion、 minSdkVersion、targetSdkVersion
            String compileSdkVersion = android.compileSdkVersion
            int targetSdkVersion = android.defaultConfig.targetSdkVersion.apiLevel
            int minSdkVersion = android.defaultConfig.minSdkVersion.apiLevel

            if (!COMPILE_SDK.equals(compileSdkVersion)) {
                throw new GradleException("请修改 compileSdkVersion，必须设置为 ${COMPILE_SDK}")
            }
            if (minSdkVersion != MIN_SDK) {
                throw new GradleException("请修改 minSdkVersion，必须设置为 ${MIN_SDK}")
            }
            if (targetSdkVersion != TARGET_SDK) {
                throw new GradleException("请修改 targetSdkVersion，必须设置为 ${TARGET_SDK}")
            }
        }
    }

}