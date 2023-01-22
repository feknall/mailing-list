package dao.repository

import dao.Email
import dao.Emails
import dao.User
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.slf4j.LoggerFactory
import java.util.*

class EmailRepository {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun add(user: User, emailAddress: String): Email {
        val email = Email.new {
            user_ = user
            emailAddress_ = emailAddress
        }

        logger.debug("New email added successfully. userId: {}, emailId: {}", user.id, email.id.value)
        return email
    }

    fun delete(emailId: Int): Int {
        val numberOfDeleteItems = Emails.deleteWhere {
            Emails.id eq emailId
        }
        if (numberOfDeleteItems > 0)
            logger.debug("Email deleted successfully. emailId: {}", emailId)
        else
            logger.debug("Email not found")
        return numberOfDeleteItems
    }

    fun getAll(userId: Int): List<Email> {
        return Email
            .find {
                Emails.user_.eq(userId)
            }.toCollection(LinkedList())

    }
}