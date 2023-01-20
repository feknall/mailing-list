package bl


import dao.repository.UserRepository
import rest.AddUser
import rest.User
import java.util.*

object UserBl {
    fun addUser(user: AddUser): User {
        val userId = UserRepository.add(user.name)
        return User(userId, user.name, null)
    }

    fun getAllUsers(): List<User> {
        return UserRepository
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