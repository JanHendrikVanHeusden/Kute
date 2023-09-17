package nl.kute.performance

import nl.kute.testobjects.performance.PropsToString

typealias ToStringTask = (PropsToString) -> String

val disabledWarning: String =
    """
       |   DISABLED!   DISABLED!   DISABLED!   DISABLED!   DISABLED!   DISABLED!
       |   (set enabled flag to run the test)""".trimIndent()
