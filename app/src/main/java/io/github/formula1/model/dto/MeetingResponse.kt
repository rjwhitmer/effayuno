package io.github.formula1.model.dto

data class MeetingResponse(
    val circuit_key: Int = 0,
    val circuit_image: String = "",
    val circuit_short_name: String = "",
    val circuit_type: String = "",
    val date_start: String = "",
    val date_end: String = "",
    val gmt_offset: String = "",
    val location: String = "",
    val meeting_key: Int = 0,
    val meeting_name: String = "",
    val year: Int = 0,
    val meeting_official_name: String = "",
    val is_cancelled: Boolean = false
)
