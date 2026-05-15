package io.github.formula1.service

object HttpRoutes {
    const val BASE_URL = "https://api.openf1.org/v1/"
    const val DRIVERS = "drivers?session_key=latest"
    const val PAST_DRIVERS = "drivers"
    const val SESSIONS = "sessions"
    const val SESSION_RESULT = "session_result"
    const val CHAMPIONSHIP = "championship_drivers?session_key=latest"
    const val CONSTRUCTORS = "championship_teams?session_key=latest"
    const val MEETINGS = "meetings"
}