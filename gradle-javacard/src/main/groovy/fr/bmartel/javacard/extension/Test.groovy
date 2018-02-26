package fr.bmartel.javacard.extension

class Test {

    TestDependencies dependencies

    void dependencies(Closure closure) {
        def dependency = new TestDependencies()
        closure.delegate = dependency
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        dependencies = dependency
        dependency
    }
}
