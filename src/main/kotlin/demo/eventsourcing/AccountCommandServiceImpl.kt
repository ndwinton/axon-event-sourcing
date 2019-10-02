package demo.eventsourcing

import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class AccountCommandServiceImpl(val commandGateway: CommandGateway) : AccountCommandService {
    override fun createAccount(startingBalance: CurrencyAmount): CompletableFuture<String> =
         commandGateway.send(
            CreateAccountCommand(
                UUID.randomUUID().toString(),
                startingBalance.amount,
                startingBalance.currency
            )
        )

    override fun creditMoneyToAccount(accountNumber: String, amount: CurrencyAmount): CompletableFuture<String> =
        commandGateway.send(CreditMoneyCommand(accountNumber, amount.amount, amount.currency))


    override fun debitMoneyFromAccount(accountNumber: String, amount: CurrencyAmount): CompletableFuture<String> =
        commandGateway.send(DebitMoneyCommand(accountNumber, amount.amount, amount.currency))
}