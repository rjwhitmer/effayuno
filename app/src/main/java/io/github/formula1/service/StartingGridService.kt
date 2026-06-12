package io.github.formula1.service

import io.github.formula1.model.dto.StartingGridResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface StartingGridService {
    @GET(HttpRoutes.STARTING_GRID)
    suspend fun getPolePosition(@QueryMap params: Map<String, Int>): Response<List<StartingGridResponse>>
}