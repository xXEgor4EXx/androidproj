package com.example.firstprog.network

import com.google.gson.annotations.SerializedName

data class ScheduleResponse(
    @SerializedName("Month")
    val months: List<MonthSchedule> = emptyList()
)

data class MonthSchedule(
    @SerializedName("Name")
    val name: String = "",
    @SerializedName("Numb")
    val number: Int = 0,
    @SerializedName("Sched")
    val sched: List<DaySchedule> = emptyList()
)

data class DaySchedule(
    @SerializedName("datePair")
    val datePair: String = "",
    @SerializedName("dayWeek")
    val dayWeek: String = "",
    @SerializedName("dayWeekShort")
    val dayWeekShort: String = "",
    @SerializedName("mainSchedule")
    val lessons: List<Lesson> = emptyList()
)

data class Lesson(
    @SerializedName("TimeStart")
    val timeStart: String = "",
    @SerializedName("SubjName")
    val subjName: String = "",
    @SerializedName("SubjSN")
    val subjShortName: String = "",
    @SerializedName("LoadKindSN")
    val loadKind: String = "",
    @SerializedName("FIO")
    val teacherFio: String = "",
    @SerializedName("Aud")
    val auditorium: String = ""
)
