package com.ade.kotlin.football.View

import com.ade.kotlin.football.Model.Team


interface DetailInterface{
    fun showLoading()
    fun hideLoading()
    fun showTeamsList(data: List<Team>?, data2: List<Team>?)
}