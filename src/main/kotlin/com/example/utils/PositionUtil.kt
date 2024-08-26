package com.example.utils

import com.example.DatabaseManager
import com.example.data.model.*
import io.ktor.server.engine.*
import org.ktorm.entity.map
import org.ktorm.entity.sequenceOf

object PositionUtil {


    private var deltaDistance = 0.0
    private const val DISTANCE_BETWEEN_SEARCH = 50
    private const val DISTANCE_FROM_ITINERARY = 30

    private fun distanceInKm(lat: Double, long: Double, latBis: Double, longBis: Double): Double {
        val R = 6371; // Radius of the earth in km
        val dLat = deg2rad(latBis - lat); // deg2rad below
        val dLon = deg2rad(longBis - long);
        val a =
            Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(deg2rad(lat)) * Math.cos(deg2rad(latBis)) *
                    Math.sin(dLon / 2) * Math.sin(dLon / 2);
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        val d = R * c; // Distance in km
        return d;
    }

    private fun deg2rad(deg: Double): Double {
        return deg * (Math.PI / 180);
    }

    fun isCloserThanThreshold(spot: CragDto, pos: Position, threshold: Int): Boolean {
        val delta = distanceInKm(spot.latitude, spot.longitude, pos.latitude, pos.longitude)
        return delta < threshold
    }

}