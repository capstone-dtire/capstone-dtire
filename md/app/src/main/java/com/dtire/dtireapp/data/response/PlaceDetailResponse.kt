package com.dtire.dtireapp.data.response

import com.google.gson.annotations.SerializedName

data class PlaceDetailResponse(

	@field:SerializedName("result")
	val result: Result? = null,

	@field:SerializedName("html_attributions")
	val htmlAttributions: List<Any?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class ReviewsItem(

	@field:SerializedName("author_name")
	val authorName: String? = null,

	@field:SerializedName("profile_photo_url")
	val profilePhotoUrl: String? = null,

	@field:SerializedName("author_url")
	val authorUrl: String? = null,

	@field:SerializedName("rating")
	val rating: Int? = null,

	@field:SerializedName("language")
	val language: String? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("time")
	val time: Int? = null,

	@field:SerializedName("relative_time_description")
	val relativeTimeDescription: String? = null
)

data class Open(

	@field:SerializedName("time")
	val time: String? = null,

	@field:SerializedName("day")
	val day: Int? = null
)

data class PeriodsItem(

	@field:SerializedName("close")
	val close: Close? = null,

	@field:SerializedName("open")
	val open: Open? = null
)

data class AddressComponentsItem(

	@field:SerializedName("types")
	val types: List<String?>? = null,

	@field:SerializedName("short_name")
	val shortName: String? = null,

	@field:SerializedName("long_name")
	val longName: String? = null
)

data class PhotosItemDetail(

	@field:SerializedName("photo_reference")
	val photoReference: String? = null,

	@field:SerializedName("width")
	val width: Int? = null,

	@field:SerializedName("html_attributions")
	val htmlAttributions: List<String?>? = null,

	@field:SerializedName("height")
	val height: Int? = null
)

data class OpeningHoursDetail(

	@field:SerializedName("open_now")
	val openNow: Boolean? = null,

	@field:SerializedName("periods")
	val periods: List<PeriodsItem?>? = null,

	@field:SerializedName("weekday_text")
	val weekdayText: List<String?>? = null
)

data class PlusCodeDetail(

	@field:SerializedName("compound_code")
	val compoundCode: String? = null,

	@field:SerializedName("global_code")
	val globalCode: String? = null
)

data class GeometryDetail(

	@field:SerializedName("location")
	val location: LocationDetail? = null
)

data class Close(

	@field:SerializedName("time")
	val time: String? = null,

	@field:SerializedName("day")
	val day: Int? = null
)

data class Result(

	@field:SerializedName("utc_offset")
	val utcOffset: Int? = null,

	@field:SerializedName("formatted_address")
	val formattedAddress: String? = null,

	@field:SerializedName("types")
	val types: List<String?>? = null,

	@field:SerializedName("business_status")
	val businessStatus: String? = null,

	@field:SerializedName("icon")
	val icon: String? = null,

	@field:SerializedName("rating")
	val rating: Double? = null,

	@field:SerializedName("icon_background_color")
	val iconBackgroundColor: String? = null,

	@field:SerializedName("address_components")
	val addressComponents: List<AddressComponentsItem?>? = null,

	@field:SerializedName("photos")
	val photos: List<PhotosItemDetail?>? = null,

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("reference")
	val reference: String? = null,

	@field:SerializedName("user_ratings_total")
	val userRatingsTotal: Int? = null,

	@field:SerializedName("reviews")
	val reviews: List<ReviewsItem?>? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("opening_hours")
	val openingHours: OpeningHoursDetail? = null,

	@field:SerializedName("geometry")
	val geometry: GeometryDetail? = null,

	@field:SerializedName("icon_mask_base_uri")
	val iconMaskBaseUri: String? = null,

	@field:SerializedName("vicinity")
	val vicinity: String? = null,

	@field:SerializedName("adr_address")
	val adrAddress: String? = null,

	@field:SerializedName("plus_code")
	val plusCode: PlusCodeDetail? = null,

	@field:SerializedName("formatted_phone_number")
	val formattedPhoneNumber: String? = null,

	@field:SerializedName("international_phone_number")
	val internationalPhoneNumber: String? = null,

	@field:SerializedName("place_id")
	val placeId: String? = null
)

data class LocationDetail(

	@field:SerializedName("lng")
	val lng: Double? = null,

	@field:SerializedName("lat")
	val lat: Double? = null
)
