package integration

import base.IntegrationTestBase
import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.format.Jackson.auto
import org.http4k.hamkrest.hasStatus
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import rest.AddEmailResponse
import rest.Email
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EmailIntegrationTest : IntegrationTestBase() {

    @Test
    fun addEmail_userExists_verifyAddsSuccessfully() {
        // First user zero email, and is going to add one email
        val firstName = "Tim"
        val firstEmailAddress = "tim@gmail.com"
        val firstUser = transaction {
            userRepository.add(firstName)
        }
        val firstUserId = firstUser.id.value

        // User with zero email address, and should not get changed
        val secondName = "John"
        val secondUser = transaction {
            userRepository.add(secondName)
        }
        val secondUserId = secondUser.id.value

        val request = Request(Method.POST, "/email").body(
            "{\"userId\":\"${firstUserId}\", \"emailAddress\":\"${firstEmailAddress}\"}"
        )
        val response = app(request)

        assertThat(response, hasStatus(Status.OK))

        val addEmailResponse = Body.auto<AddEmailResponse>().toLens().extract(response)

        assertEquals(firstUserId, addEmailResponse.userId)
        assertNotNull(addEmailResponse.emailId)
        assertEquals(firstEmailAddress, addEmailResponse.emailAddress)

        val firstUserEmails = transaction {
            emailRepository.getAll(firstUserId)
        }

        assertEquals(1, firstUserEmails.size)
        assertEquals(firstEmailAddress, firstUserEmails[0].emailAddress_)

        val secondUserEmails = transaction {
            emailRepository.getAll(secondUserId)
        }

        assertTrue { secondUserEmails.isEmpty() }
    }

    @Test
    fun addEmail_userExistsAndAlreadyHasAnEmail_verifyAddsSuccessfully() {
        // First user has one email, and is going to add another email
        val firstName = "Tim"
        val firstEmailAddress = "tim@gmail.com"
        val firstUser = transaction {
            userRepository.add(firstName)
        }
        val firstUserId = firstUser.id.value
        transaction {
            emailRepository.add(firstUser, firstEmailAddress)
        }

        // Second user has only one email, and should not get changed.
        val secondName = "John"
        val secondEmailAddress = "J.junior@gmail.com"
        val secondUser = transaction {
            userRepository.add(secondName)
        }
        val secondUserId = secondUser.id.value
        transaction {
            emailRepository.add(secondUser, secondEmailAddress)
        }

        val newEmailAddress = "tim.new@gmail.com"
        val request = Request(Method.POST, "/email").body(
            "{\"userId\":\"${firstUserId}\", \"emailAddress\":\"${newEmailAddress}\"}"
        )
        val response = app(request)

        assertThat(response, hasStatus(Status.OK))

        val addEmailResponse = Body.auto<AddEmailResponse>().toLens().extract(response)

        assertEquals(firstUserId, addEmailResponse.userId)
        assertNotNull(addEmailResponse.emailId)
        assertEquals(newEmailAddress, addEmailResponse.emailAddress)

        val firstUserEmails = transaction {
            emailRepository.getAll(firstUserId)
        }

        assertEquals(2, firstUserEmails.size)
        assertEquals(firstEmailAddress, firstUserEmails[0].emailAddress_)

        val secondUserEmails = transaction {
            emailRepository.getAll(secondUserId)
        }

        assertEquals(1, secondUserEmails.size)
        assertEquals(secondEmailAddress, secondUserEmails[0].emailAddress_)
    }

    @Test
    fun addEmail_userDoesNotExist_ShouldThrowException() {
        val emailAddress = "tim.new@gmail.com"
        val userId = 1234
        val request = Request(Method.POST, "/email").body(
            "{\"userId\":\"${userId}\", \"emailAddress\":\"${emailAddress}\"}"
        )
        val response = app(request)

        assertThat(response, hasStatus(Status.INTERNAL_SERVER_ERROR))
    }

    @Test
    fun deleteEmail_emailDoesNotExist_ShouldTrowException() {
        val emailId = 1234
        val request = Request(Method.DELETE, "/email").body(
            "{\"emailId\":\"${emailId}\"}"
        )
        val response = app(request)

        assertThat(response, hasStatus(Status.NOT_FOUND))
    }

    @Test
    fun deleteEmail_emailExists_ShouldSuccessfullyDelete() {
        val name = "user"
        val user = transaction {
            userRepository.add(name)
        }
        val firstDeleteEmailAddress = "firstDeleteEmailAddress@gmail.com"
        val firstDeleteEmailId = transaction {
            emailRepository.add(user, firstDeleteEmailAddress)
        }.id.value

        val secondDeleteEmailAddress = "secondDeleteEmailAddress@gmail.com"
        val secondDeleteEmailId = transaction {
            emailRepository.add(user, secondDeleteEmailAddress)
        }.id.value

        val firstDeleteRequest = Request(Method.DELETE, "/email").body(
            "{\"emailId\":\"${firstDeleteEmailId}\"}"
        )
        val firstResponse = app(firstDeleteRequest)

        assertThat(firstResponse, hasStatus(Status.OK))

        val emailsAfterOneDelete = transaction {
            emailRepository.getAll(user.id.value)
        }

        assertEquals(1, emailsAfterOneDelete.size)

        val secondDeleteRequest = Request(Method.DELETE, "/email").body(
            "{\"emailId\":\"${secondDeleteEmailId}\"}"
        )
        val secondResponse = app(secondDeleteRequest)

        assertThat(secondResponse, hasStatus(Status.OK))

        val emailsAfterTwoDelete = transaction {
            emailRepository.getAll(user.id.value)
        }

        assertEquals(0, emailsAfterTwoDelete.size)
    }

    @Test
    fun getEmails_userExists_ShouldSuccessfullyReturnTwoEmails() {
        val name = "user"
        val user = transaction {
            userRepository.add(name)
        }
        val userId = user.id.value

        val firstEmailAddress = "firstDeleteEmailAddress@gmail.com"
        val firstEmailId = transaction {
            emailRepository.add(user, firstEmailAddress)
        }.id.value

        val secondEmailAddress = "secondDeleteEmailAddress@gmail.com"
        val secondEmailId = transaction {
            emailRepository.add(user, secondEmailAddress)
        }.id.value

        val secondDeleteRequest = Request(Method.GET, "/email").body(
            "{\"userId\":\"${userId}\"}"
        )
        val response = app(secondDeleteRequest)
        val getEmailResponse = Body.auto<List<Email>>().toLens().extract(response)

        assertThat(response, hasStatus(Status.OK))
        assertEquals(2, getEmailResponse.size)

        listOf(Email(firstEmailId, firstEmailAddress), Email(secondEmailId, secondEmailAddress)).forEach {
            assertTrue { getEmailResponse.contains(it) }
        }
    }

    @Test
    fun getEmails_userDoesNotExists_ShouldThrowException() {
        val request = Request(Method.GET, "/email").body(
            "{\"userId\":\"${123}\"}"
        )
        val response = app(request)
        assertThat(response, hasStatus(Status.INTERNAL_SERVER_ERROR))
    }

    @Test
    fun getEmails_userExistsButHasNoEmail_ShouldReturnEmptyList() {
        val name = "user"
        val user = transaction {
            userRepository.add(name)
        }
        val userId = user.id.value

        val secondDeleteRequest = Request(Method.GET, "/email").body(
            "{\"userId\":\"${userId}\"}"
        )
        val response = app(secondDeleteRequest)

        val getEmailResponse = Body.auto<List<Email>>().toLens().extract(response)

        assertThat(response, hasStatus(Status.OK))
        assertEquals(0, getEmailResponse.size)
    }
}