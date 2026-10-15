package com.example.firstprog.classes

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firstprog.R

sealed class ScheduleItem {
    data class FormEduItem(
        val id: Int,
        val name: String,
        val courses: List<Course>,
        var isExpanded: Boolean = false
    ) : ScheduleItem()

    data class CourseItem(
        val courseNumber: Int,
        val parentFormEduId: Int,
        val groups: List<StudentGroup>,
        var isExpanded: Boolean = false
    ) : ScheduleItem()

    data class GroupItem(
        val id: Int,
        val name: String
    ) : ScheduleItem()
}

@Suppress("unused")
class TreeScheduleAdapter(
    private val onGroupClicked: (StudentGroup) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<ScheduleItem>()

    companion object {
        private const val TYPE_FORM_EDU = 0
        private const val TYPE_COURSE = 1
        private const val TYPE_GROUP = 2
    }

    @Suppress("NotifyDataSetChanged")
    fun setData(newRootItems: List<ScheduleItem.FormEduItem>) {
        items.clear()
        items.addAll(newRootItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ScheduleItem.FormEduItem -> TYPE_FORM_EDU
            is ScheduleItem.CourseItem -> TYPE_COURSE
            is ScheduleItem.GroupItem -> TYPE_GROUP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_FORM_EDU -> FormViewHolder(inflater.inflate(R.layout.item_form_edu, parent, false))
            TYPE_COURSE -> CourseViewHolder(inflater.inflate(R.layout.item_course, parent, false))
            TYPE_GROUP -> GroupViewHolder(inflater.inflate(R.layout.item_group, parent, false))
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ScheduleItem.FormEduItem -> (holder as FormViewHolder).bind(item)
            is ScheduleItem.CourseItem -> (holder as CourseViewHolder).bind(item)
            is ScheduleItem.GroupItem -> (holder as GroupViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class FormViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tvName)
        private val ivIndicator = itemView.findViewById<ImageView>(R.id.ivIndicator)

        fun bind(item: ScheduleItem.FormEduItem) {
            tvName.text = item.name
            ivIndicator.setImageResource(if (item.isExpanded) android.R.drawable.ic_menu_close_clear_cancel else android.R.drawable.ic_input_add)

            itemView.setOnClickListener {
                @Suppress("DEPRECATION")
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

                if (item.isExpanded) {
                    collapseFormEdu(pos, item)
                } else {
                    expandFormEdu(pos, item)
                }
            }
        }
    }

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tvName)
        private val ivIndicator = itemView.findViewById<ImageView>(R.id.ivIndicator)

        @SuppressLint("SetTextI18n")
        fun bind(item: ScheduleItem.CourseItem) {
            val context = itemView.context
            tvName.text = "${item.courseNumber} курс"
            tvName.setTypeface(null, Typeface.BOLD)
            ivIndicator.setImageResource(if (item.isExpanded) android.R.drawable.ic_menu_close_clear_cancel else android.R.drawable.ic_input_add)

            itemView.setOnClickListener {
                @Suppress("DEPRECATION")
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

                if (item.isExpanded) {
                    collapseCourse(pos, item)
                } else {
                    expandCourse(pos, item)
                }
            }
        }
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvGroupName = itemView.findViewById<TextView>(R.id.tvGroupName)

        fun bind(item: ScheduleItem.GroupItem) {
            tvGroupName.text = item.name
            itemView.setOnClickListener {
                onGroupClicked(StudentGroup(item.id, item.name))
            }
        }
    }

    private fun expandFormEdu(position: Int, item: ScheduleItem.FormEduItem) {
        item.isExpanded = true
        notifyItemChanged(position)

        val coursesToInsert = item.courses.map {
            ScheduleItem.CourseItem(it.number, item.id, it.groups)
        }

        items.addAll(position + 1, coursesToInsert)
        notifyItemRangeInserted(position + 1, coursesToInsert.size)
    }

    private fun collapseFormEdu(position: Int, item: ScheduleItem.FormEduItem) {
        item.isExpanded = false
        notifyItemChanged(position)

        var countToRemove = 0
        val nextPos = position + 1

        while (nextPos < items.size) {
            val current = items[nextPos]
            if (current is ScheduleItem.CourseItem && current.parentFormEduId == item.id) {
                countToRemove++
                items.removeAt(nextPos)
            } else if (current is ScheduleItem.GroupItem) {
                countToRemove++
                items.removeAt(nextPos)
            } else {
                break
            }
        }

        if (countToRemove > 0) {
            notifyItemRangeRemoved(position + 1, countToRemove)
        }
    }

    private fun expandCourse(position: Int, item: ScheduleItem.CourseItem) {
        item.isExpanded = true
        notifyItemChanged(position)

        val groupsToInsert = item.groups.map {
            ScheduleItem.GroupItem(it.id, it.name)
        }

        items.addAll(position + 1, groupsToInsert)
        notifyItemRangeInserted(position + 1, groupsToInsert.size)
    }

    private fun collapseCourse(position: Int, item: ScheduleItem.CourseItem) {
        item.isExpanded = false
        notifyItemChanged(position)

        var countToRemove = 0
        while (position + 1 < items.size && items[position + 1] is ScheduleItem.GroupItem) {
            items.removeAt(position + 1)
            countToRemove++
        }

        if (countToRemove > 0) {
            notifyItemRangeRemoved(position + 1, countToRemove)
        }
    }
}
