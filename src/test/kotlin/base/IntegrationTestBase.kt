package base

import dao.DatabaseFactory

open class IntegrationTestBase {

    init {
        val dbFactory = DatabaseFactory()
        dbFactory.init()
    }
}