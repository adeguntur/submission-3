package com.kotlin.anggie.submissions3.api

import com.kotlin.anggie.submissions3.model.EventResponse
import com.kotlin.anggie.submissions3.model.TeamResponse
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import io.reactivex.Observable

interface ApiService {

    companion object {
        val instance: ApiService by lazy {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://www.thesportsdb.com/api/v1/json/1/")
                    .build()
            retrofit.create(ApiService::class.java)
        }
    }

    @GET("eventspastleague.php?id=4328")
    fun getLastEvent(): Observable<EventResponse>

    @GET("eventsnextleague.php?id=4328")
    fun getNextEvent(): Observable<EventResponse>

    @GET("lookupteam.php")
    fun getTeamDetail(@Query("id") id: String): Observable<TeamResponse>
}
