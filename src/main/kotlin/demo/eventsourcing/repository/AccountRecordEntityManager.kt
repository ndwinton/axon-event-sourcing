package demo.eventsourcing.repository

import demo.eventsourcing.*
import org.axonframework.eventsourcing.EventSourcingHandler
import org.springframework.stereotype.Component

@Component
class AccountRecordEntityManager(
    val accountRepository: AccountRepository,
    val accountAggregateSource: AccountAggregateSource
) {

    @EventSourcingHandler
    fun on(event: BaseEvent) {
        val id = extractEventId(event)
        val aggregate = accountAggregateSource.load(id)
        accountRepository.save(AccountRecord(id, aggregate.accountBalance, aggregate.currency, aggregate.status.name))
    }

    private fun extractEventId(event: BaseEvent) =
        when (event) {
            is AccountCreatedEvent -> event.id
            is MoneyCreditedEvent -> event.id
            is MoneyDebitedEvent -> event.id
            is AccountActivatedEvent -> event.id
            is AccountHeldEvent -> event.id
        }

}