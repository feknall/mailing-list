package rest

data class AddEmailRequest(val userId: Int, val emailAddress: String)

data class AddEmailResponse(val userId: Int, val emailId: Int, val emailAddress: String)

data class GetEmails(val userId: Int)

data class DeleteEmail(val emailId: Int)

data class AddUser(val name: String)

data class User(val userId: Int?, val name: String?, val emailList: List<Email>?) {
    constructor(name: String): this(name = name, userId = null, emailList = null)
    constructor(userId: Int, emailList: List<Email>?): this(name = null, userId = userId, emailList = emailList)
}

data class Email(val emailId: Int, val address: String)