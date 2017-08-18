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

package fr.bmartel.javacard.extension

/**
 * JavaCard extension object (the same as defined in https://github.com/martinpaljak/ant-javacard#syntax
 *
 * @author Bertrand Martel
 */
class JavaCard {

    Config config

    Scripts scripts

    Config config(Closure closure) {
        def someConfig = new Config()
        closure.delegate = someConfig
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        config = someConfig
        return someConfig
    }

    Scripts scripts(Closure closure) {
        def someScript = new Scripts()
        closure.delegate = someScript
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        scripts = someScript
        return someScript
    }

    /**
     * Validate fields
     */
    def validate() {
        config.validate()
    }
}