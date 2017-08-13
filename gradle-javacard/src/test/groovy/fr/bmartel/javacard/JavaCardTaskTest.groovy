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

import fr.bmartel.javacard.util.Utility
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * JavaCard task test.
 *
 * @author Bertrand Martel
 */
class JavaCardTaskTest {

    private static Project project
    private static JavaCardTask task

    private static String buildDir = System.getProperty("user.dir") + "/build"
    private static String buildJavaCardDir = buildDir + "/javacard"

    Closure buildSdkConf(sdk, appletName) {
        return {
            cap {
                jckit sdk
                packageName 'fr.bmartel.javacard'
                version '0.1'
                aid '01:02:03:04:05:06:07:08:09'
                output appletName + '.cap'
                applet {
                    className 'fr.bmartel.javacard.HelloSmartcard'
                    aid '01:02:03:04:05:06:07:08:09:01:02'
                }
                importResource {
                    jar StaticConfig.DEPENDENCY_PATH
                    exps StaticConfig.EXP_PATH
                }
            }
        }
    }

    @Before
    void setUpProject() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'javacard'

        //set build directory
        project.setBuildDir(buildDir)

        //set source path
        project.sourceSets {
            main {
                java {
                    srcDirs = [System.getProperty("user.dir") + "/../test/src/main/java"]
                }
            }
        }
        task = project.getTasks().create('buildJavacard2', JavaCardTask)
    }

    void configureProject(Closure closure) {
        project.configure(project.javacard, closure)
    }

    void runTask(Closure closure) {
        deleteBuildDir()
        configureProject(closure)
        project.evaluate()
        task.build()
        checkOutputFile(task)
    }

    File getFile(ext) {
        return new File(ext)
    }

    String getFileName(filePath, ext) {
        return Utility.removeExtension(filePath) + "." + ext
    }

    void checkOutputFile(task) {
        task.getJavaCard().caps.each { capItem ->
            assertTrue(getFile(capItem.output).exists())
            if (capItem.export?.trim()) {
                assertTrue(getFile(getFileName(capItem.export, "exp")).exists())
            } else {
                assertTrue(getFile(getFileName(capItem.output, "exp")).exists())
            }
            if (capItem.jca?.trim()) {
                assertTrue(getFile(getFileName(capItem.jca, "jca")).exists())
            } else {
                assertTrue(getFile(getFileName(capItem.output, "jca")).exists())
            }
        }
    }

    void deleteBuildDir() {
        File dir = new File(buildJavaCardDir)
        dir.deleteDir()
    }

    @Test
    void validBuild() {
        runTask(StaticConfig.VALID_CONFIG)
    }

    @Test
    void validBuildFullOutput() {
        runTask(StaticConfig.FULL_OUTPUT)
    }

    @Test
    void multipleCaps() {
        runTask(StaticConfig.MULTIPLE_CAPS)
    }

    @Test
    void multipleApplets() {
        runTask(StaticConfig.MULTIPLE_APPLETS)
    }

    @Test
    void sdkVersion221() {
        runTask(buildSdkConf(StaticConfig.getSdkPath("jc221_kit"), "applet"))
    }

    @Test
    void sdkVersion222() {
        runTask(buildSdkConf(StaticConfig.getSdkPath("jc222_kit"), "applet1"))
    }

    @Test
    void sdkVersion303() {
        runTask(buildSdkConf(StaticConfig.getSdkPath("jc303_kit"), "applet2"))
    }

    @Test
    void sdkVersion304() {
        runTask(buildSdkConf(StaticConfig.getSdkPath("jc304_kit"), "applet3"))
    }

    @Test
    void sdkVersion305u1() {
        runTask(buildSdkConf(StaticConfig.getSdkPath("jc305u1_kit"), "applet4"))
    }

    @Test(expected = ProjectConfigurationException.class)
    void jckitPathUndefinedError() {
        runTask(StaticConfig.UNDEFINED_JCKIT_PATH)
    }

    @Test(expected = ProjectConfigurationException.class)
    void jckitPathInvalidError() {
        runTask(StaticConfig.INVALID_JCKIT_PATH)
    }

    @Test(expected = ProjectConfigurationException.class)
    void outputRequiredError() {
        runTask(StaticConfig.OUTPUT_REQUIRED)
    }

    @Test(expected = ProjectConfigurationException.class)
    void appletClassNameRequiredError() {
        runTask(StaticConfig.APPLET_CLASSNAME_REQUIRED)
    }

    @Test(expected = ProjectConfigurationException.class)
    void missingAllCapAttributesError() {
        runTask(StaticConfig.MISSING_ALL_CAPS)
    }

    @Test(expected = ProjectConfigurationException.class)
    void noFieldsError() {
        runTask(StaticConfig.NO_FIELDS)
    }
}