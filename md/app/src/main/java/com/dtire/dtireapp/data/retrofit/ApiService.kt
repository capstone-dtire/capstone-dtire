package com.dtire.dtireapp.data.retrofit

import com.dtire.dtireapp.data.response.*
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
    ): Call<UpdateUserResponse>
}

interface MapsApiService {
    @GET
    suspend fun getNearbyPlaces(
        @Url url: String
    ): Response<MapsResponse>
}