package com.example.routes

import com.example.DatabaseManager.database
import io.ktor.server.routing.*
import org.ktorm.database.asIterable
import org.owasp.encoder.Encode
import java.net.URLEncoder

fun String.secured(): String = URLEncoder.encode(Encode.forHtml(this),"UTF-8")

fun Route.utils() {
    route("/utils") {

    }
}

fun executeRawQuery(sql: String, columnIndex: Int): List<String> {
    val result = database.useConnection { conn ->
        conn.prepareStatement(sql).use { statement ->
            statement.executeQuery().asIterable().map { it.getString(columnIndex) }
        }
    }
    return result
}