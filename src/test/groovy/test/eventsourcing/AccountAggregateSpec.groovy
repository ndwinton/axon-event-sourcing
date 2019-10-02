package test.eventsourcing

import demo.eventsourcing.AccountActivatedEvent
import demo.eventsourcing.AccountAggregate
import demo.eventsourcing.AccountCreatedEvent
import demo.eventsourcing.AccountHeldEvent
import demo.eventsourcing.IncompatibleCurrencyException
import demo.eventsourcing.InsufficientFundsException
import demo.eventsourcing.MoneyCreditedEvent
import demo.eventsourcing.MoneyDebitedEvent
import demo.eventsourcing.Status
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
        aggregate.status == Status.ACTIVATED

        lifecyle.calls.size() == 0
    }

    def "Crediting a held account with sufficient to bring the balance positive should trigger reactivation"() {
        aggregate = new AccountAggregate('some-id', -100.00, 'GBP', Status.HELD, lifecyle.&apply)

        when:
        aggregate.on(new MoneyCreditedEvent('some-id', 200.00, 'GBP'))

        then:
        aggregate.accountBalance == 100.00
        aggregate.status == Status.HELD

        lifecyle.calls.size() == 1
        lifecyle.calls[0] instanceof AccountActivatedEvent
    }

    def "Crediting a held account with sufficient to bring the balance to zero should trigger reactivation"() {
        aggregate = new AccountAggregate('some-id', -100.00, 'GBP', Status.HELD, lifecyle.&apply)

        when:
        aggregate.on(new MoneyCreditedEvent('some-id', 100.00, 'GBP'))

        then:
        aggregate.accountBalance == 0.00
        aggregate.status == Status.HELD

        lifecyle.calls.size() == 1
        lifecyle.calls[0] instanceof AccountActivatedEvent
    }

    def "Crediting a held account with insufficient to bring the balance positive should NOT trigger reactivation"() {
        aggregate = new AccountAggregate('some-id', -100.00, 'GBP', Status.HELD, lifecyle.&apply)

        when:
        aggregate.on(new MoneyCreditedEvent('some-id', 50.00, 'GBP'))

        then:
        aggregate.accountBalance == -50.00
        aggregate.status == Status.HELD

        lifecyle.calls.size() == 0
    }

    def "Debiting an active account with less than the positive balance positive should NOT trigger other events"() {
        aggregate = new AccountAggregate('some-id', 100.00, 'GBP', Status.ACTIVATED, lifecyle.&apply)

        when:
        aggregate.on(new MoneyDebitedEvent('some-id', 50.00, 'GBP'))

        then:
        aggregate.accountBalance == 50.00
        aggregate.status == Status.ACTIVATED

        lifecyle.calls.size() == 0
    }

    def "Debiting an active account with more than the positive balance positive should trigger a hold event"() {
        aggregate = new AccountAggregate('some-id', 100.00, 'GBP', Status.ACTIVATED, lifecyle.&apply)

        when:
        aggregate.on(new MoneyDebitedEvent('some-id', 110.00, 'GBP'))

        then:
        aggregate.accountBalance == -10.00
        aggregate.status == Status.ACTIVATED

        lifecyle.calls.size() == 1
        lifecyle.calls[0] instanceof AccountHeldEvent
    }

    def "Debiting a held account should throw an exception"() {
        aggregate = new AccountAggregate('some-id', -100.00, 'GBP', Status.HELD, lifecyle.&apply)

        when:
        aggregate.on(new MoneyDebitedEvent('some-id', 10.00, 'GBP'))

        then:
        thrown InsufficientFundsException
        aggregate.accountBalance == -100.00
        aggregate.status == Status.HELD

        lifecyle.calls.size() == 0
    }

    def "Money can only be credited or debited in the same currency with which the account was created"() {
        aggregate = new AccountAggregate('some-id', 100.00, 'GBP', Status.ACTIVATED, lifecyle.&apply)

        when:
        aggregate.on(new MoneyCreditedEvent('some-id', 10.00, 'EUR'))

        then:
        thrown IncompatibleCurrencyException

        and:

        when:
        aggregate.on(new MoneyDebitedEvent('some-id', 10.00, "EUR"))

        then:
        thrown IncompatibleCurrencyException

        aggregate.accountBalance == 100.00
        aggregate.status == Status.ACTIVATED

        lifecyle.calls.size() == 0
    }

}
