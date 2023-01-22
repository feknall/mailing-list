package dao.repository

import dao.User
import org.slf4j.LoggerFactory

class UserRepository {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun add(nameArg: String): User {
        val user = User.new {
            name_ = nameArg
        }
        logger.debug("New user added successfully. userId: {}", user.id.value)
        return user
    }

    fun get(userId: Int): User? {
        return User.findById(userId)
    }

    fun getAll(): List<User> {
        return User.all().toCollection(ArrayList())
    }

}