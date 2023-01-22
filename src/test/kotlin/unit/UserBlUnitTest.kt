package unit

import base.UnitTestBase
import bl.UserBl
import dao.User
import dao.Users
import dao.repository.UserRepository
import org.jetbrains.exposed.dao.id.EntityID
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import rest.AddUser
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserBlUnitTest : UnitTestBase() {

    private val userRepositoryMock: UserRepository = Mockito.mock(UserRepository::class.java)
    private val userBl = UserBl(userRepositoryMock)

    @Test
    fun addUser_ok() {
        val name = "name"
        val userId = 1

        val user = User(EntityID(userId, Users))

        Mockito.`when`(userRepositoryMock.add(name)).thenReturn(user)

        val addedUser = userBl.addUser(AddUser(name))

        assertEquals(name, addedUser.name)
        assertEquals(userId, addedUser.userId)
        assertNull(addedUser.emailList)

        Mockito.verify(userRepositoryMock).add(name)
        Mockito.verifyNoMoreInteractions(userRepositoryMock)
    }

}

