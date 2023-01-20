package dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable() {
    val name_ = varchar("name", 50)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var name_ by Users.name_
    val emails_ by Email referrersOn Emails.user_
}

object Emails : IntIdTable() {
    val user_ = reference("user", Users)

    val emailAddress_ = varchar("email_address", 100)
}

class Email(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Email>(Emails)

    var emailAddress_ by Emails.emailAddress_
    var user_ by User referencedOn Emails.user_
}