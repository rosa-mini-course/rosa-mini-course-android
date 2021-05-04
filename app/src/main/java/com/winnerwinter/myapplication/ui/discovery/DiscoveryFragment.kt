package com.winnerwinter.myapplication.ui.discovery

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
import com.winnerwinter.DiscoveryCoursesQuery
import com.winnerwinter.myapplication.ApolloManager
import com.winnerwinter.myapplication.databinding.FragmentDiscoveryBinding
import com.winnerwinter.myapplication.ui.dashboard.FAILURE
import com.winnerwinter.myapplication.ui.dashboard.SUCCESS

class DiscoveryFragment : Fragment() {

    private lateinit var discoveryViewModel: DiscoveryViewModel

    private var _binding: FragmentDiscoveryBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: DiscoveryAdapter
    private lateinit var activityContext: Context
    val itemList = mutableListOf<List<String>>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        discoveryViewModel = ViewModelProvider(this).get(DiscoveryViewModel::class.java)
        _binding = FragmentDiscoveryBinding.inflate(inflater, container, false)
        val view = binding.root
        discoveryViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        // 这里可能出错， Debug 请注意
        activityContext = activity?.baseContext!!
        loadUnscribedCourses()
        initRecyclerView()
        return view
    }
    
    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(activityContext)
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.discoveryRv.layoutManager = layoutManager
        binding.discoveryRv.setHasFixedSize(true)
        adapter = activity?.let { DiscoveryAdapter(it, activityContext, itemList) }!!
        binding.discoveryRv.adapter = adapter
        binding.discoveryRv.itemAnimator = DefaultItemAnimator()
    }
    
    private fun loadUnscribedCourses() {
        val apolloClient = ApolloManager.getInstance(activityContext)
        lifecycleScope.launchWhenResumed {
            val response = try {
                apolloClient.query(DiscoveryCoursesQuery())
                    .enqueue(object : ApolloCall.Callback<DiscoveryCoursesQuery.Data>() {
                        override fun onResponse(response: Response<DiscoveryCoursesQuery.Data>) {
                            Log.i(SUCCESS, response.toString())
                            activity?.runOnUiThread {
                                Toast.makeText(activityContext, "发现课程成功", Toast.LENGTH_SHORT).show()
                            }
                            if (response.hasErrors() || response.data == null) {
                                return
                            } else {
                                for (item in response.data!!.discoveryCourses()) {
                                    val list = mutableListOf<String>()
                                    val courseId = item.courseId()
                                    val courseName = item.coursename()
                                    val lecturer = item.lecturer().useremail()
                                    val courseInfo = item.info()
                                    list.add(courseId)
                                    list.add(courseName)
                                    list.add(lecturer)
                                    list.add(courseInfo)
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
                                Toast.makeText(
                                    activityContext,
                                    e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    })
            } catch (e: ApolloNetworkException) {
                Log.e(FAILURE, e.message, e)
                activity?.runOnUiThread {
                    Toast.makeText(
                        activityContext,
                        e.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}