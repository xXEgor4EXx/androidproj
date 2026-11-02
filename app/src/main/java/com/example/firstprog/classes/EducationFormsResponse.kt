package com.example.firstprog.classes

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EducationFormsResponse(
    @SerializedName("FormEdu")
    val educationForms: List<EducationForm>
) : Serializable
