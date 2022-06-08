package com.dtire.dtireapp.data.response

import com.google.gson.annotations.SerializedName

data class HistoryResponse(

	@field:SerializedName("detection_history")
	val detectionHistory: List<DetectionHistoryItem>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Int
)

data class DetectionHistoryItem(

	@field:SerializedName("user_id")
	val userId: String,

	@field:SerializedName("image_url")
	val imageUrl: Any,

	@field:SerializedName("condition_title")
	val conditionTitle: String,

	@field:SerializedName("recommendation")
	val recommendation: String,

	@field:SerializedName("detection_id")
	val detectionId: String,

	@field:SerializedName("date_of_check")
	val dateOfCheck: String
)

data class HistorySuccessResponse(
	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Int
)


