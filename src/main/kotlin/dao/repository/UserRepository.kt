package dao.repository

import dao.User
import org.slf4j.LoggerFactory

class UserRepository {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun add(nameArg: String): Int {
        logger.debug("X")
        val userId = User.new {
            name_ = nameArg
        }.id.value
        logger.debug("New user added successfully. userId: {}", userId)
        return userId
    }

    fun get(userId: Int): User? {
        return User.findById(userId)
    }

    fun getAll(): List<User> {
        return User.all().toCollection(ArrayList())
    }

}