package com.winnerwinter.myapplication.ui.home

import android.media.MediaPlayer
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DisplayVideoViewModel() : ViewModel() {
    val mediaPlayer = MediaPlayer()
    private val _progressBarVisibility = MutableLiveData(View.VISIBLE)
    val progressBarVisibility: LiveData<Int> = _progressBarVisibility
    private val _videoResolution = MutableLiveData(Pair(0, 0))
    val videoResolution  :LiveData<Pair<Int, Int>> = _videoResolution

//    init {
//        loadVideo("my.mp4")
//    }

    fun loadVideo(videoName: String) {
        mediaPlayer.apply {
            reset()
            _progressBarVisibility.value = View.VISIBLE
            setDataSource("http://192.168.1.138:4000/download/" + videoName)
            setOnPreparedListener {
                _progressBarVisibility.value = View.INVISIBLE
                isLooping = true
                it.start()
            }
            setOnVideoSizeChangedListener { mp, width, height ->
                _videoResolution.value = Pair(width, height)
            }
            prepareAsync()
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }
}