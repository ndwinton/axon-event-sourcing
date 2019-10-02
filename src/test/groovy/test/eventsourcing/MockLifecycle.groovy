package test.eventsourcing

import org.axonframework.messaging.MetaData
import org.axonframework.modelling.command.ApplyMore

class MockLifecycle {
    def calls = []

    ApplyMore apply(Object obj) {
        println "apply($obj)"
        calls << obj
        null
    }

    ApplyMore apply(Object obj, MetaData metaData) {
        println "apply($obj, $metaData)"
        calls << [obj, metaData]
        null
    }
}
