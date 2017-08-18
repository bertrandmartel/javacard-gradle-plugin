package fr.bmartel.javacard.extension

/**
 * scripts used to configure apdu batch request from a gradle task.
 *
 * @author Bertrand Martel
 */
class Scripts {

    /**
     * list of scripts.
     */
    List<Script> scripts = []

    /**
     * list of tasks.
     */
    List<Task> tasks = []

    Script script(Closure closure) {
        def scriptInst = new Script()
        closure.delegate = scriptInst
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        scripts.add(scriptInst)
        scriptInst
    }

    Task task(Closure closure) {
        def taskInt = new Task()
        closure.delegate = taskInt
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        tasks.add(taskInt)
        taskInt
    }
}