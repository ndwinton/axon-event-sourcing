package demo.eventsourcing

import org.axonframework.modelling.command.TargetAggregateIdentifier

sealed class BaseCommand

data class CreateAccountCommand(@TargetAggregateIdentifier
                                val id: String,
                                val accountBalance:
                                Double, val currency: String) : BaseCommand()

data class CreditMoneyCommand(@TargetAggregateIdentifier
                              val id: String,
                              val creditAmount: Double,
                              val currency: String) : BaseCommand()

data class DebitMoneyCommand(@TargetAggregateIdentifier
                             val id: String,
                             val debitAmount: Double,
                             val currency: String) : BaseCommand()