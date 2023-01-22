package integration

import base.IntegrationTestBase
import bl.EmailBl
import bl.UserBl
import com.natpryce.hamkrest.assertion.assertThat
import dao.repository.EmailRepository
import dao.repository.UserRepository
import org.http4k.core.*
import org.http4k.format.Jackson.auto
import org.http4k.hamkrest.hasStatus
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import rest.Email
import rest.MailingListRest
import rest.User
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserIntegrationTest : IntegrationTestBase() {

    private val userRepository = UserRepository()
    private val userBl = UserBl(userRepository)

    private val emailRepository = EmailRepository()
    private val emailBl = EmailBl(userRepository, emailRepository)

    private val app: HttpHandler = MailingListRest().getRoutes(userBl, emailBl)

    @Test
    fun getUser_verifyEndpointExists() {
        val request = Request(Method.GET, "/user")
        val response = app(request)
        assertThat(response, hasStatus(Status.OK))
    }

    @Test
    fun getUser_verifyReturnsAllUsers() {
        val nameJackZeroEmail = "JackZeroEmail"
        val nameJillOneEmail = "Jill"
        val nameTopTwoEmail = "Tom"

        val names = listOf(nameJackZeroEmail, nameJillOneEmail, nameTopTwoEmail)

        val users = ArrayList<dao.User>()
        transaction {
            names.forEach {
                val user = userRepository.add(it)
                users.add(user)
            }
        }

        // Jill
        val jillFirstEmailAddress = "$nameJillOneEmail@gmail.com"
        val jillFirstEmailId = transaction {
            emailRepository.add(users[1], jillFirstEmailAddress)
        }

        // Tom
        val tomFirstEmailAddress = "$nameTopTwoEmail@gmail.com"
        val tomFirstEmailId = transaction {
            emailRepository.add(users[2], tomFirstEmailAddress)
        }
        val tomSecondEmailAddress = "$nameTopTwoEmail@outlook.com"
        val tomSecondEmailId = transaction {
            emailRepository.add(users[2], tomSecondEmailAddress)
        }

        val request = Request(Method.GET, "/user")
        val response = app(request)

        val getUser = Body.auto<List<User>>().toLens().extract(response)

        assertEquals(names.size, getUser.size)

        getUser.forEach { user ->
            assertTrue {
                names.contains(user.name)
            }
            if (user.name.equals(nameJackZeroEmail)) {
                assertTrue { user.emailList!!.isEmpty() }
            } else if (user.name.equals(nameJillOneEmail)) {
                assertTrue { user.emailList!!.contains(Email(jillFirstEmailId, jillFirstEmailAddress)) }
            } else if (user.name.equals(nameTopTwoEmail)) {
                assertTrue { user.emailList!!.contains(Email(tomFirstEmailId, tomFirstEmailAddress)) }
                assertTrue { user.emailList!!.contains(Email(tomSecondEmailId, tomSecondEmailAddress)) }
            }
        }
    }
}