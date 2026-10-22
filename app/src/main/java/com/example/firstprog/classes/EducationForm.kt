package com.example.firstprog.classes

import com.google.gson.annotations.SerializedName


data class EducationForm(
    @SerializedName("FormEdu_ID")
    val id: Int,

    @SerializedName("FormEduName")
    val name: String,

    @SerializedName("arr")
    val courses: List<Course>
)