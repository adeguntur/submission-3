package com.ade.kotlin.football.Presenter

import com.ade.kotlin.football.Api.API
import com.ade.kotlin.football.Api.ApiObject
import com.ade.kotlin.football.Response
import com.ade.kotlin.football.View.DetailInterface
import com.google.gson.Gson
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg

class Detail(private val anInterface: DetailInterface,
             private val api: API,
             private val gson: Gson){

    fun getBadgeList(team: String?, team2: String?){
        anInterface.showLoading()

        async(UI){
            val data = bg {
                gson.fromJson(api
                        .doRequest(ApiObject.getHomeBadge(team)),
                        Response::class.java
                )
            }
            val data2 = bg {
                gson.fromJson(api
                        .doRequest(ApiObject.getAwayBadge(team2)),
                        Response::class.java
                )
            }

            anInterface.showTeamsList(data.await().teamId, data2.await().teamId)
            anInterface.hideLoading()
        }
    }
}