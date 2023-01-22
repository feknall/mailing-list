import bl.EmailBl
import bl.UserBl
import dao.DatabaseFactory
import dao.repository.EmailRepository
import dao.repository.UserRepository
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import rest.MailingListRest

fun main() {
    val dbFactory = DatabaseFactory()
    dbFactory.init()

    val userRepository = UserRepository()
    val userBl = UserBl(userRepository)

    val emailRepository = EmailRepository()
    val emailBl = EmailBl(userRepository, emailRepository)

    val mailingListApi = MailingListRest()
    mailingListApi.getRoutes(userBl, emailBl).asServer(SunHttp(8080)).start()
}