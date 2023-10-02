---
title: propertyCoherentWithObject
---
//[kute](../../../index.html)/[nl.kute.asstring.namedvalues](../index.html)/[NamedProp](index.html)/[propertyCoherentWithObject](property-coherent-with-object.html)



# propertyCoherentWithObject



[jvm]\
val [propertyCoherentWithObject](property-coherent-with-object.html): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)



Indicates whether the [property](property.html)'s value can be resolved on the given object



- 
   `true` if no breaking incoherence was found between object and [property](property.html)
- 
   `false` if a wrong (incoherent) combination of object and [property](property.html) was passed to the constructor that would break property value resolution; in that case, the result will always be `null`




When `false`, a log message is issued on construction of the [NamedProp](index.html), and the [value](value.html) will always be `null`, regardless on actual values of the object and the [property](property.html).




