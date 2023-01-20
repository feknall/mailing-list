package dao.repository

import dao.Email
import dao.Emails
import dao.User
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.slf4j.LoggerFactory

object EmailRepository {

    private val logger = LoggerFactory.getLogger(EmailRepository.javaClass)

    fun add(user: User, emailAddress: String): Int {
        val emailId = Email.new {
            user_ = user
            emailAddress_ = emailAddress
        }.id.value

        logger.debug("New email added successfully. userId: {}, emailId: {}", user.id, emailId)
        return emailId
    }

    fun delete(emailId: Int) {
        Emails.deleteWhere {
            Emails.id eq emailId
        }
        logger.debug("Email deleted successfully. emailId: {}", emailId)
    }

    fun getAll(userId: Int): List<Email> {
        return Email
            .find {
                Emails.user_.eq(userId)
            }
            .toCollection(ArrayList())

    }
}