import org.jetbrains.exposed.sql.*

object UserTable: Table() {
    val idCol = integer("id").autoIncrement()
    val nameCol = varchar("name", 50)

    override val primaryKey = PrimaryKey(idCol, name = "PK_User_ID")
}

object EmailTable: Table() {
    val idCol = integer("id").autoIncrement()
    val addressCol = varchar("address", 100)

    val userIdCol = integer("user_id") references UserTable.idCol
    override val primaryKey = PrimaryKey(idCol, name = "PK_Email_ID")
}