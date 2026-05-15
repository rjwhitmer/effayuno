package io.github.formula1.model.dto

data class RaceSessionResult(
    val dnf: Boolean,
    val dns: Boolean,
    val dsq: Boolean,
    val driver_number: Int,
    val duration: Double,
    val gap_to_leader: Any,
    val number_of_laps: Int,
    val meeting_key: Int,
    val position: Int,
    val session_key: Int
)
