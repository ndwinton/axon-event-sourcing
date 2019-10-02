package demo.eventsourcing

interface AccountAggregateSource {
    fun load(id: String) : AccountAggregate
}