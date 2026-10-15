package com.example.firstprog.classes

import com.google.gson.annotations.SerializedName

data class Course(
    @SerializedName("Curs")
    val number: Int,

    @SerializedName("arr")
    val groups: List<StudentGroup>
)