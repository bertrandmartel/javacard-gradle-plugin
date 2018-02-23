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
 * Cap extension object (the same as defined in https://github.com/martinpaljak/ant-javacard#syntax
 *
 * @author Bertrand Martel
 */
class Cap {

    /**
     * path to the JavaCard SDK to be used for this CAP. Optional if javacard defines one, required otherwise.
     */
    String jckit

    /**
     * path to Java source code, to be compiled against the current JavaCard SDK. Either sources or classes is required.
     */
    String sources

    /**
     * If true the sources are determined automatically. The first existing source dir in source sets is taken.
     */
    boolean findSources = true

    /**
     * if true the first source dir from the source set is used. Otherwise the most recet (last).
     */
    boolean defaultSources = true

    /**
     * path to pre-compiled class files to be assembled into a CAP file. If both classes and sources are specified,
     * compiled class files will be put to classes folder, which is created if missing.
     */
    String classes

    /**
     * name of the package of the CAP file. Optional - set to the parent package of the applet class if left unspecified.
     */
    String packageName

    /**
     * version of the package. Optional - defaults to 0.0 if left unspecified.
     */
    String version

    /**
     * AID (hex) of the package. Recommended - or set to the 5 first bytes of the applet AID if left unspecified.
     */
    String aid

    /**
     * path where to save the generated CAP file. Required.
     */
    String output

    /**
     * path (folder) where to place the JAR and generated EXP file. Optional.
     */
    String export

    /**
     * path where to save the generated JavaCard Assembly (JCA) file. Optional.
     */
    String jca

    /**
     * if set to false, disables verification of the resulting CAP file with offcardeverifier. Optional.
     */
    boolean verify = true

    /**
     * if set to true, generates debug CAP components. Optional.
     */
    boolean debug = false

    /**
     * if set to true, enables support for 32 bit int type. Optional.
     */
    boolean ints = false

    /**
     * list of applets.
     */
    List<Applet> applets = []

    /**
     * dependencies
     */
    Dependencies dependencies

    Applet applet(Closure closure) {
        def someApplet = new Applet()
        closure.delegate = someApplet
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        applets.add(someApplet)
        someApplet
    }

    void dependencies(Closure closure) {
        def dependency = new Dependencies()
        closure.delegate = dependency
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        dependencies = dependency
        dependency
    }

    void jckit(String path) {
        this.jckit = path
    }

    void sources(String path) {
        this.sources = path
    }

    void findSources(Boolean findSources) {
        this.findSources = findSources
    }

    void defaultSources(Boolean defaultSources) {
        this.defaultSources = defaultSources
    }

    void packageName(String packageName) {
        this.packageName = packageName
    }

    void version(String version) {
        this.version = version
    }

    void aid(String aid) {
        this.aid = aid
    }

    void output(String output) {
        this.output = output
    }

    void export(String export) {
        this.export = export
    }

    void jca(String jca) {
        this.jca = jca
    }

    void verify(boolean verify) {
        this.verify = verify
    }

    void debug(boolean debug) {
        this.debug = debug
    }

    void ints(boolean ints) {
        this.ints = ints
    }
}