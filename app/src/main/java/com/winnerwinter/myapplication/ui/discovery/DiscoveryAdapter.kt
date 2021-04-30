package com.winnerwinter.myapplication.ui.discovery

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloNetworkException
import com.winnerwinter.SubscribeCourseMutation
import com.winnerwinter.myapplication.ApolloManager
import com.winnerwinter.myapplication.R
import com.winnerwinter.myapplication.ui.dashboard.FAILURE
import com.winnerwinter.myapplication.ui.dashboard.SUCCESS
import kotlinx.android.synthetic.main.discovery_cell.view.*

class DiscoveryAdapter(activity: FragmentActivity, activityContext: Context, itemList: MutableList<List<String>>) : RecyclerView.Adapter<DiscoveryAdapter.DiscoverViewHolder>() {

    val context = activityContext
    private var unscribedCourses = itemList
    val activity = activity


    class DiscoverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val discoveryCourseNameTextView: TextView = itemView.findViewById(R.id.discovery_course_name_tv)
        val discoveryCourseLecturerTextView: TextView = itemView.findViewById(R.id.discovery_course_lecturer_mail_tv)
        val subscribeBtn: Button = itemView.findViewById(R.id.subscribe_btn)
        val discoveryInfoTextView: TextView = itemView.findViewById(R.id.discovery_info_tv)
     }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val itemView: View = layoutInflater.inflate(R.layout.discovery_cell, parent, false)
        return DiscoverViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DiscoverViewHolder, position: Int) {
        val discoveryCourse = unscribedCourses.get(position)
        val courseId = discoveryCourse[0]
        val courseName = discoveryCourse[1]
        val lecturerEmail = discoveryCourse[2]
        val courseInfo = discoveryCourse[3]
        holder.discoveryCourseNameTextView.text = courseName
        holder.discoveryCourseLecturerTextView.text = lecturerEmail
        holder.discoveryInfoTextView.text = courseInfo
        holder.subscribeBtn.setOnClickListener {
            val subscribeCourseMutation: SubscribeCourseMutation = SubscribeCourseMutation.builder()
                .courseID(courseId)
                .build()
            val apolloClient = ApolloManager.getInstance(context)
            val response = try {
                apolloClient.mutate(subscribeCourseMutation)
                    .enqueue(object : ApolloCall.Callback<SubscribeCourseMutation.Data>() {
                        override fun onResponse(response: Response<SubscribeCourseMutation.Data>) {
                            Log.i(SUCCESS, response.toString())
                            activity.runOnUiThread {
                                Toast.makeText(context, "订阅成功", Toast.LENGTH_SHORT).show()
                            }
                            if (response.hasErrors() || response.data == null) {
                                return
                            } else {
                                val newList = mutableListOf<List<String>>()
                                for (item in unscribedCourses) {
                                    if (item[0] !== courseId) {
                                        newList.add(item)
                                    }
                                }
                                unscribedCourses = newList
                                activity.runOnUiThread {
                                    this@DiscoveryAdapter.notifyDataSetChanged()
                                }
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
        return unscribedCourses.size
    }
}