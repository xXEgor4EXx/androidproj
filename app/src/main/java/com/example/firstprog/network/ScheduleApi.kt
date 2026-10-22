package com.example.firstprog.network

import com.example.firstprog.classes.EducationFormsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ScheduleApi {

    @GET("GetGsSchedIniData")
    suspend fun getScheduleIniData(): EducationFormsResponse

    @GET("GetGsSched")
    suspend fun getGroupSchedule(@Query("grpid") groupId: Int): ScheduleResponse
}
