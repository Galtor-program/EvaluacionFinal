package com.example.alkewalletevalacion.domain.usecases


import com.example.alkewalletevalacion.data.network.api.AuthService
import com.example.alkewalletevalacion.data.network.response.NewUserRequest
import com.example.alkewalletevalacion.data.network.response.NewUserResponse


/**
 * Creacion del usuario
 */
class RegisterUserUseCase(private val authService: AuthService) {

    suspend fun execute(newUserRequest: NewUserRequest): NewUserResponse {
        return authService.registerUser(newUserRequest)
    }




}