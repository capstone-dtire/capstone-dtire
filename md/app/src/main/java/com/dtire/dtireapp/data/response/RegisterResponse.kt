package com.dtire.dtireapp.data.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("error")
	val error: Error,

	@field:SerializedName("status")
	val status: Int
)

data class Error(

	@field:SerializedName("severity")
	val severity: String,

	@field:SerializedName("schema")
	val schema: String,

	@field:SerializedName("code")
	val code: String,

	@field:SerializedName("file")
	val file: String,

	@field:SerializedName("routine")
	val routine: String,

	@field:SerializedName("line")
	val line: String,

	@field:SerializedName("length")
	val length: Int,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("constraint")
	val constraint: String,

	@field:SerializedName("detail")
	val detail: String,

	@field:SerializedName("table")
	val table: String
)
