package com.example.firstprog

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstprog.classes.EducationForm
import com.example.firstprog.classes.EducationFormsResponse
import com.example.firstprog.network.RetrofitClient
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class FormEduActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_form_edu)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.formEduRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)

        toolbar.setNavigationOnClickListener { finish() }

        recyclerView.layoutManager = LinearLayoutManager(this)

        loadEducationForms()
    }

    private fun loadEducationForms() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getScheduleIniData()
                displayForms(response.educationForms)
            } catch (_: Exception) {
                loadFormsFromLocal()
            }
        }
    }

    private fun loadFormsFromLocal() {
        val jsonString = loadJsonFromRaw(R.raw.form_edu)
        if (jsonString != null) {
            try {
                val response = Gson().fromJson(jsonString, EducationFormsResponse::class.java)
                displayForms(response.educationForms)
                return
            } catch (_: Exception) { }
        }
        progressBar.visibility = View.GONE
        Snackbar.make(recyclerView, R.string.schedule_tree_error, Snackbar.LENGTH_LONG).show()
    }

    private fun displayForms(forms: List<EducationForm>) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        val adapter = SelectionAdapter(forms.map { it.name }) { position ->
            val selectedForm = forms[position]
            val intent = Intent(this, CourseActivity::class.java).apply {
                putExtra("EXTRA_FORM", selectedForm)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    private fun loadJsonFromRaw(resourceId: Int): String? {
        return try {
            val inputStream = resources.openRawResource(resourceId)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val sb = StringBuilder()
            var line: String? = reader.readLine()
            while (line != null) {
                sb.append(line)
                line = reader.readLine()
            }
            reader.close()
            inputStream.close()
            sb.toString()
        } catch (_: Exception) {
            null
        }
    }
}
