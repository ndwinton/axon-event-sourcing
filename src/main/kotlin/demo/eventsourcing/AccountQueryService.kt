package demo.eventsourcing

interface AccountQueryService {
    fun listEventsForAccount(accountNumber: String) : List<Any>
}