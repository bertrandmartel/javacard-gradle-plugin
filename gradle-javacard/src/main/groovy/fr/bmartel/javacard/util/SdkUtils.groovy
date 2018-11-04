package fr.bmartel.javacard.util

import org.slf4j.Logger

import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * Javacard SDK utils taken from ant-javacard(https://github.com/martinpaljak/ant-javacard) by Martin Paljak
 */
class SdkUtils {

    static enum JC {
        NONE, V212, V221, V222, V3
    }

    static class JavaCardKit {
        JC version = JC.NONE
        String path = null
    }

    /**
     * Get api classpath depending on SDK version
     *
     * @param extensionKit path to JavaCard SDK
     * @return api jar classpath
     */
    static getApiPath(extensionKit, Logger logger) {
        def jckit = SdkUtils.detectSDK(extensionKit, logger)

        String api = null;
        if (jckit.version == SdkUtils.JC.V3) {
            api = Paths.get(jckit.path, "lib", "api_classic.jar").toAbsolutePath().toString()
        } else if (jckit.version == SdkUtils.JC.V212) { // V2.1.X
            api = Paths.get(jckit.path, "lib", "api21.jar").toAbsolutePath().toString()
        } else { // V2.2.X
            api = Paths.get(jckit.path, "lib", "api.jar").toAbsolutePath().toString()
        }
        return api
    }

    /**
     * Given a path, return a meta-info object about possible JavaCard SDK in that path.
     *
     * @param path raw string as present in build.xml or environment, or <code>null</code>
     *
     * @return a {@link JavaCardKit} instance
     */
    static JavaCardKit detectSDK(String path, Logger logger) {
        JavaCardKit detected = new JavaCardKit()
        if (path == null || path.trim() == "") {
            return detected
        }
        // Expand user
        String real_path = path.replaceFirst("^~", System.getProperty("user.home"))
        // Check if path is OK
        if (!new File(real_path).exists()) {
            logger.debug("JavaCard SDK folder " + path + " does not exist!")
            return detected
        }
        detected.path = real_path
        // Identify jckit type
        if (Paths.get(detected.path, "lib", "tools.jar").toFile().exists()) {
            logger.debug("JavaCard 3.x SDK detected in " + detected.path)
            detected.version = JC.V3
        } else if (Paths.get(detected.path, "lib", "api21.jar").toFile().exists()) {
            detected.version = JC.V212;
            logger.debug("JavaCard 2.1.x SDK detected in " + detected.path)
        } else if (Paths.get(detected.path, "lib", "converter.jar").toFile().exists()) {
            // Detect if 2.2.1 or 2.2.2
            File api = Paths.get(detected.path, "lib", "api.jar").toFile()

            try {
                ZipInputStream zip = new ZipInputStream(new FileInputStream(api))
                while (true) {
                    ZipEntry entry = zip.getNextEntry()
                    if (entry == null) {
                        break
                    }
                    if (entry.getName().equals("javacardx/apdu/ExtendedLength.class")) {
                        detected.version = JC.V222;
                        logger.debug("JavaCard 2.2.2 SDK detected in " + detected.path)
                    }
                }
            } catch (IOException e) {
                logger.debug("Could not parse api.jar")
            } finally {
                // Assume older SDK if jar parsing fails.
                if (detected.version == JC.NONE) {
                    detected.version = JC.V221
                    logger.debug("JavaCard 2.x SDK detected in " + detected.path)
                }
            }
        } else {
            logger.debug("Could not detect a JavaCard SDK in " + Paths.get(path).toAbsolutePath())
        }
        return detected
    }
}