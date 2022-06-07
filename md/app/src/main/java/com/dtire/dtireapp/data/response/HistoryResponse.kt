package com.dtire.dtireapp.data.response

import com.google.gson.annotations.SerializedName

data class HistoryResponse(

	@field:SerializedName("detection_history")
	val detectionHistory: List<DetectionHistoryItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class DetectionHistoryItem(

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: Any? = null,

	@field:SerializedName("condition_title")
	val conditionTitle: String? = null,

	@field:SerializedName("recommendation")
	val recommendation: String? = null,

	@field:SerializedName("detection_id")
	val detectionId: String? = null,

	@field:SerializedName("date_of_check")
	val dateOfCheck: String? = null
)

data class HistorySuccessResponse(
	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)


