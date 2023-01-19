data class User(val userId: Int?, val name: String, val emailList: List<String>?) {
    constructor(name: String): this(name = name, userId = null, emailList = null)
}