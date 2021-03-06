package com.dtire.dtireapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dtire.dtireapp.data.response.*
import com.dtire.dtireapp.data.retrofit.ApiConfig
import com.dtire.dtireapp.data.retrofit.ApiService
import com.dtire.dtireapp.data.retrofit.MapsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository {
    private val retrofit: ApiService = ApiConfig.getApiService()
    private val mapsRetrofit: MapsApiService = ApiConfig.getMapsApiService()

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

    fun getUser(id: String): LiveData<State<UserItem>> {
        val userData = MutableLiveData<State<UserItem>>()

        userData.postValue(State.Loading())
        retrofit.getUser(id).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val userResponse = response.body()?.user?.get(0)
                if (userResponse != null) {
                    userData.postValue(State.Success(userResponse))
                } else {
                    userData.postValue(State.Error("Get user failed"))
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                userData.postValue(State.Error(t.message))
                Log.d("TAG", "onFailure: ${t.message}")
            }
        })
        return userData
    }

    fun updateUser(id: String, userData: UserItem): LiveData<State<String>> {
        val updateStatus = MutableLiveData<State<String>>()

        updateStatus.postValue(State.Loading())
        retrofit.updateUser(id, userData.name, userData.email, userData.address ?: "", userData.phone ?: "", userData.urlPicture ?: "")
            .enqueue(object : Callback<UpdateUserResponse> {
                override fun onResponse(
                    call: Call<UpdateUserResponse>,
                    response: Response<UpdateUserResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            updateStatus.postValue(State.Error(responseBody.message))
                        } else {
                            updateStatus.postValue(State.Success(responseBody?.message))
                        }
                    } else {
                        updateStatus.postValue(State.Error(response.message()))
                    }


                }

                override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                    updateStatus.postValue(State.Error(t.message))
                }
            })
        return updateStatus
    }

    fun getNearbyPlaces(url: String): Flow<State<Any>> = flow<State<Any>> {
        emit(State.Loading())

        val response = mapsRetrofit.getNearbyPlaces(url)
        if (response.body()?.results?.size!! > 0) {
            emit(State.Success(response.body()!!))
        } else {
            emit(State.Error(response.message()))
        }
    }.catch {
        emit(State.Error(it.message))
    }.flowOn(Dispatchers.IO)

    fun getPlaceDetail(url: String): Flow<State<Any>> = flow<State<Any>> {
        emit(State.Loading())

        val response = mapsRetrofit.getPlaceDetail(url)
        if (response.isSuccessful) {
            response.body()?.let { emit(State.Success(it)) }
            Log.d("TAG", "getPlaceDetail: berhasil: ${response.body()}")
        } else {
            emit(State.Error(response.message()))
            Log.d("TAG", "getPlaceDetail: gagal1: ${response.message()}")
        }
    }.catch {
        emit(State.Error(it.message))
        Log.d("TAG", "getPlaceDetail: gagal2: ${it.message!!}")
    }.flowOn(Dispatchers.IO)



    fun addToHistory(id: String,
                     condition: String,
                     recommendation: String,
                     imageUrl: String): LiveData<State<HistorySuccessResponse>> {
        val history = MutableLiveData<State<HistorySuccessResponse>>()

        retrofit.addToHistory(id, condition, recommendation, imageUrl)
            .enqueue(object : Callback<HistorySuccessResponse> {
                override fun onResponse(
                    call: Call<HistorySuccessResponse>,
                    response: Response<HistorySuccessResponse>
                ) {
                    if (response.isSuccessful) {
                        history.postValue(State.Success(response.body()))
                        Log.d("TAG", "onResponse: ${response.body()}")
                    } else {
                        history.postValue(State.Error(response.message()))
                        Log.d("TAG", "onResponseFailed1: ${response.body()}")
                    }
                }

                override fun onFailure(call: Call<HistorySuccessResponse>, t: Throwable) {
                    history.postValue(State.Error(t.message))
                    Log.d("TAG", "onFailed1: ${t.message}")
                }

            })
        return history
    }

    fun getHistory(id: String): LiveData<State<HistoryResponse>> {
        val history = MutableLiveData<State<HistoryResponse>>()

        history.postValue(State.Loading())
        retrofit.getHistory(id).enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(
                call: Call<HistoryResponse>,
                response: Response<HistoryResponse>
            ) {
                if (response.isSuccessful) {
                    history.postValue(State.Success(response.body()))
                } else {
                    history.postValue(State.Error(response.body()?.message))
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                history.postValue(State.Error(t.message))
            }

        })
        return history
    }
}