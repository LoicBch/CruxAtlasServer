package com.example.data.model

import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.time.LocalDate

object Tables {

    object NewItems : Table<NewItem>("news") {
        val id = int("id").primaryKey().bindTo { it.id }
        val title = varchar("name").bindTo { it.title }
        val description = varchar("description").bindTo { it.description }
        val imageUrl = varchar("image_url").bindTo { it.imageUrl }
    }

    interface NewItem : Entity<NewItem> {
        companion object : Entity.Factory<NewItem>()

        var id: Int?
        var title: String
        var description: String
        var imageUrl: String
    }

    object RouteLogs : Table<RouteLog>("route_logs") {
        val id = int("id").primaryKey().bindTo { it.id }
        val userId = int("user_id").primaryKey().bindTo { it.userId }
        val routeId = int("route_id").bindTo { it.routeId }
        val routeName = varchar("route_name").bindTo { it.routeName }
        val text = varchar("text").bindTo { it.text }
        val rating = varchar("rating").bindTo { it.rating }
        val date = date("log_date").bindTo { it.date }
    }

    interface RouteLog : Entity<RouteLog> {
        companion object : Entity.Factory<RouteLog>()

        var id: Int
        var userId: Int
        var routeId: Int
        var routeName: String
        var text: String
        var rating: String
        var date: LocalDate
    }

    object BoulderLogs : Table<BoulderLog>("boulder_logs") {
        val id = int("id").primaryKey().bindTo { it.id }
        val userId = int("user_id").primaryKey().bindTo { it.userId }
        val boulderId = int("boulder_id").bindTo { it.boulderId }
        val boulderName = varchar("boulder_name").bindTo { it.boulderName }
        val text = varchar("text").bindTo { it.text }
        val rating = varchar("rating").bindTo { it.rating }
        val logDate = date("log_date").bindTo { it.logDate }
    }

    interface BoulderLog : Entity<BoulderLog> {
        companion object : Entity.Factory<BoulderLog>()

        val id: Int
        var userId: Int
        var boulderId: Int
        var boulderName: String
        var text: String
        var rating: String
        var logDate: LocalDate

    }

    object UserFavCrags : Table<UserFavCrag>("user_favorite_crag") {
        val id = int("id").primaryKey().bindTo { it.id }
        val userId = int("user_id").primaryKey().bindTo { it.userId }
        val spotId = int("crag_id").bindTo { it.cragId }
    }

    interface UserFavCrag : Entity<UserFavCrag> {
        companion object : Entity.Factory<UserFavCrag>()

        var id: Int
        var userId: Int
        var cragId: Int
    }

//    object UserLogBoulders : Table<UserLogBoulder>("user_log_boulder") {
//        val id = int("id").primaryKey().bindTo { it.id }
//        val userId = int("user_id").primaryKey().bindTo { it.userId }
//        val boulderId = int("boulder_id").bindTo { it.boulderId }
//    }
//
//    interface UserLogBoulder : Entity<UserLogBoulder> {
//        companion object : Entity.Factory<UserLogBoulder>()
//
//        var id: Int
//        var userId: Int
//        var boulderId: Int
//    }
//
//    object UserLogRoutes : Table<UserLogRoute>("user_log_route") {
//        val id = int("id").primaryKey().bindTo { it.id }
//        val userId = int("user_id").primaryKey().bindTo { it.userId }
//        val routeId = int("route_id").bindTo { it.routeId }
//    }

//    interface UserLogRoute : Entity<UserLogRoute> {
//        companion object : Entity.Factory<UserLogRoute>()
//
//        var id: Int
//        var userId: Int
//        var routeId: Int
//    }

    object Users : Table<User>("users") {
        val id = int("id").primaryKey().bindTo { it.id }
        val username = varchar("username").bindTo { it.username }
        val password = varchar("password").bindTo { it.password }
        val email = varchar("email").bindTo { it.email }
        val country = varchar("country").bindTo { it.country }
        val city = varchar("city").bindTo { it.city }
        val age = int("age").bindTo { it.age }
        val gender = varchar("gender").bindTo { it.gender }
        val height = int("height").bindTo { it.height }
        val weight = int("weight").bindTo { it.weight }
        val climbingSince = date("climbing_since").bindTo { it.climbingSince }
        val imageUrl = varchar("image_url").bindTo { it.imageUrl }
        val creation_date = date("creation_date").bindTo { it.creationDate }
        val lastConnection = date("last_connexion").bindTo { it.lastConnection }
    }

    interface User : Entity<User> {
        companion object : Entity.Factory<User>()

        var id: Int?
        var username: String
        var password: String
        var email: String
        var country: String
        var city: String
        var age: Int
        var gender: String
        var height: Int
        var weight: Int
        var climbingSince: LocalDate?
        var imageUrl: String?
        var creationDate: LocalDate
        var lastConnection: LocalDate
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

    object Parkings : Table<Parking>("parking_spots") {
        val id = int("id").primaryKey().bindTo { it.id }
        val sectorId = int("sector_id").bindTo { it.sectorId }
        val name = varchar("name").bindTo { it.name }
        val latitude = double("latitude").bindTo { it.latitude }
        val longitude = double("longitude").bindTo { it.longitude }
    }

    interface Parking : Entity<Parking> {
        companion object : Entity.Factory<Parking>()

        val id: Int
        var sectorId: Int
        var name: String
        var latitude: Double
        var longitude: Double
    }

    object Routes : Table<Route>("routes") {
        val id = int("id").primaryKey().bindTo { it.id }
        val cragId = int("crag_id").bindTo { it.cragId }
        val cragName = varchar("crag_name").bindTo { it.cragName }
        val sectorId = int("sector_id").bindTo { it.sectorId }
        val name = varchar("name").bindTo { it.name }
        val grade = varchar("grade").bindTo { it.grade }
        val sectorName = varchar("sector_name").bindTo { it.sectorName }
        val ascents = int("ascents").bindTo { it.ascents }
        val rating = float("rating").bindTo { it.rating }
    }

    interface Route : Entity<Route> {
        companion object : Entity.Factory<Route>()

        val id: Int
        var cragId: Int
        var cragName: String
        var sectorId: Int
        var name: String
        var grade: String
        var sectorName : String
        var ascents : Int
        var rating : Float
    }

    object Boulders : Table<Boulder>("boulders") {
        val id = int("id").primaryKey().bindTo { it.id }
        val cragId = int("crag_id").bindTo { it.cragId }
        val cragName = varchar("crag_name").bindTo { it.cragName }
        val sectorId = int("sector_id").bindTo { it.sectorId }
        val name = varchar("name").bindTo { it.name }
        val grade = varchar("grade").bindTo { it.grade }
    }

    interface Boulder : Entity<Boulder> {
        companion object : Entity.Factory<Boulder>()

        val id: Int
        var cragId: Int
        var cragName: String
        var sectorId: Int
        var name: String
        var grade: String
    }
}








