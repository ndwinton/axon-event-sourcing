package demo.eventsourcing.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component

@Component
interface AccountRepository : CrudRepository<AccountRecord,String> {
}