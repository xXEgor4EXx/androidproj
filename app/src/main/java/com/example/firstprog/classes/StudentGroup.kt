package com.example.firstprog.classes

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StudentGroup(
    @SerializedName("GS_ID")
    val id: Int,

    @SerializedName("GSName")
    val name: String
) : Serializable
