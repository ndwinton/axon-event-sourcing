package demo.eventsourcing.api

import demo.eventsourcing.CurrencyAmount
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

@RestController
@RequestMapping("/account")
class AccountCommandController(val accountCommandService: AccountCommandService) {

    @PostMapping
    fun createAccount(@RequestBody startingBalance: CurrencyAmount) : CompletableFuture<String> =
        accountCommandService.createAccount(startingBalance)

    @PutMapping("{accountNumber}/credit")
    fun creditAccount(@PathVariable accountNumber: String, @RequestBody amount: CurrencyAmount) : CompletableFuture<String> =
        accountCommandService.creditMoneyToAccount(accountNumber, amount)

    @PutMapping("{accountNumber}/debit")
    fun debitAccount(@PathVariable accountNumber: String, @RequestBody amount: CurrencyAmount) : CompletableFuture<String> =
        accountCommandService.debitMoneyFromAccount(accountNumber, amount)
}