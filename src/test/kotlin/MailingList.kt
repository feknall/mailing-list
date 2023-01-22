import bl.UserBl
import dao.repository.UserRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import rest.AddUser
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserBlUnitTest : UnitTestBase() {

    private val userRepositoryMock: UserRepository = Mockito.mock(UserRepository::class.java)
    private val userBl = UserBl(userRepositoryMock)

    @Test
    fun addUser() {
        val name = "name"
        val userId = 1
        Mockito.`when`(userRepositoryMock.add(name)).thenReturn(userId)

        val user = userBl.addUser(AddUser(name))

        assertEquals(name, user.name)
        assertEquals(userId, user.userId)
        assertNull(user.emailList)

        Mockito.verify(userRepositoryMock).add(name)
        Mockito.verifyNoMoreInteractions(userRepositoryMock)
    }
}

