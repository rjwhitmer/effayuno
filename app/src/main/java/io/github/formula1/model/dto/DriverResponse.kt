package io.github.formula1.model.dto

data class DriverResponse(
    val first_name: String,
    val last_name: String,
    val full_name: String,
    val headshot_url: String,
    val team_name: String,
    val team_colour: String,
    val driver_number: Number,
    var championship_position: Int = 0,
    var championship_info: DriverChampionship
)

data class DriverChampionship(
    val driver_number: Number,
    val points_current: Int = 0,
    val points_start: Number,
    val position_current: Number,
    val position_start: Number,
    var interval: Number?
)
