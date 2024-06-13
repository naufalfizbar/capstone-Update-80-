package com.example.myapplication.ui.add_image

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.myapplication.preference.UserModel
import com.example.myapplication.repository.UserRepository

class ScanViewModel (private val repository: UserRepository) : ViewModel(){
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}