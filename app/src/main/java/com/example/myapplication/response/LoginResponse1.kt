package com.example.myapplication.response

import com.google.gson.annotations.SerializedName

data class LoginResponse1(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("token")
	val token: String? = null
)
