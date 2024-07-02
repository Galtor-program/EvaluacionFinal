package com.example.alkewalletevalacion.data.network.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "transacciones_usuario")
data class TransactionResponse(
    @PrimaryKey
    @SerializedName("id") val id: Int,
    @SerializedName("amount") val amount: Int,
    @SerializedName("concept") val concept: String,
    @SerializedName("date") val date: String,
    @SerializedName("type") val type: String,
    @SerializedName("accountId") val accountId: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("to_account_id") val toAccountId: Int
)