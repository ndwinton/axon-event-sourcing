package test.eventsourcing

import demo.eventsourcing.AccountActivatedEvent
import demo.eventsourcing.AccountAggregate
import demo.eventsourcing.AccountCreatedEvent
import demo.eventsourcing.MoneyCreditedEvent
import demo.eventsourcing.Status
import org.axonframework.modelling.command.ApplyMore
import spock.lang.Specification

class AccountAggregateSpec extends Specification {
    final static ACCOUNT_ID = UUID.randomUUID().toString()
    AccountAggregate aggregate
    def lifecyle = new MockLifecycle()


    def "Initial status of aggregate should be UNKNOWN"() {
        given:
        aggregate = new AccountAggregate(apply: lifecyle.&apply)

        expect:
        aggregate.status == Status.UNKNOWN
    }

    def "Creation event should allocate account number and set initial status, currency and balance"() {
        given:
        aggregate = new AccountAggregate(apply: lifecyle.&apply)

        when:
        aggregate.on(new AccountCreatedEvent(ACCOUNT_ID, 123.45, "GBP"))

        then:
        aggregate.id =~ /[-a-f0-9]+/
        aggregate.status == Status.CREATED
        aggregate.currency == 'GBP'
        aggregate.accountBalance == 123.45

        lifecyle.calls.size() == 1
        lifecyle.calls[0] instanceof AccountActivatedEvent
    }

    def "Crediting an active account should add to the balance"() {
        given:
        aggregate = new AccountAggregate('some-id', 123.45, 'GBP', Status.ACTIVATED, lifecyle.&apply)

        when:
        aggregate.on(new MoneyCreditedEvent('some-id', 100.00, 'GBP'))

        then:
        aggregate.accountBalance == 223.45

    }
}
