package demo.eventsourcing

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/account")
class AccountQueryController(val accountQueryService: AccountQueryService) {

    @GetMapping("{accountNumber}/events")
    fun listEvents(@PathVariable accountNumber: String) : List<Any> =
        accountQueryService.listEventsForAccount(accountNumber)
}