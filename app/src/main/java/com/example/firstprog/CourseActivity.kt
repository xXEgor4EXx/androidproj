package com.example.firstprog

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstprog.classes.EducationForm
import com.google.android.material.appbar.MaterialToolbar

class CourseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_course)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.courseRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        @Suppress("DEPRECATION")
        val form = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("EXTRA_FORM", EducationForm::class.java)
        } else {
            intent.getSerializableExtra("EXTRA_FORM") as? EducationForm
        }

        if (form == null) {
            finish()
            return
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = form.name
        toolbar.setNavigationOnClickListener { finish() }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val courses = form.courses
        val adapter = SelectionAdapter(courses.map { "${it.number} курс" }) { position ->
            val selectedCourse = courses[position]
            val intent = Intent(this, GroupActivity::class.java).apply {
                putExtra("EXTRA_COURSE", selectedCourse)
                putExtra("EXTRA_FORM_NAME", form.name)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }
}
