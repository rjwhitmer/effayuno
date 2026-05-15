package io.github.formula1.service.navigation

sealed class Route(val navRoute: String) {
    object Main: Route("main")
    object SessionList: Route("session_list")
    object SessionResult: Route("session_result")
    object Teams: Route("teams")
    object Drivers: Route(navRoute = "drivers")
}