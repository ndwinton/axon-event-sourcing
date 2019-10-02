## Event sourcing with Axon and Spring Boot

This is as simple demonstration of
[event sourcing](https://martinfowler.com/eaaDev/EventSourcing.html)
using the open source [Axon framework](https://axoniq.io/product-overview/axon-framework)
and [Spring Boot](https://spring.io/projects/spring-boot).

The code is inspired by a Java implementation at
https://github.com/dashsaurabh/event-sourcing-cqrs-axon-spring-boot
as described in [this blog post](http://progressivecoder.com/implementing-event-sourcing-using-axon-and-spring-boot-part-1/).
This version, however, uses Kotlin as the primary language, Gradle for build and
Spock for unit testing.

There are other examples of using Kotlin with Axon, but I find the best way to learn
is to try things myself. So, don't take this as an idiomatic example of either
Kotlin or Axon! Note also that this isn't using the Axon server and all data is
in an in-memory H2 database.

Official Axon example applications are available from their website and at
https://github.com/AxonFramework.

