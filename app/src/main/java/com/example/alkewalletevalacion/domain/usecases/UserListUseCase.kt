package com.example.alkewalletevalacion.domain.usecases

import android.content.SharedPreferences
import android.util.Log
import com.example.alkewalletevalacion.data.network.api.AuthService
import com.example.alkewalletevalacion.data.network.response.UserListResponse
import com.example.alkewalletevalacion.data.network.response.UserListWrapper
import com.example.alkewalletevalacion.data.network.response.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserListUseCase(private val authService: AuthService, private val sharedPreferences: SharedPreferences) {

    private val TAG = "UserListUseCase"

    fun getUsers(onResult: (Boolean, List<UserListResponse>?) -> Unit) {
        authService.getUsers().enqueue(object : Callback<UserListWrapper> {
            override fun onResponse(call: Call<UserListWrapper>, response: Response<UserListWrapper>) {
                if (response.isSuccessful) {
                    val userList = response.body()?.data

                    if (userList == null) {

                    } else {
                        userList.forEach { user ->
                            Log.d(TAG, "User: $user")
                        }
                    }
                    onResult(true, userList)
                } else {

                    onResult(false, null)
                }
            }

            override fun onFailure(call: Call<UserListWrapper>, t: Throwable) {

                onResult(false, null)
            }
        })
    }
    suspend fun getUserById(userId: Int): UserResponse {
        return authService.getUserById(userId)
    }
    fun getLoggedInUserId(): Int {
        return sharedPreferences.getInt("userId", -1)
    }

}