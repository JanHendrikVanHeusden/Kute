//[Kute](../../../index.md)/[nl.kute.asstring.annotation.option](../index.md)/[AsStringClassOption](index.md)/[includeIdentityHash](include-identity-hash.md)

# includeIdentityHash

[jvm]\
val [includeIdentityHash](include-identity-hash.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

#### Parameters

jvm

| | |
|---|---|
| includeIdentityHash | Should the identity hash be included in output of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md)? If included, the identity hash is represented as `@` followed by the hex representation as of [System.identityHashCode](https://docs.oracle.com/javase/8/docs/api/java/lang/System.html#identityHashCode-kotlin.Any-) (up to 8 hex characters), identical to the hex string seen in non-overridden toString output.<br>Default = `false` by [initialIncludeIdentityHash](../../nl.kute.asstring.core.defaults/initial-include-identity-hash.md) |
