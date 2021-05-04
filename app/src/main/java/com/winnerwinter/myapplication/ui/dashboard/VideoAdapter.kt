package com.winnerwinter.myapplication.ui.dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloNetworkException
import com.winnerwinter.RemoveVideoMutation
import com.winnerwinter.myapplication.ApolloManager
import com.winnerwinter.myapplication.R

class VideoAdapter(activity: TeachingCourseDetailsActivity, itemList: MutableList<List<String>>) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {
    val activity = activity
    private var videos = itemList

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoNameTextView: TextView = itemView.findViewById(R.id.cell_video_name)
        val removeVideoBtn: Button = itemView.findViewById(R.id.remove_video_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val itemView: View = layoutInflater.inflate(R.layout.video_cell, parent, false)
        return VideoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos.get(position)
        val videoId = video[0]
        val videoTitle = video[1]
        holder.videoNameTextView.text = videoTitle
        holder.removeVideoBtn.setOnClickListener {
            val apolloClient = ApolloManager.getInstance(activity)
            val mutation = RemoveVideoMutation.builder().videoID(videoId).build()
            try {
                apolloClient.mutate(mutation)
                    .enqueue(object : ApolloCall.Callback<RemoveVideoMutation.Data>() {
                        override fun onResponse(response: Response<RemoveVideoMutation.Data>) {
                            Log.i(SUCCESS, response.toString())
                            activity.runOnUiThread {
                                Toast.makeText(activity, "删除资源文件成功", Toast.LENGTH_SHORT).show()
                            }
                            if (response.hasErrors() || response.data == null) {
                                return
                            }
                            val newList = mutableListOf<List<String>>()
                            for (item in videos) {
                                if (item[0] != videoId) {
                                    newList.add(item)
                                }
                            }
                            videos = newList
                            activity.runOnUiThread {
                                notifyDataSetChanged()
                            }
                        }

                        override fun onFailure(e: ApolloException) {
                            Log.e(FAILURE, e.message, e)
                            activity.runOnUiThread {
                                Toast.makeText(
                                    activity,
                                    e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    })
            } catch (e: ApolloNetworkException) {
                Log.e(FAILURE, e.message, e)
                activity.runOnUiThread {
                    Toast.makeText(
                        activity,
                        e.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return videos.size
    }
}