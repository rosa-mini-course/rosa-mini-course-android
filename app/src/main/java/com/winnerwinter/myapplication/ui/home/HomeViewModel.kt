package com.winnerwinter.myapplication.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.winnerwinter.myapplication.ApolloManager

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private var _subscribedCourses: MutableLiveData<MutableList<MutableList<String>>> = MutableLiveData<MutableList<MutableList<String>>>()
    public var subscribedCourses: MutableLiveData<MutableList<MutableList<String>>>
        get() = _subscribedCourses
        set(value) {
            _subscribedCourses = value
        }
}