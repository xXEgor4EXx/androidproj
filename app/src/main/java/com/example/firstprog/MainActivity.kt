package com.example.firstprog

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstprog.classes.EducationFormsResponse
import com.example.firstprog.classes.ScheduleItem
import com.example.firstprog.classes.TreeScheduleAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn = findViewById<Button>(R.id.buttonToast)
        btn.setOnClickListener {
            Toast.makeText(baseContext, "Test", Toast.LENGTH_LONG).show()
        }

        val btn2 = findViewById<Button>(R.id.buttonSnack)
        btn2.setOnClickListener {
            Snackbar.make(it, "Снээээк!!!", Snackbar.LENGTH_SHORT).show()
        }

        val editText = findViewById<EditText>(R.id.editText1)
        val buttonSub = findViewById<Button>(R.id.buttonToSecond)

        buttonSub.setOnClickListener {
            val inputText = editText.text.toString().trim()
            if (inputText.isEmpty()) {
                editText.error = "Поле пустое!"
            } else {
                val intent = Intent(this, SecondActivity::class.java).apply {
                    putExtra("USER_TEXT", inputText)
                }
                startActivity(intent)
            }
        }

        val btnToSched = findViewById<Button>(R.id.btnToSched)
        btnToSched.setOnClickListener {
            val dialog = GroupSelectionDialog()
            dialog.show(supportFragmentManager, "GroupSelectionDialog")
        }

        val adapter = TreeScheduleAdapter { selectedGroup ->
            Toast.makeText(
                this,
                "Группа выбрана: ${selectedGroup.name} (ID: ${selectedGroup.id})",
                Toast.LENGTH_SHORT
            ).show()
        }

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
                Toast.makeText(this, "Ошибка парсинга JSON структуры", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Не удалось прочитать файл ресурсов form_edu.json", Toast.LENGTH_LONG).show()
        }
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
}
