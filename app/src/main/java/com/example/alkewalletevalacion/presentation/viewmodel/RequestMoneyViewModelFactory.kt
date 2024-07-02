package com.example.alkewalletevalacion.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alkewalletevalacion.domain.usecases.AccountInfoUseCase
import com.example.alkewalletevalacion.domain.usecases.CreateTransactionUseCase
import com.example.alkewalletevalacion.domain.usecases.TransactionUseCase
import com.example.alkewalletevalacion.domain.usecases.UserListUseCase

class RequestMoneyViewModelFactory(
    private val userListUseCase: UserListUseCase,
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val transactionUseCase: TransactionUseCase,
    private val accountInfoUseCase: AccountInfoUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RequestMoneyViewModel::class.java)) {
            return RequestMoneyViewModel(
                userListUseCase,
                createTransactionUseCase,
                transactionUseCase,
                accountInfoUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}