package bl


import dao.repository.UserRepository
import org.jetbrains.exposed.sql.transactions.transaction
import rest.AddUser
import rest.Email
import rest.User
import java.util.*

class UserBl(private val userRepository: UserRepository) {

    fun addUser(user_: AddUser): User {
        return transaction {
            val user = userRepository.add(user_.name)
            User(user.id.value, user_.name, null)
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
                                Email(email.id.value, email.emailAddress_)
                            }
                            .toCollection(LinkedList())
                    )
                }
        }
    }
}