package io.github.formula1.service

import io.github.formula1.model.dto.QualifierSessionResult
import io.github.formula1.model.dto.RaceSessionResult
import io.github.formula1.model.dto.SessionResponse
import io.github.formula1.model.dto.StartingGridResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface SessionService {
    @GET(HttpRoutes.SESSIONS)
    suspend fun getCurrentYearSessions(@Query("year") currentYear: String): Response<List<SessionResponse>>

    @GET(HttpRoutes.SESSIONS)
    suspend fun getCurrentYearPastSessions(@QueryMap params: Map<String, String>): Response<List<SessionResponse>>

    @GET(HttpRoutes.SESSION_RESULT)
    suspend fun getRaceSessionResult(@QueryMap params: Map<String, Int>): Response<List<RaceSessionResult>>

    @GET(HttpRoutes.SESSION_RESULT)
    suspend fun getQualifyingSessionResult(@QueryMap params: Map<String, Int>): Response<List<QualifierSessionResult>>
    @GET(HttpRoutes.STARTING_GRID)
    suspend fun getPolePosition(@QueryMap params: Map<String, Int>): Response<StartingGridResponse>
}