package demo.eventsourcing

sealed class BaseEvent()

data class AccountCreatedEvent(val id: String, val accountBalance: Double, val currency: String) : BaseEvent()

data class MoneyCreditedEvent(val id: String, val creditAmount: Double, val currency: String) : BaseEvent()

data class MoneyDebitedEvent(val id: String, val debitAmount: Double, val currency: String) : BaseEvent()

data class AccountActivatedEvent(val id: String) : BaseEvent()

data class AccountHeldEvent(val id: String) : BaseEvent()

