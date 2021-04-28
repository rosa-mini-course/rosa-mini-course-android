package com.winnerwinter.myapplication.ui.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.winnerwinter.myapplication.databinding.ActivityTeachingCourseDetailsBinding

class TeachingCourseDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityTeachingCourseDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeachingCourseDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.courseIdTv.text = intent.getStringExtra("courseId")
        binding.courseNameTv.text = intent.getStringExtra("courseName")
        binding.courseLecturerEmail.text = intent.getStringExtra("lecturerEmail")
    }
}