package com.dtire.dtireapp.data.response

import com.google.gson.annotations.SerializedName

data class UploadPhotoResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("url")
	val url: String
)

data class ImageResultResponse(

	@field:SerializedName("deployedModelId")
	val deployedModelId: String,

	@field:SerializedName("model")
	val model: String,

	@field:SerializedName("modelDisplayName")
	val modelDisplayName: String,

	@field:SerializedName("predictions")
	val predictions: List<List<Double>>
)
