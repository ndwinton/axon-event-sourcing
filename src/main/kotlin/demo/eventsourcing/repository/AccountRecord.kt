package demo.eventsourcing.repository

import demo.eventsourcing.Status
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class AccountRecord(@Id var id : String = "",
                         var accountBalance: Double = 0.0,
                         var currency: String = "???",
                         var status: String = Status.UNKNOWN.name)