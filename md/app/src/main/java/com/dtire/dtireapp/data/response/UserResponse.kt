package com.dtire.dtireapp.data.response

import com.google.gson.annotations.SerializedName

data class UserResponse(

	@field:SerializedName("user")
	val user: List<UserItem>,

	@field:SerializedName("status")
	val status: Int
)

data class UserItem(

	@field:SerializedName("password")
	val password: String,

	@field:SerializedName("address")
	val address: Any,

	@field:SerializedName("user_id")
	val userId: String,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("url_picture")
	val urlPicture: String? = null,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("email")
	val email: String
)
