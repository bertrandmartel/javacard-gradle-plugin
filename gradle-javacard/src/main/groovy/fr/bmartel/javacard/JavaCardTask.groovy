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
import fr.bmartel.javacard.util.Utility
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * JavaCard task running the ant-javacard task from Martin Paljak
 *
 * @author Bertrand Martel
 */
class JavaCardTask extends DefaultTask {

    /**
     * default directory for output
     */
    def jcBuildDir = project.buildDir.absolutePath + "/javacard"

    @TaskAction
    def build() {
        //get location of ant-javacard task jar
        def loc = new File(pro.javacard.ant.JavaCard.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())

        logger.info('javacard task location : ' + loc)

        ant.taskdef(name: 'javacard',
                classname: 'pro.javacard.ant.JavaCard',
                classpath: loc.absolutePath)

        ant.javacard(jckit: project.javacard.jckit) {

            project.javacard.caps.each() { capItem ->

                updateOutputFilePath(capItem)

                cap(
                        jckit: capItem.jckit,
                        sources: capItem.sources,
                        package: capItem.packageName,
                        version: capItem.version,
                        aid: capItem.aid,
                        output: capItem.output,
                        export: capItem.export,
                        jca: capItem.jca,
                        verify: capItem.verify,
                        debug: capItem.debug,
                        ints: capItem.ints
                ) {
                    capItem.applets.each() { appletItem ->
                        applet(
                                class: appletItem.className,
                                aid: appletItem.aid
                        )
                    }
                    capItem.imports.each() { importItem ->
                        "import"(
                                exps: importItem.exps,
                                jar: importItem.jar
                        )
                    }
                }
            }
        }
    }

    /**
     * Update output file path inclusing cap, exp and jca
     *
     * @param capItem cap object
     */
    def updateOutputFilePath(capItem) {

        if (!capItem.sources?.trim()) {
            capItem.sources = project.sourceSets.main.java.srcDirs[0]
            logger.info('update source path to ' + capItem.sources)
        }
        File file = new File(capItem.output);
        if (!file.isAbsolute()) {
            Utility.createFolder(jcBuildDir)
            if (!capItem.jca?.trim()) {
                capItem.jca = jcBuildDir + "/" + Utility.removeExtension(capItem.output) + ".jca"
                logger.info('update jca path to ' + capItem.jca)
            }
            if (!capItem.export?.trim()) {
                capItem.export = jcBuildDir + "/" + Utility.removeExtension(capItem.output) + ".exp"
                logger.info('update export path to ' + capItem.export)
            }
            capItem.output = jcBuildDir + "/" + capItem.output
        } else {
            if (!capItem.jca?.trim()) {
                capItem.jca = jcBuildDir + "/" + Utility.getFileNameWithoutExtension(capItem.output) + ".jca"
                logger.info('update jca path to ' + capItem.jca)
                Utility.createFolder(jcBuildDir)
            }
            if (!capItem.export?.trim()) {
                capItem.export = jcBuildDir + "/" + Utility.removeExtension(capItem.output) + ".exp"
                logger.info('update export path to ' + capItem.export)
                Utility.createFolder(jcBuildDir)
            }
        }
    }

    /**
     * Get JavaCard project object
     * @return
     */
    public JavaCard getJavaCard() {
        return project.javacard
    }
}