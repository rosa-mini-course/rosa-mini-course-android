package com.winnerwinter.myapplication.ui.dashboard

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloNetworkException
import com.winnerwinter.RemoveTeachingCourseMutation
import com.winnerwinter.myapplication.ApolloManager
import com.winnerwinter.myapplication.R

class TeachingCourseAdapter(activity: BrowseTeachingCourseActivity, activityContext: Context, itemList: List<List<String>>) :
    RecyclerView.Adapter<TeachingCourseAdapter.TeachingCourseViewHolder>() {

    val context = activityContext
    var allTeachingCourses = itemList
    val activity = activity

    class TeachingCourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var teachingCourseNameTextView: TextView = itemView.findViewById(R.id.tv_teaching_course_name)
        var teachingLecturerTextView: TextView = itemView.findViewById(R.id.tv_teaching_lecturer)
        var endupCourseBtn: Button = itemView.findViewById(R.id.endup_course_btn)
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

        holder.endupCourseBtn.setOnClickListener {
            val apolloClient = ApolloManager.getInstance(context)
            val mutation = RemoveTeachingCourseMutation.builder().courseID(courseId).build()
            val response = try {
                apolloClient.mutate(mutation)
                    .enqueue(object : ApolloCall.Callback<RemoveTeachingCourseMutation.Data>() {
                        override fun onResponse(response: Response<RemoveTeachingCourseMutation.Data>) {
                            Log.i(SUCCESS, response.toString())
                            activity.runOnUiThread {
                                Toast.makeText(
                                    context,
                                    "结束课程成功",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            if (response.hasErrors() || response.data == null) {
                                return
                            }
                            val newList = mutableListOf<List<String>>()
                            for (item in allTeachingCourses) {
                                if (item[0] !== courseId) {
                                    newList.add(item)
                                }
                            }
                            allTeachingCourses = newList
                            activity.runOnUiThread {
                                this@TeachingCourseAdapter.notifyDataSetChanged()
                            }
                        }

                        override fun onFailure(e: ApolloException) {
                            Log.e(FAILURE, e.message, e)
                            activity.runOnUiThread {
                                Toast.makeText(
                                    activity,
                                    e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    })
            } catch (e: ApolloException) {
                Log.e(FAILURE, e.message, e)
                activity.runOnUiThread {
                    Toast.makeText(context, "加载所教课程失败", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApolloNetworkException) {

            }
        }

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