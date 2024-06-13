package com.example.myapplication.retrofit

import android.content.Context
import com.example.myapplication.repository.UserRepository
import com.example.test.data.pref.UserPreference
import com.example.test.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}