package com.winnerwinter.myapplication.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.winnerwinter.myapplication.*
import java.util.*


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var userManager: UserManager
    private lateinit var user: User
    private lateinit var logout_tv: TextView
    private lateinit var useremail_tv:TextView
    private lateinit var userId_tv: TextView
    private lateinit var addTeachingCourse_tv: TextView
    private lateinit var browseTeachingCourse_tv: TextView
    private lateinit var mySubscription_tv: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {

        })

        initData()
        initTextView(root)
        return root
    }

    private fun initData() {
        userManager = UserManager.getInstance(context)
        user = userManager.loadUser()
    }

    private fun initTextView(root: View) {
        useremail_tv = root.findViewById(R.id.tv_useremail)
        useremail_tv.text = user.useremail
        userId_tv = root.findViewById(R.id.tv_userId)
        userId_tv.text = user.userId

        addTeachingCourse_tv = root.findViewById(R.id.tv_addTeachingCourse)
        addTeachingCourse_tv.setOnClickListener {
            val intent = Intent(activity, AddTeachingCourseActivity::class.java)
            startActivity(intent)
        }

        browseTeachingCourse_tv = root.findViewById(R.id.tv_browse_teaching_courses)
        browseTeachingCourse_tv.setOnClickListener {
            val intent = Intent(activity, BrowseTeachingCourseActivity::class.java)
            startActivity(intent)
        }

        logout_tv = root.findViewById(R.id.tv_logout)
        logout_tv.setOnClickListener {
            userManager.saveUser(null)
            MyApplication.shouldLogin = true
            activity?.finish()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            Toast.makeText(activity, "注销成功", Toast.LENGTH_SHORT).show()
        }
    }
}