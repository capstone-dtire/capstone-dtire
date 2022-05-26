package com.dtire.dtireapp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dtire.dtireapp.data.response.LoginResponse
import com.dtire.dtireapp.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel: ViewModel() {
    private val _user = MutableLiveData<LoginResponse>()
    private val user: LiveData<LoginResponse> = _user

    private val _isLoading = MutableLiveData<Boolean>()
    var isLoading: LiveData<Boolean> = _isLoading

    fun loginUser(email: String, password: String): LiveData<LoginResponse> {
        _isLoading.value = true
        ApiConfig.getApiService().loginUser(email, password)
            .enqueue(object: Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        _user.value = responseBody!!
                        _isLoading.value = false
                    } else {
                        Log.d("TAG", "onResponse: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.d("TAG", "onResponse: ${t.message}")
                    _isLoading.value = false
                }

            })
        return user
    }

}