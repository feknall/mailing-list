package rest

import bl.EmailBl
import bl.UserBl
import org.http4k.core.*
import org.http4k.core.Method.*
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson.auto
import org.http4k.routing.bind
import org.http4k.routing.routes

class MailingListRest {
    fun getRoutes(userBl: UserBl, emailBl: EmailBl): HttpHandler {

        return routes(
            "/user" bind GET to {
                try {
                    Response(OK).with(Body.auto<List<User>>().toLens() of userBl.getAllUsers())
                } catch (e: Exception) {
                    Response(Status.INTERNAL_SERVER_ERROR)
                }
            },
            "/user" bind POST to {
                try {
                    val request = Body.auto<AddUser>().toLens().extract(it)
                    val response = userBl.addUser(request)
                    Response(OK).with(Body.auto<User>().toLens() of response)
                } catch (e: Exception) {
                    Response(Status.INTERNAL_SERVER_ERROR)
                }
            },
            "/email" bind GET to {
                try {
                    val request = Body.auto<GetEmails>().toLens().extract(it)
                    val response = emailBl.getAllEmails(request.userId)
                    Response(OK).with(Body.auto<List<Email>>().toLens() of response)
                } catch (e: Exception) {
                    Response(Status.INTERNAL_SERVER_ERROR)
                }
            },
            "/email" bind POST to {
                try {
                    val request = Body.auto<AddEmailRequest>().toLens().extract(it)
                    val response = emailBl.addEmail(request)
                    Response(OK).with(Body.auto<AddEmailResponse>().toLens() of response)
                } catch (e: Exception) {
                    Response(Status.INTERNAL_SERVER_ERROR)
                }

            },
            "/email" bind DELETE to {
                try {
                    val request = Body.auto<DeleteEmail>().toLens().extract(it)
                    if (emailBl.deleteEmail(request))
                        Response(OK)
                    else
                        Response(NOT_FOUND)
                } catch (e: Exception) {
                    Response(Status.INTERNAL_SERVER_ERROR)
                }
            }
        )
    }
}

