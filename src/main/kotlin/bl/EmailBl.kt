package bl

import dao.repository.EmailRepository
import dao.repository.UserRepository
import org.jetbrains.exposed.sql.transactions.transaction
import rest.AddEmailRequest
import rest.AddEmailResponse
import rest.DeleteEmail
import rest.Email

class EmailBl(private val userRepository: UserRepository, private val emailRepository: EmailRepository) {

    fun addEmail(addEmailRequest: AddEmailRequest): AddEmailResponse {
        return transaction {
            val user = userRepository.get(addEmailRequest.userId) ?: throw Exception("User not found.")
            val emailId = emailRepository.add(user, addEmailRequest.emailAddress).id.value
            AddEmailResponse(addEmailRequest.userId, emailId, addEmailRequest.emailAddress)
        }
    }

    fun deleteEmail(deleteEmail: DeleteEmail): Boolean {
        val numberOfDeleteItems = transaction {
            emailRepository.delete(deleteEmail.emailId)
        }
        return numberOfDeleteItems > 0
    }

    fun getAllEmails(userId: Int): List<Email> {
        return transaction {
            userRepository.get(userId) ?: throw Exception("User not found")
            emailRepository
                .getAll(userId)
                .map {
                    Email(it.id.value, it.emailAddress_)
                }
                .toCollection(ArrayList())
        }
    }
}