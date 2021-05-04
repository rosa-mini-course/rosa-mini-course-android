package com.winnerwinter.myapplication.ui.dashboard

import android.content.Intent
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
import com.winnerwinter.myapplication.databinding.ActivityTeachingCourseDetailsBinding

class TeachingCourseDetailsActivity : AppCompatActivity() {
    lateinit var adapter: VideoAdapter
    lateinit var binding: ActivityTeachingCourseDetailsBinding
    val itemList = mutableListOf<List<String>>()
    lateinit var courseId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeachingCourseDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.courseIdTv.text = intent.getStringExtra("courseId")
        binding.courseNameTv.text = intent.getStringExtra("courseName")
        binding.courseLecturerEmail.text = intent.getStringExtra("lecturerEmail")
        courseId = intent.getStringExtra("courseId").toString()
        loadVideosBelongToCourse()
        initRecyclerView()
        initFloatingButton()
    }

    private fun initFloatingButton() {
        binding.floatingActionButton.setOnClickListener {
            val intent: Intent = Intent(this@TeachingCourseDetailsActivity, UploadVideoActivity::class.java)
            intent.putExtra("courseId", courseId)
            startActivity(intent)
        }
    }

    private fun initRecyclerView() {
        val layoutManager: LinearLayoutManager = LinearLayoutManager(this@TeachingCourseDetailsActivity)
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.setHasFixedSize(true)
        adapter = VideoAdapter(this@TeachingCourseDetailsActivity, itemList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
    }

    private fun loadVideosBelongToCourse() {
        val apolloClient = ApolloManager.getInstance(this@TeachingCourseDetailsActivity)
        val query = LoadVideosBelongToCourseQuery.builder().courseID(courseId).build()
        lifecycleScope.launchWhenResumed {
            try {
                apolloClient.query(query)
                    .enqueue(object: ApolloCall.Callback<LoadVideosBelongToCourseQuery.Data>() {
                        override fun onResponse(response: Response<LoadVideosBelongToCourseQuery.Data>) {
                            Log.i(SUCCESS, response.toString())
                            runOnUiThread {
                                Toast.makeText(this@TeachingCourseDetailsActivity, "发现课程成功", Toast.LENGTH_SHORT).show()
                            }
                            if (response.hasErrors() || response.data == null) {
                                return
                            }
                            for (item in response.data!!.loadVideos()) {
                                val list = mutableListOf<String>()
                                val videoId = item.videoId()
                                val videoEntityName = item.title()
                                list.add(videoId)
                                list.add(videoEntityName)
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
                                    this@TeachingCourseDetailsActivity,
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
                        this@TeachingCourseDetailsActivity,
                                e.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: ApolloNetworkException) {
                Log.e(FAILURE, e.message, e)
                runOnUiThread {
                    Toast.makeText(
                        this@TeachingCourseDetailsActivity,
                        e.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}