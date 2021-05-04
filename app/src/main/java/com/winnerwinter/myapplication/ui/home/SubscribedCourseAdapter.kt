package com.winnerwinter.myapplication.ui.home

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloNetworkException
import com.winnerwinter.UnsubscribeCourseMutation
import com.winnerwinter.myapplication.ApolloManager
import com.winnerwinter.myapplication.R
import com.winnerwinter.myapplication.ui.dashboard.FAILURE
import com.winnerwinter.myapplication.ui.dashboard.SUCCESS

class SubscribedCourseAdapter(activity: FragmentActivity, activityContext: Context, itemList: MutableList<List<String>>) : RecyclerView.Adapter<SubscribedCourseAdapter.SubscribedCourseViewHolder>() {

    val context = activityContext
    var allSubscribedCourses= itemList
    val activity = activity

    class SubscribedCourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var subscribedCourseName: TextView = itemView.findViewById(R.id.tv_subscribed_course_name)
        var subscribedCourseLecturer: TextView = itemView.findViewById(R.id.tv_subscribed_course_lecturer)
        val unsubscribeBtn: Button = itemView.findViewById(R.id.home_unsubscribe_btn)
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

        // 点击单个课程跳转到该课程的详情页
        holder.itemView.setOnClickListener {
            val intent = Intent(context, SubscribedCourseDetailActivity::class.java)
            intent.putExtra("courseId", courseId)
            intent.putExtra("courseName", courseName)
            intent.putExtra("lecturerEmail", lecturerEmail)
            activity.startActivity(intent)
        }

        // 点击单个课程跳转到该课程的详情页
        holder.subscribedCourseName.setOnClickListener {
            val intent = Intent(context, SubscribedCourseDetailActivity::class.java)
            intent.putExtra("courseId", courseId)
            intent.putExtra("courseName", courseName)
            intent.putExtra("lecturerEmail", lecturerEmail)
            activity.startActivity(intent)
        }

        holder.unsubscribeBtn.setOnClickListener {
            val apolloClient = ApolloManager.getInstance(context)
            val unsubscribeCourseMutation = UnsubscribeCourseMutation.builder().courseID(courseId).build()
            val response = try {
                apolloClient.mutate(unsubscribeCourseMutation)
                    .enqueue(object : ApolloCall.Callback<UnsubscribeCourseMutation.Data>() {
                        override fun onResponse(response: Response<UnsubscribeCourseMutation.Data>) {
                            Log.i(SUCCESS, response.toString())
                            activity.runOnUiThread {
                                Toast.makeText(context, "退订成功", Toast.LENGTH_SHORT).show()
                            }
                            if (response.hasErrors() || response.data == null) {
                                return
                            }
                            val newList = mutableListOf<List<String>>()
                            for (item in allSubscribedCourses) {
                                if (item[0] != courseId) {
                                    newList.add(item)
                                }
                            }
                            allSubscribedCourses = newList
                            activity.runOnUiThread {
                                this@SubscribedCourseAdapter.notifyDataSetChanged()
                            }
                        }

                        override fun onNetworkError(e: ApolloNetworkException) {
                            super.onNetworkError(e)
                            Log.e(FAILURE, e.message, e)
                            activity.runOnUiThread {
                                Toast.makeText(context, "网络出了点问题", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(e: ApolloException) {
                            Log.e(FAILURE, e.message, e)
                            activity.runOnUiThread {
                                Toast.makeText(context, "服务器繁忙", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
            } catch (e: ApolloNetworkException) {
                Log.e(FAILURE, e.message, e)
                activity.runOnUiThread {
                    Toast.makeText(context, "网络出了点问题", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApolloException) {
                Log.e(FAILURE, e.message, e)
                activity.runOnUiThread {
                    Toast.makeText(context, "服务器繁忙", Toast.LENGTH_SHORT).show()
                }
            }


        }

    }

    override fun getItemCount(): Int {
        return allSubscribedCourses.size
    }
}