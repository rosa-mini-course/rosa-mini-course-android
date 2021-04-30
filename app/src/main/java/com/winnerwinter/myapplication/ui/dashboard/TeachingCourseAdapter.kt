package com.winnerwinter.myapplication.ui.dashboard

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.winnerwinter.myapplication.R

class TeachingCourseAdapter(activityContext: Context, itemList: List<List<String>>) :
    RecyclerView.Adapter<TeachingCourseAdapter.TeachingCourseViewHolder>() {

    val context = activityContext
    val allTeachingCourses = itemList

    class TeachingCourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var teachingCourseNameTextView: TextView = itemView.findViewById(R.id.tv_teaching_course_name)
        var teachingLecturerTextView: TextView = itemView.findViewById(R.id.tv_teaching_lecturer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeachingCourseViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val itemView: View = layoutInflater.inflate(R.layout.teaching_course_cell, parent, false)
        return TeachingCourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TeachingCourseViewHolder, position: Int) {
        val teachingCourse = allTeachingCourses.get(position)
        val courseId = teachingCourse[0]
        val courseName = teachingCourse[1]
        val lecturerEmail = teachingCourse[2]
        holder.teachingCourseNameTextView.text = courseName
        holder.teachingLecturerTextView.text = lecturerEmail

        // 点击单个课程跳转到该课程的详情页
        holder.itemView.setOnClickListener {
            val intent = Intent(context, TeachingCourseDetailsActivity::class.java)
            intent.putExtra("courseId", courseId)
            intent.putExtra("courseName", courseName)
            intent.putExtra("lecturerEmail", lecturerEmail)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return allTeachingCourses.size
    }
}