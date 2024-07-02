package com.example.alkewalletevalacion.domain.usecases

import android.util.Log
import com.example.alkewalletevalacion.data.network.api.AuthService
import com.example.alkewalletevalacion.data.network.response.TransactionRequest
import com.example.alkewalletevalacion.data.network.response.TransactionResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Creacion de Transacciones para el RequestMoney
 */
class CreateTransactionUseCase(private val authService: AuthService) {
    fun createTransaction(transactionRequest: TransactionRequest, callback: (Boolean, TransactionResponse?) -> Unit) {
        authService.createTransaction(transactionRequest).enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(call: Call<TransactionResponse>, response: Response<TransactionResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {

                        callback(true, responseBody)
                    } else {

                        callback(false, null)
                    }
                } else {

                    callback(false, null)
                }
            }

            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {

                callback(false, null)
            }
        })
    }
}