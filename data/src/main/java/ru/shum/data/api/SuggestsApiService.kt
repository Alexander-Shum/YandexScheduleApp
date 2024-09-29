package ru.shum.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.shum.data.model.SuggestionResponseApi

interface SuggestsApiService {

    @GET("all_suggests")
    suspend fun getSuggestions(
        @Query("part") part: String
    ): Response<SuggestionResponseApi>
}