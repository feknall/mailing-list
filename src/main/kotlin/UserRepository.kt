import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.util.logging.Logger

object UserRepository {

    private val logger = LoggerFactory.getLogger(UserRepository.javaClass)

    fun add(user: User) {
        transaction {
            logger.debug("Hey you")
            println("Hello World")
            val userId = UserTable.insert {
                it[nameCol] = user.name
            } get UserTable.idCol

            user.emailList?.let {
                user.emailList.forEach { address ->
                    EmailTable.insert {
                        it[addressCol] = address
                        it[userIdCol] = userId
                    }
                }
            }
        }
    }

    fun delete(userId: Int) {
        UserTable.deleteWhere {
            idCol eq userId
        }
    }

    fun getAll(): List<User> {
        var users = ArrayList<User>()
        transaction {
            users = UserTable
                .selectAll()
                .map {
                    User(it[UserTable.nameCol].toString())
                }
                .toCollection(ArrayList())
        }
        return users
    }

}