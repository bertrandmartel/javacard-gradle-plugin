package fr.bmartel.javacard

import fr.bmartel.javacard.common.CommonTest
import org.gradle.api.tasks.JavaExec
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class JavaCardKeyTest extends CommonTest {

    @Test
    void buildInstallMixedKey() {
        runBuildTask(StaticConfig.VALID_CONFIG_MIXED_KEY)
        JavaExec installTask = project.getTasks().getByName("installJavaCard")
        assertTrue(installTask ? true : false)
        assertTrue(installTask instanceof JavaExec)
        assertEquals('global platform', installTask.group)
        assertEquals(['--delete', '010203040506070809',
                      '--install', project.buildDir.absolutePath + File.separator + "javacard" + File.separator + "applet.cap",
                      '-key-enc', '414142434445464748494A4B4C4D4E4F',
                      '-key-mac', '404142434445464748494A4B4C4D4E4F',
                      '-key-kek', '424142434445464748494A4B4C4D4E4F'
        ], installTask.args)
        assertEquals(installTask.main, 'pro.javacard.gp.GPTool')
        //installTask.exec()
    }
}