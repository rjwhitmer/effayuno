package io.github.formula1.service

import io.github.formula1.model.dto.MeetingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MeetingService {
    @GET(HttpRoutes.MEETINGS)
    suspend fun getMeetings(@Query("year") year: String): Response<List<MeetingResponse>>
}