package com.example.alkewalletevalacion.data.network.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
@Entity(tableName = "cuentas_usuarios")
data class AccountResponse(
    @PrimaryKey
    @SerializedName("id") val id: Int?,
    @SerializedName("creationDate") val creationDate: String?,
    @SerializedName("money") val money: Int?,
    @SerializedName("isBlocked") val isBlocked: Boolean?,
    @SerializedName("userId") val userId: Int?
)