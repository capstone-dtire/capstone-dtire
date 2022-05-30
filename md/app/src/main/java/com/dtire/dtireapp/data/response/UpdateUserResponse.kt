package com.dtire.dtireapp.data.response

import com.google.gson.annotations.SerializedName

data class UpdateUserResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Int
)
