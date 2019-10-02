package test.eventsourcing.repository

import demo.eventsourcing.AccountActivatedEvent
import demo.eventsourcing.AccountAggregate
import demo.eventsourcing.AccountAggregateSource
import demo.eventsourcing.AccountCreatedEvent
import demo.eventsourcing.AccountHeldEvent
import demo.eventsourcing.BaseEvent
import demo.eventsourcing.MoneyCreditedEvent
import demo.eventsourcing.MoneyDebitedEvent
import demo.eventsourcing.Status
import demo.eventsourcing.repository.AccountRecordEntityManager
import demo.eventsourcing.repository.AccountRepository
import spock.lang.Specification
import spock.lang.Unroll

class AccountRecordEntityManagerUnitSpec extends Specification {
    AccountRepository accountRepository = Mock()
    AccountAggregateSource accountAggregateSource = Mock()

    AccountRecordEntityManager manager = new AccountRecordEntityManager(accountRepository, accountAggregateSource)

    @Unroll
    def "Handler shoud persist the latest state of the aggregate on all event types: #event"(BaseEvent event) {
        given:
        AccountAggregate aggregate = new AccountAggregate(event.id, 123.45, "GBP", Status.ACTIVATED)

        when:
        manager.on(event)

        then:
        1 * accountAggregateSource.load({it == event.id}) >> aggregate
        1 * accountRepository.save({
            it.id == event.id &&
                    it.accountBalance == aggregate.accountBalance &&
                    it.currency == aggregate.currency &&
                    it.status == aggregate.status.name()
        })

        where:
        event << [
                new AccountHeldEvent("held"),
                new AccountActivatedEvent("activated"),
                new AccountCreatedEvent("created", 123.45, "GBP"),
                new MoneyCreditedEvent("credited", 1.23, "GBP"),
                new MoneyDebitedEvent("debited", 4.56, "GBP")
        ]
    }
}
