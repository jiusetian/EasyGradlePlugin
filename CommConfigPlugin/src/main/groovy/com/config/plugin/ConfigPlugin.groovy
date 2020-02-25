package com.config.plugin


import org.gradle.api.Plugin
import org.gradle.api.Project
/**
 * 配置相关规范插件
 */
class ConfigPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        println("执行插件configplugin")

        //统一第三方依赖库的版本号，常见的有 com.android.support 包等
        LibVersionUtil.checkLibVersion(project)

        //检查Android编译配置
        CompileConfigUtil.checkCompileConfig(project)

        //混淆检测
        ProguardCheckUtil.checkProFile(project)

    }

}