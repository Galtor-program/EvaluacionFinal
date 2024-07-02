package com.example.alkewalletevalacion.data.network.response

data class UpdateAccountRequest(
    val money: Int,
    val isBlocked: Boolean
)