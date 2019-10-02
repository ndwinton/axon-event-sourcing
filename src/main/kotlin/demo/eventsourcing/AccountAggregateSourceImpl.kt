package demo.eventsourcing

import org.axonframework.eventsourcing.EventSourcingRepository
import org.springframework.stereotype.Component

@Component
class AccountAggregateSourceImpl(val eventSourcingRepository: EventSourcingRepository<AccountAggregate>) : AccountAggregateSource {
    override fun load(id: String) = eventSourcingRepository.load(id).wrappedAggregate.aggregateRoot
}