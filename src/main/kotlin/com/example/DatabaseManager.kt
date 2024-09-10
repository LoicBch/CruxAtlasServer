package com.example

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.ktorm.database.Database
import org.ktorm.jackson.KtormModule

object DatabaseManager {

    private const val host = "localhost:6033"
    private const val dbName = "app_db"
    private const val name = "db_user"
    private const val pass = "db_user_pass"

    //------------------------------------------------------------------------
//    private const val host = "185.170.58.30:6033"
//    private const val dbName = "app_db"
//    private const val name = "db_user"
//    private const val pass = "db_user_pass"

    val database: Database

    init {
//        val config = HikariConfig().apply {
//            jdbcUrl =
//                "jdbc:mysql://$host/$dbName?useUnicode=yes&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true"
//            driverClassName = "com.mysql.cj.jdbc.Driver"
//            username = name
//            password = pass
//            maximumPoolSize = 50
//            keepaliveTime = 240000
//            maxLifetime = 590000
//        }

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