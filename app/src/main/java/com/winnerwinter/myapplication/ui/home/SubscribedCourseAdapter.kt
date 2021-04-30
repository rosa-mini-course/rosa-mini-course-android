package com.winnerwinter.myapplication.ui.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.winnerwinter.myapplication.R

class SubscribedCourseAdapter(activityContext: Context, itemList: MutableList<List<String>>) : RecyclerView.Adapter<SubscribedCourseAdapter.SubscribedCourseViewHolder>() {

    val context = activityContext
    val allSubscribedCourses= itemList

    class SubscribedCourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var subscribedCourseName: TextView = itemView.findViewById(R.id.tv_subscribed_course_name)
        var subscribedCourseLecturer: TextView = itemView.findViewById(R.id.tv_subscribed_course_lecturer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscribedCourseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView: View = layoutInflater.inflate(R.layout.subscribed_course_cell, parent, false)
        return SubscribedCourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SubscribedCourseViewHolder, position: Int) {
        val subscribedCourse = allSubscribedCourses.get(position)
        val courseId = subscribedCourse[0]
        val courseName = subscribedCourse[1]
        val lecturerEmail = subscribedCourse[2]
        holder.subscribedCourseName.text = courseName
        holder.subscribedCourseLecturer.text = lecturerEmail

        // TODO 点击单个课程跳转到该课程的详情页
        holder.itemView.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return allSubscribedCourses.size
    }
}