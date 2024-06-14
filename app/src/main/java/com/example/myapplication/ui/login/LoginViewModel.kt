package com.example.myapplication.ui.login

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.preference.UserModel
import com.example.myapplication.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            // Pastikan token tidak null sebelum disimpan
            if (!user.token.isNullOrEmpty()) {
                repository.saveSession(user)
            } else {
                Log.e(TAG, "Trying to save session with null or empty token")
            }
        }
    }
}