package unit

import base.UnitTestBase
import bl.EmailBl
import dao.repository.EmailRepository
import dao.repository.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import rest.AddEmailRequest

class EmailBlUnitTest : UnitTestBase() {

    private val userRepositoryMock: UserRepository = Mockito.mock(UserRepository::class.java)
    private val emailRepositoryMock: EmailRepository = Mockito.mock(EmailRepository::class.java)
    private val emailBl = EmailBl(userRepositoryMock, emailRepositoryMock)

    @Test
    fun addEmail_userDoesNotExist_throwException() {
        val userId = 1
        val emailAddress = "h@gmail.com"

        Mockito.`when`(userRepositoryMock.get(userId)).thenReturn(null)

        assertThrows<Exception> {
            emailBl.addEmail(AddEmailRequest(userId, emailAddress))
        }
    }

}