# JavaCard Gradle plugin

[![Build Status](https://travis-ci.org/bertrandmartel/javacard-gradle-plugin.svg?branch=master)](https://travis-ci.org/bertrandmartel/javacard-gradle-plugin)
[![Download](https://api.bintray.com/packages/bertrandmartel/maven/gradle-javacard/images/download.svg) ](https://bintray.com/bertrandmartel/maven/gradle-javacard/_latestVersion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/fr.bmartel/gradle-javacard/badge.svg)](https://maven-badges.herokuapp.com/maven-central/fr.bmartel/gradle-javacard)
[![Coverage Status](https://coveralls.io/repos/github/bertrandmartel/javacard-gradle-plugin/badge.svg?branch=master)](https://coveralls.io/github/bertrandmartel/javacard-gradle-plugin?branch=master)
[![Javadoc](http://javadoc-badge.appspot.com/fr.bmartel/gradle-javacard.svg?label=javadoc)](http://javadoc-badge.appspot.com/fr.bmartel/gradle-javacard)
[![License](http://img.shields.io/:license-mit-blue.svg)](LICENSE.md)

A Gradle plugin for building JavaCard applets.

This plugin is a wrapper on [ant-javacard](https://github.com/martinpaljak/ant-javacard) and [Global Platform Pro](https://github.com/martinpaljak/GlobalPlatformPro), it is inspired by [gradle-javacard](https://github.com/fidesmo/gradle-javacard)

## Features

* build JavaCard applets (with the same capabilities as [ant-javacard](https://github.com/martinpaljak/ant-javacard))
* install cap files
* list applets
* write quick testing scripts used to send apdu in a configurable way
* expose `GpExec` task type that enables usage of [Global Platform Pro](https://github.com/martinpaljak/GlobalPlatformPro) tool inside Gradle
* include [jcardsim 3.0.4](https://github.com/licel/jcardsim) and [JUnit 4.12](http://junit.org/junit4/) test dependency (clear distinction between JavaCard SDK & jcardsim SDK) 
* ability to specify key for delete/install/list tasks
* possibility to add dependency between modules (exp & jar imported automatically)

## Usage 

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'fr.bmartel:gradle-javacard:1.5.3'
    }
}

apply plugin: 'javacard'

javacard {

    config {
        
        cap {
            packageName 'fr.bmartel.javacard'
            version '0.1'
            aid '01:02:03:04:05:06:07:08:09'
            output 'applet.cap'
            applet {
                className 'fr.bmartel.javacard.HelloWorld'
                aid '01:02:03:04:05:06:07:08:09:01:02'
            }
        }
    }
          
    scripts {
        script {
            name 'select'
            apdu '00:A4:04:00:0A:01:02:03:04:05:06:07:08:09:01:00'
        }
        script {
            name 'hello'
            apdu '00:40:00:00:00'
        }
        task {
            name 'sendHello'
            scripts 'select', 'hello'
        }
    }
}
```

plugin is available from `jcenter()` or `mavenCentral()`

Check [this project](https://github.com/bertrandmartel/javacard-tutorial) for more usage examples

## JavaCard SDK path

The path to JavaCard SDK can be specified through : 

* use `jc.home` properties in `local.properties` file located in your project root (in the same way as Android projects) : 
  * in project root : `echo "jc.home=$PWD/oracle_javacard_sdks/jc222_kit" >> local.properties`
* using `jckit` attribute (see [ant-javacard](https://github.com/martinpaljak/ant-javacard#syntax))
* `JC_HOME` global environment variable, for instance using : `export JC_HOME="$PWD/sdks/jck222_kit"`

## Tasks

| task name    | description   |
|--------------|---------------|
| buildJavaCard | build JavaCard cap files |
| installJavaCard | delete existing aid & install all JavaCard cap files (`gp --delete XXXX --install file.cap`) |
| listJavaCard | list applets (`gp -l`) |

It's possible to create custom tasks that will send series of custom apdu :

```groovy
scripts {
    script {
        name 'select'
        apdu '00:A4:04:00:0A:01:02:03:04:05:06:07:08:09:01:00'
    }
    script {
        name 'hello'
        apdu '00:40:00:00:00'
    }
    task {
        name 'sendHello'
        scripts 'select', 'hello'
    }
}
```

The above will create task `sendHello` that will select applet ID `01:02:03:04:05:06:07:08:09:01` and send the apdu `00:40:00:00:00`.  
The order of the scripts's apdu in `task.scripts` is respected.  
`00:A4:04:00:0A:01:02:03:04:05:06:07:08:09:01:00` or `'00A404000A0102030405060708090100'` are valid apdu.

## Custom Global Platform Pro task

You can build custom tasks that launch [Global Platform Pro](https://github.com/martinpaljak/GlobalPlatformPro) tool :

```groovy
task displayHelp(type: fr.bmartel.javacard.gp.GpExec) {
    description = 'display Global Platform pro help'
    group = 'help'
    args '-h'
}
```

## More complex example

```groovy
apply plugin: 'javacard'

repositories {
    maven {
        url 'http://dl.bintray.com/bertrandmartel/maven'
    }
}

javacard {

    config {
        jckit '../oracle_javacard_sdks/jc222_kit'
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
            dependencies {
                local {
                    jar '/path/to/dependency.jar'
                    exps '/path/to/expfolder'
                }
                remote 'fr.bmartel:gplatform:2.1.1'
            }
        }
    }
    
    defaultKey '40:41:42:43:44:45:46:47:48:49:4A:4B:4C:4D:4E:4F'
    // or 
    /*
    key {
        enc '40:41:42:43:44:45:46:47:48:49:4A:4B:4C:4D:4E:4F'
        kek '40:41:42:43:44:45:46:47:48:49:4A:4B:4C:4D:4E:4F' 
        mac '40:41:42:43:44:45:46:47:48:49:4A:4B:4C:4D:4E:4F' 
    }
    */

    scripts {
        script {
            name 'select'
            apdu '00:A4:04:00:0A:01:02:03:04:05:06:07:08:09:01:00'
        }
        script {
            name 'hello'
            apdu '00:40:00:00:00'
        }
        task {
            name 'sendHello'
            scripts 'select', 'hello'
        }
    }
}
```

Note1 : the `remote` dependency will automatically download the `jar` (the `jar` file must include the `exp` file)  
Note2 : you can add as many `local` or `remote` dependency as you want

## Syntax

* javacard [Closure]
  * config [Closure] - object that holds build configuration **Required**
    * jckit [String] - path to the JavaCard SDK that is used if individual cap does not specify one. Optional if cap defines one, required otherwise. The path is relative to the module
    * jcardSim [String|Map] - optional JcardSim dependency definition, requires addImplicitJcardSim=true
    * addSurrogateJcardSimRepo [Boolean] - if true (default) the surrogate maven repo with JcardSim 3.0.4 is added
    * addImplicitJcardSim [Boolean] - if true (default) the JcardSim is added to the test target by the plugin. If set to false user can specify own JcardSim dependency, such as: `jcardsim 'com.licel:jcardsim:3.0.4'`. 
    * addImplicitJcardSimJunit [Boolean] - if true (default) the JcardSim dependency junit is added by the plugin.
    * logLevel [String] - log level of ant-javacard task ("VERBOSE","DEBUG","INFO","WARN","ERROR"). default : "INFO"
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
      * dependencies [Closure] - for linking against external components/libraries, like GPSystem or OPSystem
        * local [Closure] local dependencies must include absolute path to exp/jar
          * exps [String] - path to the folder keeping .exp files. Required
          * jar [String] - path to the JAR file for compilation. Optional - only required if using sources mode and not necessary with classes mode if java code is already compiled
        * remote [String] remote dependencies (ex: "group:module:1.0").the remote repository (maven repo) must be included in the project
  * key [Closure] key configuration (if not defined the default keys will be used)
    * enc [String] ENC key
    * kek [String] KEK key
    * mac [String] MAC key
  * defaultKey [String] default key used (will be used for enc, kek and mac key if not specified in key closure)
  * scripts [Closure] - object that holds the configurable scripts to send apdu
     * script [Closure] - a script referenced by name/apdu value to be sent
       * name [String] - script name (ex: select)
       * apdu [String] - apdu value to be sent (it can hold ":" to separate bytes)
     * task [Closure] - gradle task to create that will map the specified list of apdu to send
       * name [String] - task name
       * scripts [String...] - list of script's name

## Compatibility

This plugin has been tested on following IDE : 

* IntelliJ IDEA
* Android Studio
* Eclipse

Recommended IDE : IntelliJ IDEA or Android Studio

## License

The MIT License (MIT) Copyright (c) 2017 Bertrand Martel
