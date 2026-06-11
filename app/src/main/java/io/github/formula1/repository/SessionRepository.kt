package io.github.formula1.repository

import android.util.Log
import io.github.formula1.model.Resource
import io.github.formula1.model.dto.QualifierSessionResult
import io.github.formula1.model.dto.RaceSessionResult
import io.github.formula1.model.dto.SessionResponse
import io.github.formula1.model.dto.StartingGridResponse
import io.github.formula1.service.RetrofitInstance
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class SessionRepository {
    suspend fun getCurrentYearSessions(currentYear: String): Resource<List<SessionResponse>> {
        return try {
            val response = RetrofitInstance.sessionsApi.getCurrentYearSessions(currentYear)
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

    @OptIn(ExperimentalTime::class)
    suspend fun getCurrentYearPastSessions(currentYear: String): Resource<List<SessionResponse>> {
        val params = mutableMapOf<String, String>()
        params["year"] = currentYear
        params["date_start>"] = "${currentYear}-01-01"
        params["date_end<"] = "${Clock.System.now()}"
        return try {
            val response = RetrofitInstance.sessionsApi.getCurrentYearPastSessions(params)
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Session Response body is null")
            } else {
                Log.v("Error", "Failed to fetch race sessions: ${response.code()} = ${response.message()}")
                Resource.Error("Failed to fetch sessions: ${response.code()} = ${response.message()}, URL: ${response.raw()}")
            }
        } catch (e: Exception) {
            Log.v("Error", e.message ?: "")
            Resource.Error("Failed to fetch sessions: ${e.message}")
        }
    }

    suspend fun getQualifyingSessionResult(meetingKey: Int, sessionKey: Int): Resource<List<QualifierSessionResult>> {
        val params = mutableMapOf<String, Int>()
        params["meeting_key"] = meetingKey
        params["session_key"] = sessionKey
        return try {
            val response = RetrofitInstance.sessionsApi.getQualifyingSessionResult(params)
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Qualifying Session response body is null")
            } else {
                Log.v("Error", "Failed to fetch qualifying sessions: ${response.code()} = ${response.message()}")
                Resource.Error("Failed to fetch qualifying sessions: ${response.code()} = ${response.message()}")
            }
        } catch (e: Exception) {
            Log.v("Error", e.message ?: "")
            Resource.Error("Failed to fetch sessions: ${e.message}")
        }
    }

    suspend fun getRaceSessionResult(meetingKey: Int, sessionKey: Int): Resource<List<RaceSessionResult>> {
        val params = mutableMapOf<String, Int>()
        params["meeting_key"] = meetingKey
        params["session_key"] = sessionKey
        return try {
            val response = RetrofitInstance.sessionsApi.getRaceSessionResult(params)
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.v("Success", it.toString())
                    Resource.Success(it)
                } ?: Resource.Error("Race Session response body is null")
            } else {
                Log.v("Error", "Failed to fetch race sessions: ${response.code()} = ${response.message()}")
                Resource.Error("Failed to fetch race sessions: ${response.code()} = ${response.message()}")
            }
        } catch (e: Exception) {
            Log.v("Error", e.message ?: "")
            Resource.Error("Failed to fetch sessions: ${e.message}")
        }
    }

    suspend fun getPolePosition(meetingKey: Int, position: Int): Resource<StartingGridResponse> {
        val params = mutableMapOf<String, Int>()
        params["meeting_key"] = meetingKey
        params["position"] = position
        return try {
            val response = RetrofitInstance.sessionsApi.getPolePosition(params)
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