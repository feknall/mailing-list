package bl

import dao.repository.EmailRepository
import dao.repository.UserRepository
import org.jetbrains.exposed.sql.transactions.transaction
import rest.AddEmailRequest
import rest.AddEmailResponse
import rest.DeleteEmail
import rest.Email

object EmailBl {

    fun addEmail(addEmailRequest: AddEmailRequest): AddEmailResponse {
        return transaction {
            val user = UserRepository.get(addEmailRequest.userId) ?: throw Exception("User not found.")
            val emailId = EmailRepository.add(user, addEmailRequest.emailAddress)
            AddEmailResponse(addEmailRequest.userId, emailId, addEmailRequest.emailAddress)
        }
    }

    fun deleteEmail(deleteEmail: DeleteEmail) {
        transaction {
            EmailRepository.delete(deleteEmail.emailId)
        }
    }

    fun getAllEmails(userId: Int): List<Email> {
        return transaction {
            EmailRepository
                .getAll(userId)
                .map {
                    Email(it.id.value, it.emailAddress_)
                }
                .toCollection(ArrayList())
        }
    }
}