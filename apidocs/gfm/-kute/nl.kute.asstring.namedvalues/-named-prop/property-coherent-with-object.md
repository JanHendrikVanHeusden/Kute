//[Kute](../../../index.md)/[nl.kute.asstring.namedvalues](../index.md)/[NamedProp](index.md)/[propertyCoherentWithObject](property-coherent-with-object.md)

# propertyCoherentWithObject

[jvm]\
val [propertyCoherentWithObject](property-coherent-with-object.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Indicates whether the [property](property.md)'s value can be resolved on the given object

- 
   `true` if no breaking incoherence was found between object and [property](property.md)
- 
   `false` if a wrong (incoherent) combination of object and [property](property.md) was passed to the constructor that would break property value resolution; in that case, the result will always be `null`

When `false`, a log message is issued on construction of the [NamedProp](index.md), and the [value](value.md) will always be `null`, regardless on actual values of the object and the [property](property.md).
