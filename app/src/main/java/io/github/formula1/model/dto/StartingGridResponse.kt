package io.github.formula1.model.dto

data class StartingGridResponse(
    val driver_number: Number,
    val position: Number,
    val lap_duration: Double,
    val meeting_key: Number,
    val session_key: Number
)
