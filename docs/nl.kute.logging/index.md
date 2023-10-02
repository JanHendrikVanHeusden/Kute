---
title: nl.kute.logging
---
//[kute](../../index.html)/[nl.kute.logging](index.html)



# Package-level declarations



## Types


| Name | Summary |
|---|---|
| [KuteLogConsumer](-kute-log-consumer/index.html) | [jvm]<br>class [KuteLogConsumer](-kute-log-consumer/index.html)<br>To be used in **Java** code to redirect Kute's log output to a consumer |


## Properties


| Name | Summary |
|---|---|
| [logger](logger.html) | [jvm]<br>var [logger](logger.html): ([String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Static [logger](logger.html). By default, wen no other logger set explicitly, Kute uses stdOutLogger to output to std out (using `println()`). |


## Functions


| Name | Summary |
|---|---|
| [log](log.html) | [jvm]<br>fun [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?.[log](log.html)(msg: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?)<br>Logs message [msg](log.html) to loggerWithCaller, prefixed by the receiver's class name |

