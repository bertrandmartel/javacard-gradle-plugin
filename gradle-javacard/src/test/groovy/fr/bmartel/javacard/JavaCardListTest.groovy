package fr.bmartel.javacard

import fr.bmartel.javacard.common.CommonTest
import org.gradle.api.tasks.JavaExec
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class JavaCardListTest extends CommonTest {

    @Test
    void listAppletTest() {
        runBuildTask(StaticConfig.VALID_CONFIG)

        JavaExec listTask = project.getTasks().findByName('listJavaCard')

        assertTrue(listTask ? true : false)
        assertTrue(listTask instanceof JavaExec)
        assertEquals(listTask.group, 'global platform')
        assertEquals(listTask.args, ['-l',
                                     '-key-enc', '404142434445464748494A4B4C4D4E4F',
                                     '-key-mac', '404142434445464748494A4B4C4D4E4F',
                                     '-key-kek', '404142434445464748494A4B4C4D4E4F'])
        assertEquals(listTask.main, 'pro.javacard.gp.GPTool')

        //listTask.exec()
    }
}