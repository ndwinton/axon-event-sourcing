package demo.eventsourcing

class InsufficientFundsException(message: String?) : Exception(message)

class IncompatibleCurrencyException(message: String?) : Exception(message)