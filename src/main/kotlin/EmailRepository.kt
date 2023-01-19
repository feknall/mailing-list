import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert

class EmailRepository {
    fun add(userId: Int, address: String) {
        EmailTable.insert {
            it[addressCol] = address
            it[userIdCol] = userId
        }
    }

    fun delete(userId: Int, emailId: String) {
        EmailTable.deleteWhere {
            (userIdCol eq userId) and (idCol eq userId)
        }
    }
}