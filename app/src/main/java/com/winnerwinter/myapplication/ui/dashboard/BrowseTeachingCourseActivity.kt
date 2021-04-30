package com.winnerwinter.myapplication.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloNetworkException
import com.winnerwinter.MeQuery
import com.winnerwinter.myapplication.ApolloManager
import com.winnerwinter.myapplication.databinding.ActivityBrowseTeachingCourseBinding
import java.util.*


class BrowseTeachingCourseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBrowseTeachingCourseBinding
    lateinit var view: View
    private lateinit var activityContext: Context
    lateinit var adapter: TeachingCourseAdapter
    val itemList = mutableListOf<List<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrowseTeachingCourseBinding.inflate(layoutInflater)
        view = binding.root

        // 此处可能报错，Debug 请留意
        // activityContext = baseContext
        activityContext = this@BrowseTeachingCourseActivity

        setContentView(view)
        loadTeachingCourses()
        initRecyclerView()
    }
    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(activityContext)
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.teachingCourseRv.layoutManager = layoutManager
        binding.teachingCourseRv.setHasFixedSize(true)
        adapter = TeachingCourseAdapter(activityContext, itemList)
        binding.teachingCourseRv.adapter = adapter
        binding.teachingCourseRv.itemAnimator = DefaultItemAnimator()
    }

    private fun loadTeachingCourses() {
        val apolloClient = ApolloManager.getInstance(this.baseContext)
        lifecycleScope.launchWhenResumed {
            val response = try {
                apolloClient.query(MeQuery())
                    .enqueue(object :
                        ApolloCall.Callback<MeQuery.Data>() {
                        override fun onResponse(response: Response<MeQuery.Data>) {
                            Log.i(SUCCESS, response.toString())
                            runOnUiThread {
                                Toast.makeText(
                                    this@BrowseTeachingCourseActivity,
                                    "加载课程成功",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Toast.makeText(this@BrowseTeachingCourseActivity, response.data?.me()?.teachingCourses().toString(), Toast.LENGTH_SHORT).show()
                            }
                            if (response.hasErrors() || response.data === null) {
                                return
                            } else if (response.data!!.me()?.teachingCourses() == null) {
                                return
                            } else {
                                for (item in response.data?.me()?.teachingCourses()!!) {
                                    val list = mutableListOf<String>()
                                    val courseId = item.courseId()
                                    val courseName = item.coursename()
                                    val lecturer = item.lecturer().useremail()
                                    list.add(courseId)
                                    list.add(courseName)
                                    list.add(lecturer)
                                    itemList.add(list)
                                }
                                this@BrowseTeachingCourseActivity.runOnUiThread {
                                    adapter.notifyDataSetChanged()
                                }
                            }


                        }

                        override fun onFailure(e: ApolloException) {
                            Log.e(FAILURE, e.message, e)
                            runOnUiThread {
                                Toast.makeText(
                                    this@BrowseTeachingCourseActivity,
                                    "加载所教课程失败",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })
            } catch (e: ApolloException) {
                Log.e(FAILURE, "加载所教课程失败")
                runOnUiThread {
                    Toast.makeText(this@BrowseTeachingCourseActivity, "加载所教课程失败", Toast.LENGTH_SHORT).show()
                }
                return@launchWhenResumed
            } catch (e: ApolloNetworkException) {
                Log.e(FAILURE, "网络出了点问题")
                runOnUiThread {
                    Toast.makeText(activityContext, "网络出了点问题", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}