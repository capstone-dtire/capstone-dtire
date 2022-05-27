package com.dtire.dtireapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dtire.dtireapp.data.response.LoginResponse
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
}