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
            main = 'pro.javacard.gp.GPTool'
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