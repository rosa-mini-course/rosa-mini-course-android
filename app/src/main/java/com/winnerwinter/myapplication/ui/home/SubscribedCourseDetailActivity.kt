package com.winnerwinter.myapplication.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloNetworkException
import com.winnerwinter.LoadVideosBelongToCourseQuery
import com.winnerwinter.myapplication.ApolloManager
import com.winnerwinter.myapplication.databinding.ActivitySubscribedCourseDetailBinding
import com.winnerwinter.myapplication.ui.dashboard.FAILURE
import com.winnerwinter.myapplication.ui.dashboard.SUCCESS

class SubscribedCourseDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubscribedCourseDetailBinding
    private val itemList = mutableListOf<List<String>>()

    private lateinit var adapter: SubscribedVideoApater
    private lateinit var courseId: String
    private lateinit var courseName: String
    private lateinit var lecturerEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscribedCourseDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        courseId = intent.getStringExtra("courseId").toString()
        courseName = intent.getStringExtra("courseName").toString()
        lecturerEmail = intent.getStringExtra("lecturerEmail").toString()
        binding.sbCourseIdTv.text = courseId
        binding.sbCourseNameTv.text = courseName
        binding.sbCourseLecturerEmail.text = lecturerEmail
        loadVideoBelongToCourse()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this@SubscribedCourseDetailActivity)
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.sbRecyclerView.layoutManager = layoutManager
        binding.sbRecyclerView.setHasFixedSize(true)
        adapter = SubscribedVideoApater(this@SubscribedCourseDetailActivity, itemList)
        binding.sbRecyclerView.adapter =adapter
        binding.sbRecyclerView.itemAnimator = DefaultItemAnimator()
    }

    private fun loadVideoBelongToCourse() {
        val apolloClient = ApolloManager.getInstance(this@SubscribedCourseDetailActivity)
        val query = LoadVideosBelongToCourseQuery.builder().courseID(courseId).build()
        lifecycleScope.launchWhenResumed {
            try {
                apolloClient.query(query)
                    .enqueue(object: ApolloCall.Callback<LoadVideosBelongToCourseQuery.Data>() {
                        override fun onResponse(response: Response<LoadVideosBelongToCourseQuery.Data>) {
                            Log.i(SUCCESS, response.toString())
                            runOnUiThread {
                                Toast.makeText(this@SubscribedCourseDetailActivity, "加载详情成功", Toast.LENGTH_SHORT).show()
                            }
                            if (response.hasErrors() || response.data == null) {
                                return
                            }
                            for (item in response.data!!.loadVideos()) {
                                val list = mutableListOf<String>()
                                val videoId = item.videoId()
                                val videoEntityName = item.title()
                                val videoLocation = item.location()
                                list.add(videoId)
                                list.add(videoEntityName)
                                list.add(videoLocation)
                                itemList.add(list)
                            }
                            runOnUiThread {
                                adapter.notifyDataSetChanged()
                            }
                        }

                        override fun onFailure(e: ApolloException) {
                            Log.e(FAILURE, e.message, e)
                            runOnUiThread {
                                Toast.makeText(
                                    this@SubscribedCourseDetailActivity,
                                    e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    })
            } catch (e: ApolloException) {
                Log.e(FAILURE, e.message, e)
                runOnUiThread {
                    Toast.makeText(
                        this@SubscribedCourseDetailActivity,
                        e.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: ApolloNetworkException) {
                Log.e(FAILURE, e.message, e)
                runOnUiThread {
                    Toast.makeText(
                        this@SubscribedCourseDetailActivity,
                        e.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}