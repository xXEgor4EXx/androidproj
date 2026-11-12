package com.example.firstprog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firstprog.network.DaySchedule

class ScheduleDayAdapter(
    private val days: List<DaySchedule>
) : RecyclerView.Adapter<ScheduleDayAdapter.DayViewHolder>() {

    class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDayHeader: TextView = view.findViewById(R.id.tvDayHeader)
        val llLessonsContainer: LinearLayout = view.findViewById(R.id.llLessonsContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day_schedule, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.tvDayHeader.text = if (day.dayWeek.isNotEmpty()) {
            "${day.datePair} (${day.dayWeek})"
        } else {
            day.datePair
        }

        holder.llLessonsContainer.removeAllViews()
        val inflater = LayoutInflater.from(holder.itemView.context)

        day.lessons.forEachIndexed { index, lesson ->
            val lessonView = inflater.inflate(R.layout.item_lesson, holder.llLessonsContainer, false)

            val tvTime = lessonView.findViewById<TextView>(R.id.tvTime)
            val tvType = lessonView.findViewById<TextView>(R.id.tvType)
            val tvAud = lessonView.findViewById<TextView>(R.id.tvAud)
            val tvSubject = lessonView.findViewById<TextView>(R.id.tvSubject)
            val tvTeacher = lessonView.findViewById<TextView>(R.id.tvTeacher)
            val lessonDivider = lessonView.findViewById<View>(R.id.lessonDivider)

            tvTime.text = lesson.timeStart
            tvType.text = lesson.loadKind
            tvAud.text = lesson.auditorium
            tvSubject.text = lesson.subjName
            tvTeacher.text = lesson.teacherFio

            if (lesson.loadKind.isEmpty()) {
                tvType.visibility = View.GONE
            }
            if (lesson.auditorium.isEmpty()) {
                tvAud.visibility = View.GONE
            }
            if (lesson.teacherFio.isEmpty()) {
                tvTeacher.visibility = View.GONE
            }

            lessonDivider.visibility = if (index < day.lessons.size - 1) View.VISIBLE else View.GONE

            holder.llLessonsContainer.addView(lessonView)
        }
    }

    override fun getItemCount(): Int = days.size
}
