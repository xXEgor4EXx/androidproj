package com.example.firstprog

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstprog.network.RetrofitClient
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class ScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_schedule)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scheduleRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val groupId = intent.getIntExtra("EXTRA_GROUP_ID", -1)
        val groupName = intent.getStringExtra("EXTRA_GROUP_NAME") ?: ""

        if (groupId == -1) {
            finish()
            return
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = groupName
        toolbar.setNavigationOnClickListener { finish() }

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val rvSchedule = findViewById<RecyclerView>(R.id.rvSchedule)
        val tvEmpty = findViewById<TextView>(R.id.tvEmpty)

        rvSchedule.layoutManager = LinearLayoutManager(this)

        progressBar.visibility = View.VISIBLE
        rvSchedule.visibility = View.GONE
        tvEmpty.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getGroupSchedule(groupId)
                progressBar.visibility = View.GONE

                val allEntries = response.months.flatMap { month ->
                    month.entries.map { entry -> "${month.name}: $entry" }
                }

                if (allEntries.isNotEmpty()) {
                    rvSchedule.adapter = SelectionAdapter(allEntries) { }
                    rvSchedule.visibility = View.VISIBLE
                    tvEmpty.visibility = View.GONE
                } else {
                    tvEmpty.text = getString(R.string.schedule_empty)
                    tvEmpty.visibility = View.VISIBLE
                    rvSchedule.visibility = View.GONE
                }
            } catch (_: Exception) {
                progressBar.visibility = View.GONE
                tvEmpty.text = getString(R.string.schedule_load_error)
                tvEmpty.visibility = View.VISIBLE
                rvSchedule.visibility = View.GONE
            }
        }
    }
}
