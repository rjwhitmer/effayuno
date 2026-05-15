package io.github.formula1.repository

import io.github.formula1.model.Resource
import io.github.formula1.model.dto.TeamChampionshipResponse
import io.github.formula1.service.RetrofitInstance

class TeamChampionshipRepository {
    suspend fun getTeamChampionships(): Resource<List<TeamChampionshipResponse>> {
        return try {
            val response = RetrofitInstance.teamChampionshipApi.getTeamChampionships()
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Team Championship Response body is null")
            } else {
                Resource.Error("Error: ${response.code()} = ${response.message() }")
            }
        } catch (e: Exception) {
            Resource.Error("Failed to fetch drivers: ${e.message}")
        }
    }
}