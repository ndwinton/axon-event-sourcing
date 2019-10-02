package demo.eventsourcing

import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.eventsourcing.eventstore.EventStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AxonConfig {

    @Bean
    fun accountAggregateEventSourcingRepository(eventStore: EventStore) : EventSourcingRepository<AccountAggregate> =
        EventSourcingRepository.builder(AccountAggregate::class.java).eventStore(eventStore).build()
}