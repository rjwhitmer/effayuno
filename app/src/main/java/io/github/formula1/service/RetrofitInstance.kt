package io.github.formula1.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
     val driversApi: DriversService by lazy {
        Retrofit.Builder()
            .baseUrl(HttpRoutes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DriversService::class.java)
    }

    val sessionsApi: SessionService by lazy {
        Retrofit.Builder()
            .baseUrl(HttpRoutes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SessionService::class.java)
    }

    val teamChampionshipApi: TeamChampionshipService by lazy {
        Retrofit.Builder()
            .baseUrl(HttpRoutes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TeamChampionshipService::class.java)
    }

    val meetingApi: MeetingService by lazy {
        Retrofit.Builder()
            .baseUrl(HttpRoutes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MeetingService::class.java)
    }

    val startingGridApi: StartingGridService by lazy {
        Retrofit.Builder()
            .baseUrl(HttpRoutes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StartingGridService::class.java)
    }
}