package fr.bmartel.javacard

import fr.bmartel.javacard.common.CommonTest
import org.gradle.api.Task
import org.gradle.api.tasks.JavaExec
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class JavaCardKeyTest extends CommonTest {

    @Test
    void validScriptWithKeys() {
        runBuildTask(StaticConfig.VALID_SCRIPT_CONFIG_WITH_KEY)

        Task task1 = project.getTasks().findByName('task1')
        Task task2 = project.getTasks().findByName('task2')
        Task task3 = project.getTasks().findByName('task3')

        assertTrue(task1 ? true : false)
        assertTrue(task2 ? true : false)
        assertTrue(task3 ? true : false)

        assertTrue(task1 instanceof JavaExec)
        assertTrue(task2 instanceof JavaExec)
        assertTrue(task3 instanceof JavaExec)

        assertEquals('global platform', task1.group)
        assertEquals('global platform', task2.group)
        assertEquals('global platform', task3.group)

        assertEquals(['-d',
                      '-a', '010203',
                      '-a', '040506',
                      '-key-enc', '404142434445464748494A4B4C4D4E4F',
                      '-key-mac', '404142434445464748494A4B4C4D4E4F',
                      '-key-kek', '404142434445464748494A4B4C4D4E4F'], task1.args)
        assertEquals(['-d', '-a', '010203',
                      '-key-enc', '404142434445464748494A4B4C4D4E4F',
                      '-key-mac', '404142434445464748494A4B4C4D4E4F',
                      '-key-kek', '404142434445464748494A4B4C4D4E4F'], task2.args)
        assertEquals(['-d', '-a', '040506',
                      '-key-enc', '404142434445464748494A4B4C4D4E4F',
                      '-key-mac', '404142434445464748494A4B4C4D4E4F',
                      '-key-kek', '404142434445464748494A4B4C4D4E4F'], task3.args)

        assertEquals('pro.javacard.gp.GPTool', task1.main)
        assertEquals('pro.javacard.gp.GPTool', task2.main)
        assertEquals('pro.javacard.gp.GPTool', task3.main)

        //task1.exec()
    }

    @Test
    void validScriptWithDefaultKey() {
        runBuildTask(StaticConfig.VALID_SCRIPT_CONFIG_DEFAULT_KEY)

        Task task1 = project.getTasks().findByName('task1')
        Task task2 = project.getTasks().findByName('task2')
        Task task3 = project.getTasks().findByName('task3')

        assertTrue(task1 ? true : false)
        assertTrue(task2 ? true : false)
        assertTrue(task3 ? true : false)

        assertTrue(task1 instanceof JavaExec)
        assertTrue(task2 instanceof JavaExec)
        assertTrue(task3 instanceof JavaExec)

        assertEquals('global platform', task1.group)
        assertEquals('global platform', task2.group)
        assertEquals('global platform', task3.group)

        assertEquals(['-d',
                      '-a', '010203',
                      '-a', '040506',
                      '-key-enc', '404142434445464748494A4B4C4D4E4F',
                      '-key-mac', '404142434445464748494A4B4C4D4E4F',
                      '-key-kek', '404142434445464748494A4B4C4D4E4F'], task1.args)
        assertEquals(['-d', '-a', '010203',
                      '-key-enc', '404142434445464748494A4B4C4D4E4F',
                      '-key-mac', '404142434445464748494A4B4C4D4E4F',
                      '-key-kek', '404142434445464748494A4B4C4D4E4F'], task2.args)
        assertEquals(['-d', '-a', '040506',
                      '-key-enc', '404142434445464748494A4B4C4D4E4F',
                      '-key-mac', '404142434445464748494A4B4C4D4E4F',
                      '-key-kek', '404142434445464748494A4B4C4D4E4F'], task3.args)

        assertEquals('pro.javacard.gp.GPTool', task1.main)
        assertEquals('pro.javacard.gp.GPTool', task2.main)
        assertEquals('pro.javacard.gp.GPTool', task3.main)

        //task1.exec()
    }

    @Test
    void buildInstallMixedKey() {
        runBuildTask(StaticConfig.VALID_CONFIG_MIXED_KEY)
        JavaExec installTask = project.getTasks().getByName("installJavaCard")
        assertTrue(installTask ? true : false)
        assertTrue(installTask instanceof JavaExec)
        assertEquals('global platform', installTask.group)
        assertEquals(['-relax',
                      '--delete', '010203040506070809',
                      '--install', project.buildDir.absolutePath + File.separator + "javacard" + File.separator + "applet.cap",
                      '-key-enc', '414142434445464748494A4B4C4D4E4F',
                      '-key-mac', '404142434445464748494A4B4C4D4E4F',
                      '-key-kek', '424142434445464748494A4B4C4D4E4F'
        ], installTask.args)
        assertEquals(installTask.main, 'pro.javacard.gp.GPTool')
        //installTask.exec()
    }
}