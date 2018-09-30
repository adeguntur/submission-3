package com.ade.kotlin.football.Presenter

import com.ade.kotlin.football.Api.API
import com.ade.kotlin.football.Api.ApiObject
import com.ade.kotlin.football.CCProvider
import com.ade.kotlin.football.Response
import com.ade.kotlin.football.View.MainInterface
import com.google.gson.Gson
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg

class Next(private val anInterface: MainInterface,
           private val api: API,
           private val gson: Gson,
           private val context: CCProvider = CCProvider()){

    fun getMatchList(match: String?){
        anInterface.showLoading()

        async(context.main) {
            val data = bg {
                gson.fromJson(api
                        .doRequest(ApiObject.getNextMatch(match)),
                        Response::class.java
                )
            }
            anInterface.showMatchList(data.await().events)
            anInterface.hideLoading()
        }
    }
}