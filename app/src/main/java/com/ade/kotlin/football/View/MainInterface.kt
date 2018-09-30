package com.ade.kotlin.football.View

import com.ade.kotlin.football.Model.Match


interface MainInterface{
    fun showLoading()
    fun hideLoading()
    fun showMatchList(data: List<Match>?)
}