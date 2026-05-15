package io.github.formula1.service

import io.github.formula1.model.dto.DriverChampionship
import io.github.formula1.model.dto.DriverResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap
import java.sql.Driver

interface DriversService {
    @GET(HttpRoutes.DRIVERS)
    suspend fun getDrivers(): Response<List<DriverResponse>>

    @GET(HttpRoutes.PAST_DRIVERS)
    suspend fun getPastDrivers(@QueryMap params: Map<String, Int>): Response<List<DriverResponse>>

    @GET(HttpRoutes.CHAMPIONSHIP)
    suspend fun getDriverChampionships(): Response<List<DriverChampionship>>

    @GET(HttpRoutes.DRIVERS)
    suspend fun getDriversSessionSpecific(@QueryMap params: Map<String, Int>): Response<List<DriverResponse>>
}