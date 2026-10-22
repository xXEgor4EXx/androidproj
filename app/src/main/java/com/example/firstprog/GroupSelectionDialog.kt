package com.example.firstprog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstprog.classes.EducationFormsResponse
import com.example.firstprog.classes.ScheduleItem
import com.example.firstprog.classes.TreeScheduleAdapter
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader

class GroupSelectionDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_group_selection, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.dialogRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = TreeScheduleAdapter { selectedGroup ->
            Toast.makeText(
                requireContext(),
                "Выбрана группа: ${selectedGroup.name}",
                Toast.LENGTH_SHORT
            ).show()

            dismiss()
        }
        recyclerView.adapter = adapter

        val jsonString = loadJsonFromRaw(R.raw.form_edu)
        if (jsonString != null) {
            try {
                val response = Gson().fromJson(jsonString, EducationFormsResponse::class.java)
                val rootItems = response.educationForms.map { formEdu ->
                    ScheduleItem.FormEduItem(
                        id = formEdu.id,
                        name = formEdu.name,
                        courses = formEdu.courses
                    )
                }
                adapter.setData(rootItems)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return view
    }

    private fun loadJsonFromRaw(resourceId: Int): String? {
        return try {
            val inputStream = resources.openRawResource(resourceId)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String? = reader.readLine()
            while (line != null) {
                stringBuilder.append(line)
                line = reader.readLine()
            }
            reader.close()
            inputStream.close()
            stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
