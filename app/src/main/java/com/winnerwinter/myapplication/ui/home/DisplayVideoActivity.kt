package com.winnerwinter.myapplication.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.winnerwinter.myapplication.databinding.ActivityDisplayVideoBinding

class DisplayVideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDisplayVideoBinding
    private lateinit var displayVideoViewModel: DisplayVideoViewModel

    private lateinit var videoTitle: String
    private lateinit var videoLocation: String

    override fun onResume() {
        super.onResume()
        displayVideoViewModel.loadVideo(videoTitle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayVideoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        videoTitle = intent.getStringExtra("videoTitle").toString()
        videoLocation = intent.getStringExtra("videoLocation").toString()
        displayVideoViewModel = ViewModelProvider(this).get(DisplayVideoViewModel::class.java).apply {
            progressBarVisibility.observe(this@DisplayVideoActivity, Observer {
                binding.displayVideoProgressBar.visibility = it
            })


            // resize
        }
        binding.displayVideoSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                displayVideoViewModel.mediaPlayer.setDisplay(holder)
                displayVideoViewModel.mediaPlayer.setScreenOnWhilePlaying(true)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }

        })



    }
}