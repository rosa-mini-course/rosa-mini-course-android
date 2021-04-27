package com.winnerwinter.myapplication.ui.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.winnerwinter.AddTeachingCourseMutation
import com.winnerwinter.myapplication.ApolloManager
import com.winnerwinter.myapplication.R
import com.winnerwinter.myapplication.databinding.ActivityAddTeachingCourseBinding
import java.util.*

const val SUCCESS = "success"
const val FAILURE = "failure"


class AddTeachingCourseActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddTeachingCourseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTeachingCourseBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        binding.addTeachingCourseBtn.setOnClickListener {
            val coursename = binding.coursenameEt.text.toString().trim()
            val courseinfo = binding.courseInfoEt.text.toString().trim()
            if (coursename.equals("")) {
                Toast.makeText(this, "课程名称不能为空", Toast.LENGTH_SHORT).show()
            } else if (courseinfo.equals("")) {
                Toast.makeText(this, "课程描述不能为空", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "正在提交", Toast.LENGTH_SHORT).show()
                // PerformUpload()
                val addTeachingCourseMutation = AddTeachingCourseMutation.builder()
                    .courseName(coursename)
                    .courseInfo(courseinfo)
                    .build()
                val apolloClient = ApolloManager.getInstance(this);
                lifecycleScope.launchWhenResumed {
                    val response = try {
                        apolloClient.mutate(addTeachingCourseMutation)
                            .enqueue(object :
                                ApolloCall.Callback<AddTeachingCourseMutation.Data>() {
                                override fun onResponse(response: Response<AddTeachingCourseMutation.Data>) {
                                    Log.i(SUCCESS, response.toString());
                                    runOnUiThread {
                                        Toast.makeText(
                                            this@AddTeachingCourseActivity,
                                            "上传的课程编号为：" + Objects.requireNonNull(
                                                response.data?.addTeachingCourse()
                                                    ?.courseId()
                                            ),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onFailure(e: ApolloException) {
                                    Log.e(FAILURE, e.message, e);
                                }
                            })
                    } catch (e: Exception) {
                        Log.e(FAILURE, "添加所教课程失败")
                    }
                }
            }
        }
    }

}