import dao.DatabaseFactory
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import rest.mailingListApi

fun main() {
    DatabaseFactory.init()
    mailingListApi().asServer(SunHttp(8080)).start()
}