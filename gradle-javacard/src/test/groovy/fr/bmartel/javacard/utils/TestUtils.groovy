package fr.bmartel.javacard.utils

import fr.bmartel.javacard.util.Utility

class TestUtils {

    public static File getFile(ext) {
        return new File(ext)
    }

    public static String getFileName(filePath, ext) {
        return Utility.removeExtension(filePath) + "." + ext
    }
}