/*
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2017 Bertrand Martel
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package fr.bmartel.javacard

import fr.bmartel.javacard.extension.JavaCard
import fr.bmartel.javacard.util.SdkUtils
import fr.bmartel.javacard.util.Utility
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.JavaExec
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * JavaCard plugin
 *
 * @author Bertrand Martel
 */
class JavaCardPlugin implements Plugin<Project> {

    Logger logger = LoggerFactory.getLogger('javacard-logger')

    def depList = [
            'net.sf.jopt-simple:jopt-simple:5.0.4',
            'org.bouncycastle:bcprov-jdk15on:1.57',
            'com.google.guava:guava:22.0',
            'com.googlecode.json-simple:json-simple:1.1.1',
            'net.java.dev.jna:jna:4.2.1',
            'org.slf4j:slf4j-simple:1.7.25',
            'org.apache.ant:ant:1.8.2'
    ]

    void apply(Project project) {

        //define plugin extension
        def extension = project.extensions.create("javacard", JavaCard)

        project.afterEvaluate {

            File propertyFile = project.rootProject.file('local.properties')

            if (propertyFile.exists()) {
                Properties properties = new Properties()
                properties.load(propertyFile.newDataInputStream())
                if (properties.getProperty('jc.home')?.trim()) {
                    extension.config.jckit = properties.getProperty('jc.home')
                }
            }
            logger.debug("jckit location : " + extension.config.getJcKit())

            //resolve the javacard framework according to SDK version
            project.dependencies {
                compile project.files(SdkUtils.getApiPath(extension.config.getJcKit(), logger))
            }

            extension.config.caps.each { capItem ->

                if (capItem.dependencies != null) {
                    capItem.dependencies.local.each { localItem ->
                        project.dependencies.add("compile", project.files(localItem.jar))
                    }
                    capItem.dependencies.remote.each { remoteItem ->
                        project.dependencies.add("compile", remoteItem)
                    }
                }
            }

            if (extension.scripts != null) {

                extension.scripts.tasks.each { taskItem ->

                    def command = []

                    command.add('-d')

                    taskItem.scripts.each { taskIncludedScript ->
                        extension.scripts.scripts.each { scriptItem ->
                            if (scriptItem.name == taskIncludedScript) {
                                command.add('-a')
                                command.add(Utility.formatApdu(scriptItem.apdu))
                            }
                        }
                    }

                    if (!project.tasks.findByName(taskItem.name)) {
                        createScriptTask(project, taskItem.name, command)
                    }
                }
            }

            if (!project.tasks.findByName('installJavacard')) {
                createInstallTask(project, extension)
            }

            //validate the extension properties
            extension.validate()
        }

        //apply the java plugin if not defined
        if (!project.plugins.hasPlugin(JavaPlugin)) {
            project.plugins.apply(JavaPlugin)
        }

        def build = project.tasks.create("buildJavacard", JavaCardBuildTask)

        build.configure {
            group = 'build'
            description = 'Create CAP file(s) for installation on a smart card'
            dependsOn(project.classes)
        }

        if (!project.tasks.findByName('listJavacard')) {
            createListTask(project)
        }

        project.build.dependsOn(build)
    }

    def createInstallTask(Project project, extension) {
        def install = project.tasks.create(name: "installJavacard", type: JavaExec)
        def args = ['-relax']
        extension.config.caps.each { capItem ->
            args.add('--delete')
            args.add(Utility.formatApdu(capItem.aid))
            args.add('--install')
            args.add(new File(capItem.output).absolutePath)
        }
        createJavaExec(project, install, 'install', 'install cap file', args)
    }

    def createListTask(Project project) {
        def script = project.tasks.create(name: 'listJavacard', type: JavaExec)
        createJavaExec(project, script, 'list', 'apdu script', ['-l'])
    }

    def createScriptTask(Project project, String taskName, args) {
        def script = project.tasks.create(name: taskName, type: JavaExec)
        createJavaExec(project, script, 'javacard-script', 'apdu script', args)
    }

    def createJavaExec(Project project, Task task, String grp, String desc, arguments) {

        FileCollection gproClasspath = project.files(new File(pro.javacard.gp.GPTool.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()))

        project.repositories.add(project.repositories.mavenCentral())
        depList.each { item ->
            project.dependencies.add("compile", item)
        }

        gproClasspath += project.sourceSets.main.runtimeClasspath

        task.configure {
            group = grp
            description = desc
            main = 'pro.javacard.gp.GPTool'
            classpath = gproClasspath
            args(arguments)
            doFirst {
                logger.quiet(commandLine)
            }
            dependsOn(project.jar)
        }
    }
}