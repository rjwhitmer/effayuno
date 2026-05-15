package io.github.formula1.service

import io.github.formula1.model.dto.TeamChampionshipResponse
import retrofit2.Response
import retrofit2.http.GET

interface TeamChampionshipService {
    @GET(HttpRoutes.CONSTRUCTORS)
    suspend fun getTeamChampionships(): Response<List<TeamChampionshipResponse>>
}