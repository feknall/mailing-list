package rest

import bl.EmailBl
import bl.UserBl
import org.http4k.contract.contract
import org.http4k.contract.meta
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.ui.swaggerUi
import org.http4k.core.*
import org.http4k.core.Method.*
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson.auto
import org.http4k.routing.routes

class MailingListRest {

    fun routes(userBl: UserBl, emailBl: EmailBl): HttpHandler {

        val getUserHandler = "/user" meta {
            operationId = "getUserHandler"
            summary = "Returns list of users"
            returning(
                OK,
                Body.auto<List<User>>().toLens() to listOf<User>(
                    User(
                        100,
                        "hamid",
                        listOf(Email(1, "hamid@gmail.com"))
                    )
                )
            )
        } bindContract GET to { _: Request ->
            try {
                Response(OK).with(Body.auto<List<User>>().toLens() of userBl.getAllUsers())
            } catch (e: Exception) {
                Response(Status.INTERNAL_SERVER_ERROR)
            }
        }

        val postUserHandler = "/user" meta {
            operationId = "postUserHandler"
            summary = "Adds a new user"
            receiving(Body.auto<AddUser>().toLens() to AddUser("hamid"))
            returning(
                OK,
                Body.auto<User>().toLens() to User(1, "hamid", listOf())
            )

        } bindContract POST to { req: Request ->
            try {
                val request = Body.auto<AddUser>().toLens().extract(req)
                val response = userBl.addUser(request)
                Response(OK).with(Body.auto<User>().toLens() of response)
            } catch (e: Exception) {
                Response(Status.INTERNAL_SERVER_ERROR)
            }
        }

        val getEmailHandler = "/email" meta {
            operationId = "getEmailHandler"
            summary = "Returns list of emails for a specific user"
            receiving(Body.auto<GetEmails>().toLens() to GetEmails(1))
            returning(
                OK,
                Body.auto<List<Email>>().toLens() to listOf(Email(1, "hamid@gmail.com"))
            )
        } bindContract GET to { req: Request ->
            try {
                val request = Body.auto<GetEmails>().toLens().extract(req)
                val response = emailBl.getAllEmails(request.userId)
                Response(OK).with(Body.auto<List<Email>>().toLens() of response)
            } catch (e: Exception) {
                Response(Status.INTERNAL_SERVER_ERROR)
            }
        }

        val postEmailHandler = "/email" meta {
            operationId = "postEmailHandler"
            summary = "Adds a new email for a specific user"
            receiving(Body.auto<AddEmailRequest>().toLens() to AddEmailRequest(1, "hamid@gmail.com"))
            returning(
                OK,
                Body.auto<AddEmailResponse>().toLens() to AddEmailResponse(1, 100, "hamid@gmail.com")
            )
        } bindContract POST to { req: Request ->
            try {
                val request = Body.auto<AddEmailRequest>().toLens().extract(req)
                val response = emailBl.addEmail(request)
                Response(OK).with(Body.auto<AddEmailResponse>().toLens() of response)
            } catch (e: Exception) {
                Response(Status.INTERNAL_SERVER_ERROR)
            }
        }

        val deleteEmailHandler = "/email" meta {
            operationId = "deleteEmailHandler"
            summary = "Adds a new email for a specific user"
            receiving(Body.auto<DeleteEmail>().toLens() to DeleteEmail(1))
            returning(OK)
        } bindContract DELETE to { req: Request ->
            try {
                val request = Body.auto<DeleteEmail>().toLens().extract(req)
                if (emailBl.deleteEmail(request))
                    Response(OK)
                else
                    Response(NOT_FOUND)
            } catch (e: Exception) {
                Response(Status.INTERNAL_SERVER_ERROR)
            }
        }

        val v1Api = contract {
            routes += getUserHandler
            routes += postUserHandler
            routes += getEmailHandler
            routes += postEmailHandler
            routes += deleteEmailHandler
            renderer = OpenApi3(
                ApiInfo("Hello Server - Developer UI", "99.3.4")
            )
            descriptionPath = "spec"
        }

        val ui = swaggerUi(
            Uri.of("spec"),
            title = "Hello Server",
            displayOperationId = true
        )

        return routes(v1Api, ui)
    }
}

