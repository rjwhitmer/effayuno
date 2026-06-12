package io.github.formula1.repository

import android.util.Log
import io.github.formula1.model.Resource
import io.github.formula1.model.dto.StartingGridResponse
import io.github.formula1.service.RetrofitInstance

class StartingGridRepository {
    suspend fun getPolePosition(meetingKey: Int, position: Int): Resource<List<StartingGridResponse>> {
        val params = mutableMapOf<String, Int>()
        params["meeting_key"] = meetingKey
        params["position"] = position
        return try {
            val response = RetrofitInstance.startingGridApi.getPolePosition(params)
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.v("Pole Success", it.toString())
                    Resource.Success(it)
                } ?: Resource.Error("Starting Grid response body is null")
            } else {
                Log.v("Error", "Failed to fetch pole position: ${response.code()} = ${response.message()}")
                Resource.Error("Failed to fetch pole position: ${response.code()} = ${response.message()}")
            }
        } catch (e: Exception) {
            Log.v("Error", e.message ?: "")
            Resource.Error("Failed to fetch staring grid: ${e.message}")
        }
    }
}