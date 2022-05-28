package com.dtire.dtireapp.data.response

import com.google.gson.annotations.SerializedName

data class UserResponse(

	@field:SerializedName("user")
	val user: User,

	@field:SerializedName("status")
	val status: Int
)

data class User(

	@field:SerializedName("user_id")
	val userId: String,

	@field:SerializedName("name")
	val name: String
)
