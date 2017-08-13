# JavaCard Gradle plugin

[![Build Status](https://travis-ci.org/bertrandmartel/javacard-gradle-plugin.svg?branch=master)](https://travis-ci.org/bertrandmartel/javacard-gradle-plugin)
[![Download](https://api.bintray.com/packages/bertrandmartel/maven/gradle-javacard/images/download.svg) ](https://bintray.com/bertrandmartel/maven/gradle-javacard/_latestVersion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/fr.bmartel/gradle-javacard/badge.svg)](https://maven-badges.herokuapp.com/maven-central/fr.bmartel/gradle-javacard)
[![Coverage Status](https://coveralls.io/repos/github/bertrandmartel/javacard-gradle-plugin/badge.svg?branch=master)](https://coveralls.io/github/bertrandmartel/javacard-gradle-plugin?branch=master)
[![Javadoc](http://javadoc-badge.appspot.com/fr.bmartel/gradle-javacard.svg?label=javadoc)](http://javadoc-badge.appspot.com/fr.bmartel/gradle-javacard)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

A Gradle plugin for building JavaCard applets.

This plugin is a wrapper on [ant-javacard](https://github.com/martinpaljak/ant-javacard) project and is inspired by [gradle-javacard](https://github.com/fidesmo/gradle-javacard)

## Usage 

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'fr.bmartel:gradle-javacard:1.0.0'
    }
}

apply plugin: 'javacard'

javacard {
    cap {
        packageName 'fr.bmartel.javacard'
        version '0.1'
        aid '01:02:03:04:05:06:07:08:09'
        output 'applet.cap'
        applet {
            className 'fr.bmartel.javacard.HelloSmartcard'
            aid '01:02:03:04:05:06:07:08:09:01:02'
        }
    }
}
```

plugin is available from `jcenter()` or `mavenCentral()`

## JavaCard SDK path

The path to JavaCard SDK can be specified through : 

* *[Recommended]* use `jc.home` properties in `local.properties` file located in your project root (in the same way as Android projects) : 
  * in project root : `echo "jc.home=$PWD/oracle_javacard_sdks/jc222_kit" >> local.properties`
* `JC_HOME` global environment variable, for instance using : `export JC_HOME="$PWD/sdks/jck222_kit"`
* using `jckit` attribute (see [ant-javacard](https://github.com/martinpaljak/ant-javacard#syntax))

## More complex example

```groovy
apply plugin: 'javacard'

javacard {
    jckit "/path/to/oracle_javacard_sdks/jc222_kit"
    cap {
        packageName 'fr.bmartel.javacard'
        version '0.1'
        aid '01:02:03:04:05:06:07:08:09'
        output 'applet1.cap'
        applet {
            className 'fr.bmartel.javacard.HelloSmartcard'
            aid '01:02:03:04:05:06:07:08:09:01:02'
        }
        applet {
            className 'fr.bmartel.javacard.GoodByeSmartCard'
            aid '01:02:03:04:05:06:07:08:09:01:03'
        }
    }
    cap {
        packageName 'fr.bmartel.javacard'
        version '0.1'
        aid '01:02:03:04:05:06:07:08:0A'
        output 'applet2.cap'
        applet {
            className 'fr.bmartel.javacard.SomeOtherClass'
            aid '01:02:03:04:05:06:07:08:09:01:04'
        }
        importResource {
            jar '/path/to/dependency.jar'
            exps '/path/to/some.exp'
        }
    }
}
```

## Syntax

* javacard [Closure]
  * jckit [String] - path to the JavaCard SDK that is used if individual cap does not specify one. Optional if cap defines one, required otherwise
  * cap [Closure] - construct a CAP file **Required**
    * jckit [String] - path to the JavaCard SDK to be used for this CAP. *Optional if javacard defines one, required otherwise*
    * sources [String] - path to Java source code, to be compiled against the current JavaCard SDK. **Required**
    * classes [String] - path to pre-compiled class files to be assembled into a CAP file. If both classes and sources are specified, compiled class files will be put to classes folder, which is created if missing
    * packageName [String] - name of the package of the CAP file. Optional - set to the parent package of the applet class if left unspecified.
    * version [String] - version of the package. Optional - defaults to 0.0 if left unspecified.
    * aid [String] - AID (hex) of the package. Recommended - or set to the 5 first bytes of the applet AID if left unspecified.
    * output [String] - path where to save the generated CAP file. if a filename or a non-absolute path is referenced, the output will be in `build/javacard/{output}` **Required**
    * export [String] - path (folder) where to place the JAR and generated EXP file. Default output directory is `build/javacard`. Filename depends on `output` filename if referenced. Optional.
    * jca [String] - path where to save the generated JavaCard Assembly (JCA) file. Default output directory is `build/javacard`. Filename depends on `output` filename if referenced. Optional.
    * verify [boolean] - if set to false, disables verification of the resulting CAP file with offcardeverifier. Optional.
    * debug [boolean] - if set to true, generates debug CAP components. Optional.
    * ints [boolean] - if set to true, enables support for 32 bit int type. Optional.
    * applet [Closure] - for creating an applet inside the CAP
      * className [String] - class of the Applet where install() method is defined. **Required**
      * aid [String] - AID (hex) of the applet. Recommended - or set to package aid+i where i is index of the applet definition in the build.xml instruction
    * importResource [Closure] - for linking against external components/libraries, like GPSystem or OPSystem
      * exps [String] - path to the folder keeping .exp files. Required
      * jar [String] - path to the JAR file for compilation. Optional - only required if using sources mode and not necessary with classes mode if java code is already compiled

## License

The MIT License (MIT) Copyright (c) 2017 Bertrand Martel