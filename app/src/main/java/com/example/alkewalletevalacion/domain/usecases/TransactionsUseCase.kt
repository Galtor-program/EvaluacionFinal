package com.example.alkewalletevalacion.domain.usecases

import android.util.Log

import com.example.alkewalletevalacion.data.network.api.AuthService
import com.example.alkewalletevalacion.data.network.response.AccountResponse
import com.example.alkewalletevalacion.data.network.response.TransactionListResponse
import com.example.alkewalletevalacion.data.network.response.TransactionResponse


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Transacciones para el SendingMoney
 */
class TransactionUseCase(private val authService: AuthService) {
    fun getTransactions(callback: (Boolean, List<TransactionResponse>?) -> Unit) {
        authService.getTransactions().enqueue(object : Callback<TransactionListResponse> {
            override fun onResponse(
                call: Call<TransactionListResponse>,
                response: Response<TransactionListResponse>
            ) {
                if (response.isSuccessful) {
                    val transactionListResponse = response.body()
                    if (transactionListResponse != null) {

                        callback(true, transactionListResponse.data)
                    } else {

                        callback(true, null)
                    }
                } else {

                    callback(false, null)
                }
            }

            override fun onFailure(call: Call<TransactionListResponse>, t: Throwable) {

                callback(false, null)
            }
        })
    }
}