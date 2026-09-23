package com.example.firstprog.classes

import com.google.gson.annotations.SerializedName

data class EducationFormsResponse(
    @SerializedName("FormEdu")
    val educationForms: List<EducationForm>
)

data class EducationForm(
    @SerializedName("FormEdu_ID")
    val id: Int,

    @SerializedName("FormEduName")
    val name: String,

    @SerializedName("arr")
    val courses: List<Course>
)

data class Course(
    @SerializedName("Curs")
    val number: Int,

    @SerializedName("arr")
    val groups: List<StudentGroup>
)

data class StudentGroup(
    @SerializedName("GS_ID")
    val id: Int,

    @SerializedName("GSName")
    val name: String
)