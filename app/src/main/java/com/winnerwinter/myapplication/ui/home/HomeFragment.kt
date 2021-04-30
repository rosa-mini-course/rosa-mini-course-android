package com.winnerwinter.myapplication.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloNetworkException
import com.winnerwinter.LoadSubscribedCoursesQuery
import com.winnerwinter.myapplication.ApolloManager
import com.winnerwinter.myapplication.databinding.FragmentHomeBinding
import com.winnerwinter.myapplication.ui.dashboard.FAILURE
import com.winnerwinter.myapplication.ui.dashboard.SUCCESS

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    lateinit var adapter: SubscribedCourseAdapter
    private lateinit var activityContext: Context

    var itemList = mutableListOf<List<String>>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.subscribedCourses.observe(viewLifecycleOwner, Observer {
        })
        activityContext = requireContext()
        loadSubscribedCourses()
        initRecyclerView()
        return view
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(activityContext)
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.homeFragmentRv.layoutManager = layoutManager
        binding.homeFragmentRv.setHasFixedSize(true)
        adapter = activity?.let { SubscribedCourseAdapter(it, activityContext, itemList = itemList) }!!
        binding.homeFragmentRv.adapter = adapter
        binding.homeFragmentRv.itemAnimator = DefaultItemAnimator()
    }

    private fun loadSubscribedCourses() {
        val apolloClient = ApolloManager.getInstance(this.context)
        lifecycleScope.launchWhenResumed {
            val response = try {
                apolloClient.query(LoadSubscribedCoursesQuery())
                    .enqueue(object : ApolloCall.Callback<LoadSubscribedCoursesQuery.Data>() {
                        override fun onResponse(response: Response<LoadSubscribedCoursesQuery.Data>) {
                            Log.i(SUCCESS, response.toString())
                            activity?.runOnUiThread {
                                Toast.makeText(activityContext, "加载已订阅课程成功", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            if (response.hasErrors() || response.data == null) {
                                return
                            } else if (response.data!!.me()?.subscribedCourses()  == null) {
                                return
                            } else {
                                for (item in response.data?.me()?.subscribedCourses()!!) {
                                    val list = mutableListOf<String>()
                                    val courseId = item.courseId()
                                    val courseName = item.coursename()
                                    val lecturer = item.lecturer().useremail()
                                    list.add(courseId)
                                    list.add(courseName)
                                    list.add(lecturer)
                                    itemList.add(list)
                                }
                                activity?.runOnUiThread {
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }

                        override fun onFailure(e: ApolloException) {
                            Log.e(FAILURE, e.message, e)
                            activity?.runOnUiThread {
                                Toast.makeText(activityContext, "加载已订阅课程失败", Toast.LENGTH_SHORT).show()
                            }
                        }

                    })
            } catch (e: ApolloException) {
                Log.e(FAILURE, "加载已订阅课程失败")
                activity?.runOnUiThread {
                    Toast.makeText(activityContext, "加载已订阅课程失败", Toast.LENGTH_SHORT).show()
                }
                return@launchWhenResumed
            } catch (e: ApolloNetworkException) {
                Log.e(FAILURE, "网络出了点问题")
                activity?.runOnUiThread {
                    Toast.makeText(activityContext, "网络连接出了点问题", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}