package com.ade.kotlin.football

import com.ade.kotlin.football.Model.Match
import com.ade.kotlin.football.Model.Team
import com.google.gson.annotations.SerializedName

data class Response(
        @field:SerializedName("events")
        val events: List<Match>? = null,

        @field:SerializedName("teams")
        val teamId: List<Team>? = null
)