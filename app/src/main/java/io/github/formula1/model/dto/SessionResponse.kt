package io.github.formula1.model.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class SessionResponse(
    val circuit_short_name: String = "",
    val session_name: String = "",
    val session_type: String = "",
    val country_name: String = "",
    val date_start: String = "",
    val date_end: String = "",
    val year: Int = 0,
    val gmt_offset: String = "",
    val meeting_key: Int = 0,
    val session_key: Int = 0,
    var is_sprint_weekend: Boolean = false,
    var is_cancelled: Boolean = false
)