package com.dtire.dtireapp.data.retrofit

import com.dtire.dtireapp.data.response.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<RegisterResponse>

    @GET("user/{id}")
    fun getUser(
        @Path("id") id: String,
    ): Call<UserResponse>

    @FormUrlEncoded
    @PUT("user/{id}")
    fun updateUser(
        @Path("id") id: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("address") address: String,
        @Field("phone") phone: String,
        @Field("url_picture") urlPicture: String,
    ): Call<UpdateUserResponse>

    @Multipart
    @POST("upload-tire")
    fun uploadPhoto(
        @Part file: MultipartBody.Part
    ): Call<UploadPhotoResponse>

    @FormUrlEncoded
    @POST("detection_history")
    fun addToHistory(
        @Field("user_id") id: String,
        @Field("condition_title") conditionTitle: String,
        @Field("recommendation") recommendation: String,
        @Field("image_url") imageUrl: String,
    ): Call<HistorySuccessResponse>

    @GET("detection_history/{id}")
    fun getHistory(
        @Path("id") id: String
    ): Call<HistoryResponse>
}

interface MapsApiService {
    @GET
    suspend fun getNearbyPlaces(
        @Url url: String
    ): Response<MapsResponse>

    @GET
    suspend fun getPlaceDetail(
        @Url url: String
    ): Response<PlaceDetailResponse>


}

interface UploadImageApiService {
    @POST("pred")
    fun uploadImageToCloudFunc(
        @Query("url") url: String
    ): Call<ImageResultResponse>
}