package test.eventsourcing

import demo.eventsourcing.api.AccountCommandController
import demo.eventsourcing.api.AccountCommandService
import demo.eventsourcing.CurrencyAmount
import spock.lang.Specification

import java.util.concurrent.CompletableFuture

class AccountCommandControllerUnitSpec extends Specification {
    AccountCommandService mockService = Mock()
    AccountCommandController controller = new AccountCommandController(mockService)

    def "Create action should delegate to service"() {
        given:
        CurrencyAmount startingBalance = new CurrencyAmount(123.45, "GBP")

        when:
        def future = controller.createAccount(startingBalance)

        then:
        1 * mockService.createAccount(startingBalance) >> CompletableFuture.supplyAsync {"987654321"}
        future.get() == "987654321"
    }

    def "Credit action should delegate to service"() {
        given:
        def amount = new CurrencyAmount(1.23, "GBP")

        when:
        def future = controller.creditAccount("987654321", amount)

        then:
        1 * mockService.creditMoneyToAccount("987654321", amount)
    }

    def "Debit action should delegate to service"() {
        given:
        def amount = new CurrencyAmount(1.23, "GBP")

        when:
        def future = controller.debitAccount("987654321", amount)

        then:
        1 * mockService.debitMoneyFromAccount("987654321", amount)
    }
}
