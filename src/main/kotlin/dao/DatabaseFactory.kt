package dao

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.util.*

class DatabaseFactory {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val fileAddress = "application.properties"

    fun init() {
        val file = this::class.java.classLoader.getResourceAsStream(fileAddress)
        val prop = Properties()
        prop.load(file)
        val jdbcUrl = prop.getProperty("database.jdbcUrl")
        val driverClassName = prop.getProperty("database.driverClassName")
        val username = prop.getProperty("database.username")
        val password = prop.getProperty("database.password")
        init(jdbcUrl, driverClassName, username, password)
    }

    private fun init(jdbcURL: String, driverClassName: String, username: String, password: String) {
        logger.debug(
            "jdbcUrl: {}, driverClassName: {}, username: {}, password: {}",
            jdbcURL,
            driverClassName,
            username,
            password
        )

        val database = Database.connect(jdbcURL, driverClassName, username, password)
        transaction(database) {
            SchemaUtils.create(Users)
            SchemaUtils.create(Emails)
        }
    }
}