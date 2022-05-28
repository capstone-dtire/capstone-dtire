package com.dtire.dtireapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dtire.dtireapp.data.response.LoginResponse
import com.dtire.dtireapp.data.response.RegisterResponse
import com.dtire.dtireapp.data.response.UserResponse
import com.dtire.dtireapp.data.retrofit.ApiConfig
import com.dtire.dtireapp.data.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository {
    private val retrofit: ApiService = ApiConfig.getApiService()

    fun loginUser(email: String, password: String): LiveData<State<LoginResponse>> {
        val loginUser = MutableLiveData<State<LoginResponse>>()

        loginUser.postValue(State.Loading())
        retrofit.loginUser(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user?.status == 200) {
                        loginUser.postValue(State.Success(user))
                    } else {
                        loginUser.postValue(State.Error(user?.message))
                    }
                } else {
                    loginUser.postValue(State.Error(response.body()?.message))
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginUser.postValue(State.Error(t.message))
            }
        })
        return loginUser
    }

    fun registerUser(name: String, email: String, password: String): LiveData<State<RegisterResponse>> {
        val registeredUser = MutableLiveData<State<RegisterResponse>>()

        registeredUser.postValue(State.Loading())
        retrofit.registerUser(name, email, password).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                val user = response.body()
                if (user == null) {
                    registeredUser.postValue(State.Error("Email already used"))
                } else {
                    registeredUser.postValue((State.Success(user)))
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                registeredUser.postValue(State.Error(t.message))
            }
        })
        return registeredUser
    }

    fun getUser(id: String): LiveData<State<UserResponse>> {
        val userData = MutableLiveData<State<UserResponse>>()

        userData.postValue(State.Loading())
        retrofit.getUser(id).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val userResponse = response.body()
                if (userResponse != null) {
                    userData.postValue(State.Success(userResponse))
                } else {
                    userData.postValue(State.Error("Get user failed"))
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                userData.postValue(State.Error(t.message))
            }
        })
        return userData
    }
}