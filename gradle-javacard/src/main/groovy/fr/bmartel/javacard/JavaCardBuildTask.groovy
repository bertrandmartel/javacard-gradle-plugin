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

import com.sun.media.sound.InvalidDataException
import fr.bmartel.javacard.extension.JavaCard
import fr.bmartel.javacard.util.Utility
import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.tasks.TaskAction

/**
 * JavaCard task running the ant-javacard task from Martin Paljak
 *
 * @author Bertrand Martel
 */
class JavaCardBuildTask extends DefaultTask {

    /**
     * default directory for output
     */
    def jcBuildDir = project.buildDir.absolutePath + File.separator + "javacard"

    @TaskAction
    def build() {
        ant.lifecycleLogLevel = project.javacard.config.logLevel

        //get location of ant-javacard task jar
        def loc = new File(pro.javacard.ant.JavaCard.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())

        logger.debug('javacard task location : ' + loc)

        ant.taskdef(name: 'javacard',
                classname: 'pro.javacard.ant.JavaCard',
                classpath: loc.absolutePath)

        ant.javacard(buildJavacardMap(project.javacard.config.jckit)) {

            project.javacard.config.caps.each() { capItem ->

                updateOutputFilePath(capItem)

                cap(buildCapMap(capItem)) {
                    capItem.applets.each() { appletItem ->
                        applet(buildAppletMap(appletItem))
                    }

                    // for each dependent project add an import with exp folder & jar
                    getAllDependentProjects(project).each {
                        def expFolder = ""
                        def jarPath = ""

                        new File(it.ext.javacardDir).eachFile() {
                            if (it.isDirectory()) {
                                expFolder = it.absolutePath
                                it.eachFileMatch(FileType.ANY, ~/.*\.jar/) {
                                    jarPath = it.absolutePath
                                }
                            }
                        }

                        logger.debug("import attributes : " + [
                                exps: expFolder,
                                jar : jarPath
                        ])

                        "import"([
                                exps: expFolder,
                                jar : jarPath
                        ])
                    }

                    if (capItem.dependencies != null) {
                        capItem.dependencies.local.each() { localItem ->
                            "import"(buildLocalItem(localItem))
                        }

                        capItem.dependencies.remote.each() { remoteItem ->
                            "import"(buildRemoteItem(remoteItem))
                        }
                    }
                }
            }
        }
    }

    /**
     * build cap attribute map
     *
     * @param capItem cap item
     * @return map of cap attributes
     */
    def buildCapMap(capItem) {
        def map = [:]
        if (capItem.jckit?.trim()) {
            map["jckit"] = capItem.jckit
        }
        if (capItem.sources?.trim()) {
            map["sources"] = capItem.sources
        }
        if (capItem.classes?.trim()) {
            map["classes"] = capItem.classes
        }
        if (capItem.packageName?.trim()) {
            map["package"] = capItem.packageName
        }
        if (capItem.version?.trim()) {
            map["version"] = capItem.version
        }
        if (capItem.aid?.trim()) {
            map["aid"] = capItem.aid
        }
        if (capItem.output?.trim()) {
            map["output"] = capItem.output
        }
        if (capItem.export?.trim()) {
            map["export"] = capItem.export
        }
        if (capItem.jca?.trim()) {
            map["jca"] = capItem.jca
        }
        map["verify"] = capItem.verify
        map["ints"] = capItem.ints
        map["debug"] = capItem.debug
        logger.debug("cap attributes : " + map)
        return map
    }

    /**
     * build javacard attribute map.
     *
     * @param jckit jckit
     * @return map of javacard attributes
     */
    def buildJavacardMap(jckit) {
        def map = [:]
        if (jckit?.trim()) {
            map["jckit"] = jckit
        }
        logger.debug("javacard attributes : " + map)
        return map
    }

    /**
     * Build applet attribute map.
     *
     * @param appletItem applet item
     * @return map of applet attributes
     */
    def buildAppletMap(appletItem) {
        def map = [:]
        if (appletItem.className?.trim()) {
            map["class"] = appletItem.className
        }
        if (appletItem.aid?.trim()) {
            map["aid"] = appletItem.aid
        }
        logger.debug("applet attributes : " + map)
        return map
    }

    /**
     * Get all dependent project : https://discuss.gradle.org/t/getting-all-project-dependencies/6540/2 by Ahsan_Rabbani
     *
     * @param project
     * @return
     */
    def getAllDependentProjects(project) {
        def projectDependencies = project.configurations.runtime.getAllDependencies().withType(ProjectDependency)
        def dependentProjects = projectDependencies*.dependencyProject
        if (dependentProjects.size > 0) {
            dependentProjects.each { dependentProjects += getAllDependentProjects(it) }
        }
        return dependentProjects.unique()
    }

    /**
     * Build import attributes map for local item.
     *
     * @param Import import item
     * @return map of import attributes
     */
    def buildLocalItem(importItem) {
        def map = [:]
        if (importItem.exps?.trim()) {
            map["exps"] = importItem.exps
        }
        if (importItem.jar?.trim()) {
            map["jar"] = importItem.jar
        }
        logger.debug("import attributes : " + map)
        return map
    }

    /**
     * Build import attributes map for remote item.
     *
     * @param String remote item
     * @return map of import attributes
     */
    def buildRemoteItem(remote) {
        def map = [:]
        if (remote?.trim()) {
            project.configurations.create(remote)
            project.dependencies.add(remote, remote)

            def jarConf = project.configurations[remote].resolve()

            if (jarConf.size() > 0) {
                Utility.unzip(jarConf[0].getAbsolutePath(), jarConf[0].getParent())
                map["exps"] = jarConf[0].getParent()
                map["jar"] = jarConf[0].getAbsolutePath()
            } else {
                logger.error("error : exp/jar wasn't found in remote dependency : " + remote)
                throw new InvalidDataException();
            }
        }
        logger.debug("import attributes : " + map)
        return map
    }

    /**
     * Finds first available source set match
     * @param capItem
     * @return
     */
    def findDefaultSources(capItem){
        if (capItem.findSources) {
            def folderFound = false
            def folderIdx = 0
            for(curSrcDir in project.sourceSets.main.java.srcDirs){
                if (curSrcDir.exists() && (capItem.defaultSources || folderIdx > 0)) {
                    folderFound = true
                    capItem.sources = curSrcDir
                    break
                }
                folderIdx += 1
            }

            if (!folderFound){
                throw new InvalidUserDataException('Applet sources not found : ' + project.sourceSets.main.java.srcDirs[0])
            }

        } else {
            def srcIndex = capItem.defaultSources ? 0 : project.sourceSets.main.java.srcDirs.size() - 1;
            capItem.sources = project.sourceSets.main.java.srcDirs[srcIndex]
        }

        logger.debug('update source path to ' + capItem.sources)
    }

    /**
     * Update output file path inclusing cap, exp and jca
     *
     * @param capItem cap object
     */
    def updateOutputFilePath(capItem) {
        if (!capItem.sources?.trim()) {
            findDefaultSources(capItem);
        }

        File file = new File(capItem.output);
        if (!file.isAbsolute()) {
            Utility.createFolder(jcBuildDir)
            if (!capItem.jca?.trim()) {
                capItem.jca = jcBuildDir + File.separator + Utility.removeExtension(capItem.output) + ".jca"
                logger.debug('update jca path to ' + capItem.jca)
            }
            if (!capItem.export?.trim()) {
                capItem.export = jcBuildDir + File.separator + Utility.removeExtension(capItem.output) + ".exp"
                logger.debug('update export path to ' + capItem.export)
            }
            capItem.output = jcBuildDir + File.separator + capItem.output
        } else {
            if (!capItem.jca?.trim()) {
                capItem.jca = jcBuildDir + File.separator + Utility.removeExtension(capItem.output) + ".jca"
                logger.debug('update jca path to ' + capItem.jca)
                Utility.createFolder(jcBuildDir)
            }
            if (!capItem.export?.trim()) {
                capItem.export = jcBuildDir + File.separator + Utility.removeExtension(capItem.output) + ".exp"
                logger.debug('update export path to ' + capItem.export)
                Utility.createFolder(jcBuildDir)
            }
        }

        //update jca & export when non absolute path are referenced
        File jcaFile = new File(capItem.jca);
        if (!jcaFile.isAbsolute()) {
            capItem.jca = jcBuildDir + File.separator + capItem.jca
            logger.debug('update jca path to ' + capItem.jca)
        }
        File exportFile = new File(capItem.export);
        if (!exportFile.isAbsolute()) {
            capItem.export = jcBuildDir + File.separator + capItem.export
            logger.debug('update export path to ' + capItem.export)
        }
    }

    /**
     * Get JavaCard project object
     * @return
     */
    JavaCard getJavaCard() {
        return project.javacard
    }
}