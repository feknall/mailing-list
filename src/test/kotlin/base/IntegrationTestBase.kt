package base

import bl.EmailBl
import bl.UserBl
import dao.DatabaseFactory
import dao.Emails
import dao.Users
import dao.repository.EmailRepository
import dao.repository.UserRepository
import org.http4k.core.HttpHandler
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import rest.MailingListRest

open class IntegrationTestBase {

    val userRepository = UserRepository()
    val userBl = UserBl(userRepository)

    val emailRepository = EmailRepository()
    val emailBl = EmailBl(userRepository, emailRepository)

    val app: HttpHandler = MailingListRest().routes(userBl, emailBl)

    init {
        val dbFactory = DatabaseFactory()
        dbFactory.init()
    }

    @BeforeEach
    fun before() {
        transaction {
            SchemaUtils.drop(Users, Emails)
            SchemaUtils.create(Users, Emails)
        }
    }
}