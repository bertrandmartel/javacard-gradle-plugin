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

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.BeforeClass
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.hamcrest.Matchers.*
import static org.junit.Assert.assertThat

/**
 * JavaCard plugin test.
 *
 * @author Bertrand Martel
 */
class JavaCardPluginTest {

    private static Project project

    def projectTest = [
            jckit: '/path/to/project/jckit',
            caps : [
                    cap1: [
                            jckit       : '/path/to/jckit',
                            sources     : '/path/to/sources',
                            packageName : 'some.package.name',
                            version     : '1.0',
                            aid         : '0x01:0x02:0x03:0x04:0x05',
                            output      : '/path/to/output',
                            export      : '/path/to/export',
                            jca         : '/path/to/jca',
                            verify      : false,
                            debug       : true,
                            ints        : false,
                            dependencies: [
                                    local : [
                                            exps: '/path/to/exps',
                                            jar : '/path/to/jar'
                                    ],
                                    remote: 'fr.bmartel:gplatform:1.6'
                            ],
                            applets     : [
                                    applet1: [
                                            className: 'some.class.applet1',
                                            aid      : '0x01:0x02:0x03:0x04:0x05:0x01'
                                    ],
                                    applet2: [
                                            className: 'some.class.applet2',
                                            aid      : '0x01:0x02:0x03:0x04:0x05:0x02'
                                    ]
                            ]
                    ]
            ]
    ]

    @BeforeClass
    static void setUpProject() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'javacard'
    }

    @Test
    void addBuildTask() {
        def task = project.getTasks().findByPath('buildJavacard')
        assertThat(task, instanceOf(JavaCardTask))
        assertThat(task.group, equalTo('build'))
        assertThat(task.dependsOn, hasItem(project.classes))
    }

    @Test
    void checkDependsSetup() {
        assertThat(project.build.dependsOn, hasItem(project.buildJavacard))
    }

    @Test
    void checkExtensionValues() {
        project.configure(project.javacard) {
            jckit projectTest.jckit
            cap {
                jckit projectTest.caps.cap1.jckit
                sources projectTest.caps.cap1.sources
                packageName projectTest.caps.cap1.packageName
                version projectTest.caps.cap1.version
                aid projectTest.caps.cap1.aid
                output projectTest.caps.cap1.output
                export projectTest.caps.cap1.export
                jca projectTest.caps.cap1.jca
                verify projectTest.caps.cap1.verify
                debug projectTest.caps.cap1.debug
                ints projectTest.caps.cap1.ints
                applet {
                    className projectTest.caps.cap1.applets.applet1.className
                    aid projectTest.caps.cap1.applets.applet1.aid
                }
                applet {
                    className projectTest.caps.cap1.applets.applet2.className
                    aid projectTest.caps.cap1.applets.applet2.aid
                }
                dependencies {
                    remote projectTest.caps.cap1.dependencies.remote
                    local {
                        exps projectTest.caps.cap1.dependencies.local.exps
                        jar projectTest.caps.cap1.dependencies.local.jar
                    }
                }
            }
        }

        JavaCardTask task = project.getTasks().findByPath('buildJavacard')
        assertEquals(task.getJavaCard().jckit, projectTest.jckit)
        assertEquals(task.getJavaCard().caps.size(), 1)
        assertEquals(task.getJavaCard().caps[0].jckit, projectTest.caps.cap1.jckit)
        assertEquals(task.getJavaCard().caps[0].sources, projectTest.caps.cap1.sources)
        assertEquals(task.getJavaCard().caps[0].packageName, projectTest.caps.cap1.packageName)
        assertEquals(task.getJavaCard().caps[0].version, projectTest.caps.cap1.version)
        assertEquals(task.getJavaCard().caps[0].aid, projectTest.caps.cap1.aid)
        assertEquals(task.getJavaCard().caps[0].output, projectTest.caps.cap1.output)
        assertEquals(task.getJavaCard().caps[0].export, projectTest.caps.cap1.export)
        assertEquals(task.getJavaCard().caps[0].jca, projectTest.caps.cap1.jca)
        assertEquals(task.getJavaCard().caps[0].verify, projectTest.caps.cap1.verify)
        assertEquals(task.getJavaCard().caps[0].debug, projectTest.caps.cap1.debug)
        assertEquals(task.getJavaCard().caps[0].ints, projectTest.caps.cap1.ints)
        assertEquals(task.getJavaCard().caps[0].applets.size(), 2)
        assertEquals(task.getJavaCard().caps[0].applets[0].className, projectTest.caps.cap1.applets.applet1.className)
        assertEquals(task.getJavaCard().caps[0].applets[0].aid, projectTest.caps.cap1.applets.applet1.aid)
        assertEquals(task.getJavaCard().caps[0].applets[1].className, projectTest.caps.cap1.applets.applet2.className)
        assertEquals(task.getJavaCard().caps[0].applets[1].aid, projectTest.caps.cap1.applets.applet2.aid)
        assertEquals(task.getJavaCard().caps[0].dependencies.local[0].exps, projectTest.caps.cap1.dependencies.local.exps)
        assertEquals(task.getJavaCard().caps[0].dependencies.local[0].jar, projectTest.caps.cap1.dependencies.local.jar)
        assertEquals(task.getJavaCard().caps[0].dependencies.local.size(), 1)
        assertEquals(task.getJavaCard().caps[0].dependencies.remote[0], projectTest.caps.cap1.dependencies.remote)
        assertEquals(task.getJavaCard().caps[0].dependencies.remote.size(), 1)
    }
}
