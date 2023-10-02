---
title: logger
---
//[kute](../../index.html)/[nl.kute.logging](index.html)/[logger](logger.html)



# logger



[jvm]\
var [logger](logger.html): ([String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)



Static [logger](logger.html). By default, wen no other logger set explicitly, Kute uses stdOutLogger to output to std out (using `println()`).



A different logger (typically SLF4J etc.) can be injected to have it send error logs to your logging framework.



- 
   To be used in **Kotlin**.
- 
   In **Java** code, use [KuteLogConsumer.setLogConsumer](-kute-log-consumer/-companion/set-log-consumer.html) instead (more convenient in Java)




Typical usage for **Kotlin** with an SLF4J compatible logger might be:

```kotlin
private val kuteLogger = Logger.getLogger("nl.kute")
nl.kute.logging.logger = { msg -> kuteLogger.error(msg) }
```


#### See also


| |
|---|
| [KuteLogConsumer.Companion.setLogConsumer](-kute-log-consumer/-companion/set-log-consumer.html) |



