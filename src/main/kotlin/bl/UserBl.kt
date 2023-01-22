package bl


import dao.repository.UserRepository
import org.jetbrains.exposed.sql.transactions.transaction
import rest.AddUser
import rest.User
import java.util.*

class UserBl(private val userRepository: UserRepository) {

    fun addUser(user: AddUser): User {
        return transaction {
            val userId = userRepository.add(user.name)
            User(userId, user.name, null)
        }
    }

    fun getAllUsers(): List<User> {
        return transaction {
            userRepository
                .getAll()
                .map { user ->
                    User(user.id.value,
                        user.name_,
                        user
                            .emails_
                            .map { email ->
                                rest.Email(email.id.value, email.emailAddress_)
                            }
                            .toCollection(LinkedList())
                    )
                }
        }
    }
}