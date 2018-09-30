package com.kotlin.anggie.submissions3.model

import com.google.gson.annotations.SerializedName

data class EventResponse(
        @SerializedName("events") val events: List<Event>?
)