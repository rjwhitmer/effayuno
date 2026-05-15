package io.github.formula1.model.dto

data class TeamChampionshipResponse(
    val meeting_key: Int,
    val points_current: Int,
    val points_start: Int,
    val position_current: Int,
    val position_start: Int,
    val session_key: Int,
    val team_name: String
)
