package com.config.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * 混淆规则检测
 */
class ProguardCheckUtil {

    /**
     * 检测混淆文件是否符合规则
     * @param project
     */
    static void checkProFile(Project project) {

        //在project配置结束后调用
        project.afterEvaluate {

            BaseExtension android = project.extensions.getByName("android")

            if (!(android instanceof AppExtension)) {
                return
            }

            //遍历所有的构建体
            android.applicationVariants.all { ApplicationVariant variant ->

                if (!variant.buildType.minifyEnabled) {
                    println("检测到${variant.name}没有开启混淆，所以不进行检测")
                    return
                }

                String compileTaskName = "compile${variant.name.capitalize()}JavaWithJavac"
                //获得编译的task
                Task compileTask = project.tasks.getByName(compileTaskName)
                println("名字为：${compileTaskName}")
                if (compileTask==null){
                    println("任务为空")
                }
                if (compileTask != null) {
                    //创建一个task
                    Task checkTask = project.task("easy${variant.name.capitalize()}CheckProguardRule")

                    checkTask.doLast {
                        println("开始检测混淆文件")
                        //获取混淆文件
                        Collection<File> files = variant.buildType.proguardFiles
                        //将所有的混淆文件转换成字符串
                        StringBuilder sb = new StringBuilder()
                        if (files != null) {
                            for (File file : files) {
                                if (file.exists()) {
                                    sb.append(file.text)
                                }
                            }
                        }
                        //进行检测
                        analyseProguardText(sb.toString())
                    }
                    //在javac编译前执行checkTask任务
                    compileTask.dependsOn(checkTask)
                }
            }
        }
    }

    /**
     * 逐行做检查，看看有没有不符合规范的规则
     *
     * @param content
     */
    private static void analyseProguardText(String content) throws GradleException {
        //以换行符为界限将混淆文件的内容分割成一个一个字符串
        String[] lines = content.split("\n")
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim() //去空格
            if (line == null || line.length() == 0 || line.startsWith("#")) {
                continue
            }
            println("当前规则：${line}")

            if (line.matches(~"[\\s]*-ignorewarnings.*")) {
                throw new GradleException("违反 Proguard 规则：\n禁止使用 -ignorewarnings ")
            }
            if (line.matches(~"[\\s]*-dontshrink[\\s]*")) {
                throw new GradleException("违反 Proguard 规则：\n禁止使用 -dontshrink ")
            }

            //该选项如果开启，编译会很慢
            if (line.matches(~"[\\s]*-dontoptimize[\\s]*")) {
                throw new GradleException("违反 Proguard 规则：\n禁止使用 -dontoptimize ")
            }

            if (line.matches(~"[\\s]*-dontwarn[\\s]*\\*\\*[\\s]*")) {
                throw new GradleException("违反 Proguard 规则：\n禁止使用 -dontwarn ** ")
            }

        }
    }

}
