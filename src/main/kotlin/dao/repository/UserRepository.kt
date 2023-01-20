package dao.repository

import dao.User
import org.slf4j.LoggerFactory

object UserRepository {

    private val logger = LoggerFactory.getLogger(UserRepository.javaClass)

    fun add(nameArg: String): Int {
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