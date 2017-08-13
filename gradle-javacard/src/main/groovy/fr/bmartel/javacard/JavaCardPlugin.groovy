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
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * JavaCard plugin
 *
 * @author Bertrand Martel
 */
class JavaCardPlugin implements Plugin<Project> {

    Logger logger = LoggerFactory.getLogger('javacard-logger')

    void apply(Project project) {

        //define plugin extension
        def extension = project.extensions.create("javacard", JavaCard)

        project.afterEvaluate {

            //resolve the javacard framework according to SDK version
            project.dependencies {
                compile project.files(SdkUtils.getApiPath(extension.getJcKit(), logger))
            }

            //validate the extension properties
            extension.validate()
        }

        //apply the java plugin if not defined
        if (!project.plugins.hasPlugin(JavaPlugin)) {
            project.plugins.apply(JavaPlugin)
        }

        def build = project.tasks.create("buildJavacard", JavaCardTask)

        build.configure {
            group = 'build'
            description = 'Create CAP file(s) for installation on a smart card'
            dependsOn(project.classes)
        }

        project.build.dependsOn(build)
    }
}