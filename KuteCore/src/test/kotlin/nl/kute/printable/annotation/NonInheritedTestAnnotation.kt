package nl.kute.printable.annotation

annotation class NonInheritedTestAnnotation(val option: Option = Option.OPTION_1) {

    enum class Option {
        OPTION_1,
        OPTION_2
    }

}
