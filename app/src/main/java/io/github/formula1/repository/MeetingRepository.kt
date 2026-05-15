package io.github.formula1.repository

import android.util.Log
import io.github.formula1.model.Resource
import io.github.formula1.model.dto.MeetingResponse
import io.github.formula1.service.RetrofitInstance

class MeetingRepository {
    suspend fun getMeetings(year: String): Resource<List<MeetingResponse>> {
        return try {
            val response = RetrofitInstance.meetingApi.getMeetings(year)
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Session Response body is null")
            } else {
                Log.v("Response", response.message())
                Resource.Error(message = "Error: ${response.code()} = ${response.message()}")
            }
        } catch (e: Exception) {
            Log.v("Error", e.message ?: "")
            Resource.Error("Failed to fetch sessions: ${e.message}")
        }
    }
}