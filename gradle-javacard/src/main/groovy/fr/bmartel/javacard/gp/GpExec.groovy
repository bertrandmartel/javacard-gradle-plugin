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

package fr.bmartel.javacard.gp

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.JavaExec

/**
 * Task type that inherits JavaExec.
 *
 * @author Bertrand Martel
 */
class GpExec extends JavaExec {

    static String GP_CLASSNAME = 'pro.javacard.gp.GPTool'

    /**
     * dependency of Global Platform Pro
     */
    def depList = [
            'net.sf.jopt-simple:jopt-simple:5.0.4',
            'org.bouncycastle:bcprov-jdk15on:1.57',
            'com.google.guava:guava:22.0',
            'com.googlecode.json-simple:json-simple:1.1.1',
            'net.java.dev.jna:jna:4.2.1',
            'org.slf4j:slf4j-simple:1.7.25',
            'org.apache.ant:ant:1.8.2'
    ]

    GpExec() {
        super()
        configure {
            main = GP_CLASSNAME
            classpath = getGpClassPath(project)
        }
    }

    /**
     * Get the classpath for Global Platform Pro
     *
     * @param project
     * @return
     */
    def getGpClassPath(Project project) {

        FileCollection gproClasspath = project.files(new File(pro.javacard.gp.GPTool.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()))

        project.repositories.add(project.repositories.mavenCentral())

        depList.each { item ->
            project.dependencies.add("compile", item)
        }

        gproClasspath += project.sourceSets.main.runtimeClasspath

        return gproClasspath
    }
}