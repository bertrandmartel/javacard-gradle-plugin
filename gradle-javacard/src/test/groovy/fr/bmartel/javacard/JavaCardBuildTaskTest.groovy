/*
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2017-2018 Bertrand Martel
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

import fr.bmartel.javacard.common.CommonTest
import org.gradle.api.ProjectConfigurationException
import org.junit.Test

/**
 * JavaCard task test.
 *
 * @author Bertrand Martel
 */
class JavaCardBuildTaskTest extends CommonTest {

    Closure buildSdkConf(sdk, appletName) {
        return {
            config {
                cap {
                    jckit sdk
                    packageName 'fr.bmartel.javacard'
                    version '0.1'
                    aid '01:02:03:04:05:06:07:08:09'
                    output appletName + '.cap'
                    applet {
                        className 'fr.bmartel.javacard.HelloWorld'
                        aid '01:02:03:04:05:06:07:08:09:01:02'
                    }
                    dependencies {
                    }
                }
            }
        }
    }

    @Test
    void validBuild() {
        runBuildTask(StaticConfig.VALID_CONFIG)
    }

    @Test
    void simpleBuild() {
        runBuildTask(StaticConfig.SIMPLE_CONFIG)
    }

    @Test
    void validBuildFullOutput() {
        runBuildTask(StaticConfig.FULL_OUTPUT)
    }

    @Test
    void multipleCaps() {
        runBuildTask(StaticConfig.MULTIPLE_CAPS)
    }

    @Test
    void multipleApplets() {
        runBuildTask(StaticConfig.MULTIPLE_APPLETS)
    }

    @Test
    void sdkVersion212() {
        runBuildTask(buildSdkConf(StaticConfig.getSdkPath("jc212_kit"), "applet"))
    }

    @Test
    void sdkVersion221() {
        runBuildTask(buildSdkConf(StaticConfig.getSdkPath("jc221_kit"), "applet"))
    }

    @Test
    void sdkVersion222() {
        runBuildTask(buildSdkConf(StaticConfig.getSdkPath("jc222_kit"), "applet1"))
    }


    @Test
    void sdkVersion303() {
        runBuildTask(buildSdkConf(StaticConfig.getSdkPath("jc303_kit"), "applet2"))
    }

    @Test
    void sdkVersion304() {
        runBuildTask(buildSdkConf(StaticConfig.getSdkPath("jc304_kit"), "applet3"))
    }

    @Test
    void sdkVersion305u1() {
        runBuildTask(buildSdkConf(StaticConfig.getSdkPath("jc305u1_kit"), "applet4"))
    }

    @Test(expected = ProjectConfigurationException.class)
    void jckitPathUndefinedError() {
        runBuildTask(StaticConfig.UNDEFINED_JCKIT_PATH)
    }

    @Test(expected = ProjectConfigurationException.class)
    void jckitPathInvalidError() {
        runBuildTask(StaticConfig.INVALID_JCKIT_PATH)
    }

    @Test(expected = ProjectConfigurationException.class)
    void outputRequiredError() {
        runBuildTask(StaticConfig.OUTPUT_REQUIRED)
    }

    @Test(expected = ProjectConfigurationException.class)
    void appletClassNameRequiredError() {
        runBuildTask(StaticConfig.APPLET_CLASSNAME_REQUIRED)
    }

    @Test(expected = ProjectConfigurationException.class)
    void missingAllCapAttributesError() {
        runBuildTask(StaticConfig.MISSING_ALL_CAPS)
    }

    @Test(expected = ProjectConfigurationException.class)
    void noFieldsError() {
        runBuildTask(StaticConfig.NO_FIELDS)
    }
}