package com.example.alkewalletevalacion.data.network.response


data class NewUserResponse(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val email: String,
    val roleId: Int,
    val points: Int
)