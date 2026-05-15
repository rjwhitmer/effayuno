package io.github.formula1.repository

import io.github.formula1.model.Resource
import io.github.formula1.model.dto.DriverChampionship
import io.github.formula1.model.dto.DriverResponse
import io.github.formula1.service.RetrofitInstance

class DriverRepository {
    suspend fun getDrivers(): Resource<List<DriverResponse>> {
        return try {
            val response = RetrofitInstance.driversApi.getDrivers()
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Driver Response body is null")
            } else {
                Resource.Error("Error: ${response.code()} = ${response.message() }")
            }
        } catch (e: Exception) {
            Resource.Error("Failed to fetch drivers: ${e.message}")
        }
    }

    suspend fun getPastDrivers(meetingKey: Int, sessionKey: Int): Resource<List<DriverResponse>> {
        return try {
            val params = mutableMapOf<String, Int>()
            params["meeting_key"] = meetingKey
            params["session_key"] = sessionKey
            val response = RetrofitInstance.driversApi.getPastDrivers(params)
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Driver Response body is null")
            } else {
                Resource.Error("Error: ${response.code()} = ${response.message() }")
            }
        } catch (e: Exception) {
            Resource.Error("Failed to fetch drivers: ${e.message}")
        }
    }

    suspend fun getDriverChampionships(): Resource<List<DriverChampionship>> {
        return try {
            val response = RetrofitInstance.driversApi.getDriverChampionships()
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Driver Championship Response body is null")
            } else {
                Resource.Error("Error: ${response.code()} = ${response.message() }")
            }
        } catch (e: Exception) {
            Resource.Error("Failed to fetch driver championships: ${e.message}")
        }
    }

    suspend fun getDriversSessionSpecific(sessionKey: Int, meetingKey: Int): Resource<List<DriverResponse>> {
        val params = mutableMapOf<String, Int>()
        params["meeting_key"] = meetingKey
        params["session_key"] = sessionKey
        return try {
            val response = RetrofitInstance.driversApi.getDriversSessionSpecific(params)
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Driver Response body is null")
            } else {
                Resource.Error("Failed to fetch drivers: ${response.code()} = ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Failed to fetch drivers: ${e.message}")
        }
    }
}