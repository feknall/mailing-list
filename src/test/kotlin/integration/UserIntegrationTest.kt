package integration

import base.IntegrationTestBase
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.format.Jackson.auto
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import rest.Email
import rest.User
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserIntegrationTest : IntegrationTestBase() {

    @Test
    fun addUser_verifyAddsSuccessfully() {
        val name = "Tim"

        val request = Request(Method.POST, "/user").body(
            "{\"name\":\"$name\"}"
        )
        val response = app(request)

        val deserializedResponse = Body.auto<User>().toLens().extract(response)
        val user = transaction {
            userRepository.get(deserializedResponse.userId!!)!!
        }

        assertEquals(name, user.name_)
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
        }.id.value

        // Tom
        val tomFirstEmailAddress = "$nameTopTwoEmail@gmail.com"
        val tomFirstEmailId = transaction {
            emailRepository.add(users[2], tomFirstEmailAddress)
        }.id.value

        val tomSecondEmailAddress = "$nameTopTwoEmail@outlook.com"
        val tomSecondEmailId = transaction {
            emailRepository.add(users[2], tomSecondEmailAddress)
        }.id.value

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