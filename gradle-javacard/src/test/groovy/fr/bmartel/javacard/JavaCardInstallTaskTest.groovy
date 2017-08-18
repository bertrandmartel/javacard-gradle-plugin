package fr.bmartel.javacard

import fr.bmartel.javacard.common.CommonTest
import org.gradle.api.tasks.JavaExec
import org.junit.Test

class JavaCardInstallTaskTest extends CommonTest {

    @Test
    void validBuildInstall() {
        runBuildTask(StaticConfig.VALID_CONFIG)
        JavaExec installTask = project.getTasks().getByName("installJavacard")
        installTask.exec()
    }
}