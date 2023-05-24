package nl.kute.sample

import nl.kute.printable.annotation.PrintOption
import java.time.LocalDate

@PrintOption(showNullAs = "[null]")
class MyKotlinClassWithAnnotations {

    @PrintOption
    val myStringVal: String = "my string value"
    val myLocalDate: LocalDate = LocalDate.of(2023, 5, 24)
}
