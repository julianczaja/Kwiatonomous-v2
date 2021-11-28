package com.corrot.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

class KwiatonomousDatabase {

    val db: Database = Database.connect(
//        url = "jdbc:h2:file:${System.getProperty("user.home")}/kwiatonomous/",
        url = "jdbc:h2:mem:test_mem;DB_CLOSE_DELAY=-1;",
        driver = "org.h2.Driver"
    )

    fun isConnected() = try {
        transaction { !connection.isClosed }
    } catch (e: Exception) {
        false
    }
}