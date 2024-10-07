package com.example.routes

import com.example.DatabaseManager.database
import com.example.data.model.NewsItem
import com.example.data.model.Tables
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.from
import org.ktorm.dsl.limit
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.schema.date


fun Route.feed() {

    route("/feed"){
        get {
            val page = call.request.queryParameters["page"]!!.secured().toInt()
            val pageSize = 3
            val skip = (page - 1) * pageSize

            val feeds = database.from(Tables.NewItems)
                .select(
                    Tables.NewItems.id,
                    Tables.NewItems.title,
                    Tables.NewItems.description,
                    Tables.NewItems.imageUrl
                )
                .limit(offset = skip, limit = pageSize)
                .map {
                    NewsItem(
                        it[Tables.NewItems.id]!!,
                        it[Tables.NewItems.title]!!,
                        it[Tables.NewItems.description]!!,
                        it[Tables.NewItems.imageUrl]!!
                    )
                }

            call.respond(HttpStatusCode.OK, feeds)
        }
    }
}