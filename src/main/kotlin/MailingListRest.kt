import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun MailingListApi(): HttpHandler {

    return routes(
        "/addUser" bind POST to {
            val user = Body.auto<AddUserJson>().toLens().extract(it)
            MailingListBl.addUser(user)
            Response(OK).with(Body.auto<AddUserJson>().toLens() of AddUserJson("a"))
        },
        "/getAllUsers" bind GET to {
            Response(OK).with(Body.auto<List<User>>().toLens() of MailingListBl.getAllUsers())
        },
        "/addEmail" bind POST to {
            val addEmail = Body.auto<AddEmailJson>().toLens().extract(it)

            Response(OK).with(Body.auto<AddEmailJson>().toLens() of AddEmailJson(1, "b"))

        }
//        "/" bind GET to {
//            Response(OK).with(bodyLens of JsonMessage(Instant.now(clock), random.nextInt(), true)) }
    )
}

fun main() {
    DatabaseFactory.init()

    MailingListApi().asServer(SunHttp(8080)).start()
}