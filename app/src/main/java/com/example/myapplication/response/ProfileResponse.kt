package com.example.myapplication.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("profile_picture")
	val profilePicture: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)

