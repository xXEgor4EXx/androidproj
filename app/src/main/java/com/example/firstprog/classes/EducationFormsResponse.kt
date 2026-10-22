package com.example.firstprog.classes

import com.google.gson.annotations.SerializedName

data class EducationFormsResponse(
    @SerializedName("FormEdu")
    val educationForms: List<EducationForm>
)
