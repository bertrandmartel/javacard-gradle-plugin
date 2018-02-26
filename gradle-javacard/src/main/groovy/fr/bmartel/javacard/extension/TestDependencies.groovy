package fr.bmartel.javacard.extension

class TestDependencies {

    /**
     * list of dependencies to compile.
     */
    List<String> dependencies = []

    void compile(String dependency) {
        dependencies.add(dependency)
    }
}
