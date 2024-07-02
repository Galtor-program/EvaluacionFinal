package com.example.alkewalletevalacion.data.network.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
@Entity(tableName = "usuarios")
data class UserListResponse(
        @PrimaryKey
        @SerializedName("id") val id: Int,
        @SerializedName("first_name") val firstName: String,
        @SerializedName("last_name") val lastName: String,
        @SerializedName("email") val email: String,
        @SerializedName("password") val password: String,
        @SerializedName("points") val points: Int,
        @SerializedName("roleId") val roleId: Int
    )