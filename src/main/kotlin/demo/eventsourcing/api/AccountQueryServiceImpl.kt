package demo.eventsourcing.api

import org.axonframework.eventsourcing.eventstore.EventStore
import org.springframework.stereotype.Service
import kotlin.streams.toList

@Service
class AccountQueryServiceImpl(val eventStore: EventStore) : AccountQueryService {
    override fun listEventsForAccount(accountNumber: String): List<Any> =
        eventStore.readEvents(accountNumber).asStream().map { it.payload }.toList()
}