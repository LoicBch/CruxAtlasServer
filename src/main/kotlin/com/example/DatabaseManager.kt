package com.example

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.ktorm.database.Database
import org.ktorm.jackson.KtormModule

object DatabaseManager {

    private const val host = "localhost:3306"
    private const val dbName = "database"
    private const val pass = "db_user_pass"
    private const val name = "db_user"

    val database: Database

    init {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:mysql://$host/$dbName"
            driverClassName = "com.mysql.cj.jdbc.Driver"
            username = name
            password = pass
            maximumPoolSize = 10
        }

        database = Database.connect(HikariDataSource(config))
    }
}