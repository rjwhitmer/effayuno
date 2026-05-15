package io.github.formula1.model.dto

data class QualifierSessionResult(
    val dnf: Boolean,
    val dns: Boolean,
    val dsq: Boolean,
    val driver_number: Int,
    val duration: List<Double>,
    val gap_to_leader: List<Double>,
    val number_of_laps: Int,
    val meeting_key: Int,
    val position: Int,
    val session_key: Int
)
