package demo.eventsourcing.api

interface AccountQueryService {
    fun listEventsForAccount(accountNumber: String) : List<Any>
}