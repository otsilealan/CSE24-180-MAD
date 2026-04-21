package com.accommodation.domain

data class FilterParams(
    val minPrice: Double = 0.0,
    val maxPrice: Double = 0.0,
    val location: String = "",
    val date: Long = 0L
)

object CampusData {
    // UB Main Campus reference point
    const val CAMPUS_LAT = -24.6553
    const val CAMPUS_LON = 25.9086

    val locationCoords: Map<String, Pair<Double, Double>> = mapOf(
        "Gaborone West" to Pair(-24.6600, 25.8900),
        "Gaborone North" to Pair(-24.6200, 25.9200),
        "Broadhurst" to Pair(-24.6800, 25.9100),
        "Tlokweng" to Pair(-24.6500, 25.9700),
        "Mogoditshane" to Pair(-24.6200, 25.8500),
        "Phakalane" to Pair(-24.5800, 25.9300),
        "Block 8" to Pair(-24.6700, 25.9000),
        "Block 9" to Pair(-24.6750, 25.9050),
        "Extension 2" to Pair(-24.6550, 25.9100),
        "Bontleng" to Pair(-24.6450, 25.9150)
    )
}
