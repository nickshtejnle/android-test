package com.example.androidtest

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("search_by_date")
    fun getHits(@Query("page") page: Long, @Query("tags") tags: String = "story"): Observable<ApiResponse>

}