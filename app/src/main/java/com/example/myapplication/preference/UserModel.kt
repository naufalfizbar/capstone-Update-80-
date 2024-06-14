package com.example.myapplication.preference

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false,
    val name: String,
    val userId: String
)
