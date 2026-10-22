package com.example.firstprog.network

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class ScheduleResponse(
    @SerializedName("Month")
    val months: List<MonthSchedule>
)

data class MonthSchedule(
    @SerializedName("Name")
    val name: String,
    @SerializedName("Numb")
    val number: Int,
    @SerializedName("Sched")
    val entries: List<JsonObject>
)
