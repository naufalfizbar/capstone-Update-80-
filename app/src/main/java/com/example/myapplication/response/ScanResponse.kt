package com.example.myapplication.response

import com.google.gson.annotations.SerializedName

data class ScanResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("probabilities")
	val probabilities: List<List<Any?>?>? = null
)
