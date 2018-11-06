package fr.bmartel.javacard

import fr.bmartel.javacard.common.CommonTest
import org.gradle.api.Task
import org.gradle.api.tasks.JavaExec
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertThat
import static org.hamcrest.Matchers.*

class JavaCardInstallTaskTest extends CommonTest {

    @Test
    void validBuildInstall() {
        runBuildTask(StaticConfig.VALID_CONFIG)
        JavaExec installTask = project.getTasks().getByName("installJavaCard")
        Task buildTask = project.getTasks().getByName("buildJavaCard")
        assertTrue(installTask ? true : false)
        assertTrue(installTask instanceof JavaExec)
        assertThat(installTask.dependsOn, hasItem(buildTask))
        assertEquals(installTask.group, 'global platform')
        assertEquals(installTask.args, ['--delete', '010203040506070809',
                                        '--install', project.buildDir.absolutePath + File.separator + "javacard" + File.separator + "applet.cap",
                                        '-key-enc', '404142434445464748494A4B4C4D4E4F',
                                        '-key-mac', '404142434445464748494A4B4C4D4E4F',
                                        '-key-kek', '404142434445464748494A4B4C4D4E4F'
        ])
        assertEquals(installTask.main, 'pro.javacard.gp.GPTool')
        //installTask.exec()
    }
}