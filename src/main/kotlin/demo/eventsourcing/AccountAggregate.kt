package demo.eventsourcing

import com.fasterxml.jackson.annotation.JsonIgnore
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.ApplyMore
import org.axonframework.spring.stereotype.Aggregate


@Aggregate
class AccountAggregate(
    @AggregateIdentifier
    var id: String? = null, // Must be null initially for Axon
    var accountBalance: Double = 0.0,
    var currency: String = "???",
    var status: Status = Status.UNKNOWN,
    @JsonIgnore // This property is only here to allow injection for unit tests
    var apply: (obj: Any) -> ApplyMore = AggregateLifecycle::apply
) {

    @CommandHandler
    constructor(command: CreateAccountCommand): this() {
        apply(AccountCreatedEvent(command.id, command.accountBalance, command.currency))
    }

// It seems to be possible to have a generic event handler that dispatches to
// more specific handlers, but this doesn't work with the command handlers

//    @CommandHandler
//    fun on(command: BaseCommand) {
//        when (command) {
//            is CreateAccountCommand -> handleCreateAccountCommand(command)
//            is CreditMoneyCommand -> handleCreditMoneyCommand(command)
//            is DebitMoneyCommand -> handleDebitMoneyCommand(command)
//        }
//    }

    @CommandHandler
    fun handleCreditMoneyCommand(command: CreditMoneyCommand) =
        apply(MoneyCreditedEvent(command.id, command.creditAmount, command.currency))

    @CommandHandler
    fun handleDebitMoneyCommand(command: DebitMoneyCommand) =
        apply(MoneyDebitedEvent(command.id, command.debitAmount, command.currency))

    @EventSourcingHandler
    fun on(event: BaseEvent) = when (event) {
        is AccountCreatedEvent -> handleAccountCreatedEvent(event)
        is MoneyCreditedEvent -> handleMoneyCreditedEvent(event)
        is MoneyDebitedEvent -> handleMoneyDebitedEvent(event)
        is AccountActivatedEvent -> handleAccountActivatedEvent(event)
        is AccountHeldEvent -> handleAccountHeldEvent(event)
    }

    private fun handleAccountActivatedEvent(event: AccountActivatedEvent) {
        this.status = Status.ACTIVATED
    }

    private fun handleAccountHeldEvent(event: AccountHeldEvent) {
        this.status = Status.HELD
    }

    private fun handleAccountCreatedEvent(event: AccountCreatedEvent) {
        this.id = event.id
        this.accountBalance = event.accountBalance
        this.currency = event.currency
        this.status = Status.CREATED

        apply(AccountActivatedEvent(event.id))
    }

    private fun handleMoneyCreditedEvent(event: MoneyCreditedEvent) {
        if (currency != event.currency) {
            throw IncompatibleCurrencyException("Credits can only be made in $currency, not ${event.currency}.")
        }

        if (this.status == Status.HELD && this.accountBalance + event.creditAmount >= 0.0) {
            apply(AccountActivatedEvent(event.id))
        }

        this.accountBalance += event.creditAmount
    }

    private fun handleMoneyDebitedEvent(event: MoneyDebitedEvent) {
        if (currency != event.currency) {
            throw IncompatibleCurrencyException("Debits can only be made in $currency, not ${event.currency}.")
        }

        if (this.status == Status.HELD) {
            throw InsufficientFundsException("Sorry, your account is currently $accountBalance in deficit. Please add more funds.")
        }

        if (this.accountBalance - event.debitAmount < 0.0) {
            apply(AccountHeldEvent(event.id))
        }

        this.accountBalance -= event.debitAmount
    }
}