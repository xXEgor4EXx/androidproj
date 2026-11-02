package com.example.firstprog.classes

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Course(
    @SerializedName("Curs")
    val number: Int,

    @SerializedName("arr")
    val groups: List<StudentGroup>
) : Serializable
