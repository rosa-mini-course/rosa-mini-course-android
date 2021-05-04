package com.winnerwinter.myapplication.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.winnerwinter.myapplication.R

class SubscribedVideoApater(activity: SubscribedCourseDetailActivity, itemList: MutableList<List<String>>): RecyclerView.Adapter<SubscribedVideoApater.SubscribedVideoViewHolder>() {

    private val videos = itemList
    private val activity = activity

    class SubscribedVideoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val videoNameTextView: TextView = itemView.findViewById(R.id.sb_cell_video_name)
        val displayVideoBtn: Button = itemView.findViewById(R.id.sb_display_video_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscribedVideoViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val itemView: View = layoutInflater.inflate(R.layout.subscribed_video_cell, parent, false)
        return SubscribedVideoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SubscribedVideoViewHolder, position: Int) {
        val video = videos.get(position)
        val videoId = video[0]
        val videoTitle = video[1]
        val videoLocation = video[2]
        holder.videoNameTextView.text = videoTitle
        holder.displayVideoBtn.setOnClickListener {
            val intent = Intent(activity, DisplayVideoActivity::class.java)
            intent.putExtra("videoTitle", videoTitle)
            intent.putExtra("videoLocation", videoLocation)
            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return videos.size
    }
}