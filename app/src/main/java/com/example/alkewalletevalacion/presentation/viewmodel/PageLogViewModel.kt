package com.example.alkewalletevalacion.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alkewalletevalacion.data.network.response.LoginRequest
import com.example.alkewalletevalacion.data.network.response.NewAccountRequest
import com.example.alkewalletevalacion.domain.usecases.AuthUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PageLogViewModel(private val authUseCase: AuthUseCase, application: Application) : AndroidViewModel(application) {

    val navigateToSignUp = MutableLiveData<Unit>()
    val navigationToHome = MutableLiveData<Unit>()
    val loginError = MutableLiveData<String>()

    fun onLoginClick(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            loginError.value = "Email y contraseña no pueden estar vacíos"
            return
        }

        val loginRequest = LoginRequest(email, password)
        authUseCase.authenticateUser(loginRequest) { success, accessToken ->
            if (success) {
                // Guardar el token en SharedPreferences
                val sharedPreferences = getApplication<Application>().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("accessToken", accessToken).apply()

                if (accessToken != null) {
                    checkAndCreateAccount(accessToken) { accountCreated ->
                        if (accountCreated) {
                            navigationToHome.value = Unit
                        } else {
                            Log.e("PageLogViewModel", "Error al crear la cuenta")
                        }
                    }
                }
            } else {
                loginError.value = "Error en autenticación: No se pudo iniciar sesión."
                Log.e("PageLogViewModel", "Error en autenticación: No se pudo iniciar sesión.")
            }
        }
    }

    private fun checkAndCreateAccount(accessToken: String, callback: (Boolean) -> Unit) {
        authUseCase.getUserInfo(accessToken) { success, userResponse ->
            if (success && userResponse != null) {
                // Verifica si el usuario tiene cuenta
                authUseCase.getAccountInfo(accessToken) { accountSuccess, accountResponse ->
                    if (accountSuccess && !accountResponse.isNullOrEmpty()) {
                        callback(true)
                    } else {
                        /**
                         * Si el usuario no cuenta con la cuenta se la creamos,
                         * esto se realiza de esta forma ya que necesitamos primero el token
                         * para poder crear la cuenta.
                         */
                        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                        val newAccountRequest = userResponse.id?.let {
                            NewAccountRequest(
                                creationDate = date,
                                money = 150000,
                                isBlocked = false,
                                userId = it
                            )
                        }

                        if (newAccountRequest != null) {
                            authUseCase.createAccountWithToken(newAccountRequest, accessToken) { accountCreated ->
                                callback(accountCreated)
                            }
                        }
                    }
                }
            } else {
                callback(false)
            }
        }
    }

    class Factory(private val application: Application, private val authUseCase: AuthUseCase) : ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PageLogViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PageLogViewModel(authUseCase, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}