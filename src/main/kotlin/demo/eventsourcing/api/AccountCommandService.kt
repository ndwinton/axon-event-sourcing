package demo.eventsourcing.api

import demo.eventsourcing.CurrencyAmount
import java.util.concurrent.CompletableFuture

interface AccountCommandService {
    fun createAccount(startingBalance: CurrencyAmount) : CompletableFuture<String>
    fun creditMoneyToAccount(accountNumber: String, amount: CurrencyAmount) : CompletableFuture<String>
    fun debitMoneyFromAccount(accountNumber: String, amount: CurrencyAmount) : CompletableFuture<String>
}