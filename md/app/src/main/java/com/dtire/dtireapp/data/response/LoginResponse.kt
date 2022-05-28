package com.dtire.dtireapp.data.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("user_id")
	val userId: String,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Int
)
