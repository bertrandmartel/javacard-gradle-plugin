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

package fr.bmartel.javacard.util

/**
 * Some utility functions
 *
 * @author Bertrand Martel
 */
class Utility {

    /**
     * Remove extension from file path but keep the path
     * https://stackoverflow.com/a/34321971/2614364
     *
     * @param file path
     * @return
     */
    static String removeExtension(file) {
        return file[0..<file.lastIndexOf('.')]
    }

    /**
     * Get only the filename and without the extension
     * https://stackoverflow.com/a/6541137/2614364
     *
     * @param file
     * @return
     */
    static String getFileNameWithoutExtension(file) {
        return file.lastIndexOf('.').with { it != -1 ? file[0..<it] : file }
    }

    /**
     * Create a folder if not existing.
     *
     * @param folderPath
     */
    static void createFolder(folderPath) {
        def folder = new File(folderPath)
        if (!folder.exists()) {
            folder.mkdirs()
        }
    }
}