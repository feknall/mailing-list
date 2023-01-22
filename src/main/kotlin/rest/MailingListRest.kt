package rest

import bl.EmailBl
import bl.UserBl
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.*
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.routing.bind
import org.http4k.routing.routes

class MailingListRest {
    fun getRoutes(userBl: UserBl, emailBl: EmailBl): HttpHandler {

        return routes(
            "/user" bind GET to {
                Response(OK).with(Body.auto<List<User>>().toLens() of userBl.getAllUsers())
            },
            "/user" bind POST to {
                val request = Body.auto<AddUser>().toLens().extract(it)
                val response = userBl.addUser(request)
                Response(OK).with(Body.auto<User>().toLens() of response)
            },
            "/email" bind GET to {
                val request = Body.auto<GetEmails>().toLens().extract(it)
                val response = emailBl.getAllEmails(request.userId)
                Response(OK).with(Body.auto<List<Email>>().toLens() of response)
            },
            "/email" bind POST to {
                val request = Body.auto<AddEmailRequest>().toLens().extract(it)
                val response = emailBl.addEmail(request)
                Response(OK).with(Body.auto<AddEmailResponse>().toLens() of response)

            },
            "/email" bind DELETE to {
                val request = Body.auto<DeleteEmail>().toLens().extract(it)
                emailBl.deleteEmail(request)
                Response(OK)
            }
        )
    }
}

