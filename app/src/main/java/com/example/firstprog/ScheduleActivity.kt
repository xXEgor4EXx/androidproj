package com.example.firstprog

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstprog.classes.EducationFormsResponse
import com.example.firstprog.classes.ScheduleItem
import com.example.firstprog.classes.TreeScheduleAdapter
import com.example.firstprog.network.RetrofitClient
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class ScheduleActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvTree: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var scheduleContainer: View
    private lateinit var tvGroupTitle: TextView
    private lateinit var rvSchedule: RecyclerView
    private lateinit var tvEmpty: TextView
    private var isShowingSchedule = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_schedule)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scheduleRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolbar = findViewById(R.id.toolbar)
        rvTree = findViewById(R.id.rvTree)
        progressBar = findViewById(R.id.progressBar)
        scheduleContainer = findViewById(R.id.scheduleContainer)
        tvGroupTitle = findViewById(R.id.tvGroupTitle)
        rvSchedule = findViewById(R.id.rvSchedule)
        tvEmpty = findViewById(R.id.tvEmpty)

        toolbar.setNavigationOnClickListener { handleBack() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBack()
            }
        })

        rvTree.layoutManager = LinearLayoutManager(this)
        rvSchedule.layoutManager = LinearLayoutManager(this)

        loadTreeData()
    }

    private fun handleBack() {
        if (isShowingSchedule) {
            showTreeView()
        } else {
            finish()
        }
    }

    private fun showTreeView() {
        isShowingSchedule = false
        toolbar.title = getString(R.string.schedule_select_group)
        scheduleContainer.visibility = View.GONE
        rvTree.visibility = View.VISIBLE
    }

    private fun loadTreeData() {
        progressBar.visibility = View.VISIBLE
        rvTree.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getScheduleIniData()
                showTree(response)
            } catch (_: Exception) {
                loadTreeFromLocal()
            }
        }
    }

    private fun loadTreeFromLocal() {
        val jsonString = loadJsonFromRaw(R.raw.form_edu)
        if (jsonString != null) {
            try {
                val response = Gson().fromJson(jsonString, EducationFormsResponse::class.java)
                showTree(response)
                return
            } catch (_: Exception) { }
        }
        progressBar.visibility = View.GONE
        Snackbar.make(rvTree, R.string.schedule_tree_error, Snackbar.LENGTH_LONG).show()
    }

    private fun showTree(response: EducationFormsResponse) {
        val adapter = TreeScheduleAdapter { selectedGroup ->
            loadSchedule(selectedGroup.id, selectedGroup.name)
        }

        val rootItems = response.educationForms.map { formEdu ->
            ScheduleItem.FormEduItem(
                id = formEdu.id,
                name = formEdu.name,
                courses = formEdu.courses
            )
        }

        rvTree.adapter = adapter
        adapter.setData(rootItems)
        progressBar.visibility = View.GONE
        rvTree.visibility = View.VISIBLE
    }

    private fun loadSchedule(groupId: Int, groupName: String) {
        isShowingSchedule = true
        toolbar.title = groupName
        rvTree.visibility = View.GONE
        scheduleContainer.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getGroupSchedule(groupId)
                progressBar.visibility = View.GONE

                val hasData = response.months.any { it.entries.isNotEmpty() }
                tvGroupTitle.text = groupName
                scheduleContainer.visibility = View.VISIBLE

                if (hasData) {
                    tvEmpty.visibility = View.GONE
                    rvSchedule.visibility = View.VISIBLE
                } else {
                    tvEmpty.text = getString(R.string.schedule_empty)
                    tvEmpty.visibility = View.VISIBLE
                    rvSchedule.visibility = View.GONE
                }
            } catch (_: Exception) {
                progressBar.visibility = View.GONE
                tvGroupTitle.text = groupName
                tvEmpty.text = getString(R.string.schedule_load_error)
                tvEmpty.visibility = View.VISIBLE
                rvSchedule.visibility = View.GONE
                scheduleContainer.visibility = View.VISIBLE
            }
        }
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
