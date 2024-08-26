package com.example.data.model

import org.ktorm.entity.Entity
import org.ktorm.schema.*

object Tables {
    object Users : Table<User>("users") {
        val id = int("id").primaryKey().bindTo { it.id }
        val username = varchar("username").bindTo { it.username }
        val password = varchar("password").bindTo { it.password }
        val email = varchar("email").bindTo { it.email }
        val creationDate = varchar("product_name").bindTo { it.creationDate }
        val lastConnection = varchar("latitude").bindTo { it.lastConnection }
    }

    interface User : Entity<User> {
        companion object : Entity.Factory<User>()
        var id: Int?
        var username: String
        var password: String
        var email: String
        var creationDate: String
        var lastConnection: String
    }

    object Crags : Table<Crag>("crags") {
        val id = int("id").primaryKey().bindTo { it.id }
        val areaId = varchar("area_id").bindTo { it.areaId }
        val name = varchar("name").bindTo { it.name }
        val latitude = double("latitude").bindTo { it.latitude }
        val longitude = double("longitude").bindTo { it.longitude }
        val thumbnailUrl = varchar("thumbnail_url").bindTo { it.thumbnailUrl }
    }

    interface Crag : Entity<Crag> {
        companion object : Entity.Factory<Crag>()
        val id: Int
        var areaId: String
        var name: String
        var latitude: Double
        var longitude: Double
        var thumbnailUrl: String
    }

    object Sectors : Table<Sector>("sectors") {
        val id = int("id").primaryKey().bindTo { it.id }
        val cragId = varchar("crag_id").bindTo { it.cragId }
        val name = varchar("name").bindTo { it.name }
    }

    interface Sector : Entity<Sector> {
        companion object : Entity.Factory<Sector>()
        val id: Int
        var cragId: String
        var name: String
    }

    object Areas : Table<Area>("areas") {
        val id = int("id").primaryKey().bindTo { it.id }
        val name = varchar("name").bindTo { it.name }
        val country = varchar("country").bindTo { it.country }
    }

    interface Area : Entity<Area> {
        companion object : Entity.Factory<Area>()
        val id: Int
        var name: String
        var country: String
    }

    object Routes : Table<Route>("routes") {
        val id = int("id").primaryKey().bindTo { it.id }
        val cragId = varchar("crag_id").bindTo { it.cragId }
        val sectorId = varchar("sector_id").bindTo { it.sectorId }
        val name = varchar("name").bindTo { it.name }
        val grade = varchar("grade").bindTo { it.grade }
    }
}

interface Route : Entity<Route> {
    companion object : Entity.Factory<Route>()
    val id: Int
    var cragId: String
    var sectorId: String
    var name: String
    var grade: String
}






