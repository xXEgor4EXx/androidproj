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
import com.example.firstprog.classes.Course
import com.google.android.material.appbar.MaterialToolbar

class GroupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_group)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.groupRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        @Suppress("DEPRECATION")
        val course = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("EXTRA_COURSE", Course::class.java)
        } else {
            intent.getSerializableExtra("EXTRA_COURSE") as? Course
        }

        if (course == null) {
            finish()
            return
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = "${course.number} курс"
        toolbar.setNavigationOnClickListener { finish() }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val groups = course.groups
        val adapter = SelectionAdapter(groups.map { it.name }) { position ->
            val selectedGroup = groups[position]
            val intent = Intent(this, ScheduleActivity::class.java).apply {
                putExtra("EXTRA_GROUP_ID", selectedGroup.id)
                putExtra("EXTRA_GROUP_NAME", selectedGroup.name)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }
}
