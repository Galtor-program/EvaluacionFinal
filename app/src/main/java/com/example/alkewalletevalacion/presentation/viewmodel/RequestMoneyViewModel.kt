package com.example.alkewalletevalacion.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alkewalletevalacion.data.network.response.AccountResponse
import com.example.alkewalletevalacion.data.network.response.TransactionRequest
import com.example.alkewalletevalacion.data.network.response.TransactionResponse
import com.example.alkewalletevalacion.data.network.response.UserResponse
import com.example.alkewalletevalacion.domain.usecases.AccountInfoUseCase
import com.example.alkewalletevalacion.domain.usecases.CreateTransactionUseCase
import com.example.alkewalletevalacion.domain.usecases.TransactionUseCase
import com.example.alkewalletevalacion.domain.usecases.UserListUseCase
import kotlinx.coroutines.launch

class RequestMoneyViewModel(
    private val userListUseCase: UserListUseCase,
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val transactionUseCase: TransactionUseCase,
    private val accountInfoUseCase: AccountInfoUseCase
) : ViewModel() {

    private val _selectedUser = MutableLiveData<UserResponse?>()
    val selectedUser: LiveData<UserResponse?> get() = _selectedUser

    private val _userId = MutableLiveData<Int?>()
    val userId: MutableLiveData<Int?> get() = _userId

    private val _accountId = MutableLiveData<Int?>()
    val accountId: LiveData<Int?> get() = _accountId

    private val _transactionResult = MutableLiveData<Boolean>()
    val transactionResult: LiveData<Boolean> get() = _transactionResult

    private val _transactions = MutableLiveData<List<TransactionResponse>?>()
    val transactions: LiveData<List<TransactionResponse>?> get() = _transactions

    fun fetchUserById(userId: Int) {
        viewModelScope.launch {
            try {
                val user = userListUseCase.getUserById(userId)
                _selectedUser.value = user
            } catch (e: Exception) {
                _selectedUser.value = null
            }
        }
    }

    fun fetchUserId() {
        viewModelScope.launch {
            try {
                val id = userListUseCase.getLoggedInUserId()
                _userId.value = id
            } catch (e: Exception) {
                _userId.value = null
            }
        }
    }

    fun fetchUserAccountId() {
        accountInfoUseCase.getAccountInfo { success, accountList ->
            if (success && accountList != null) {
                _accountId.postValue(accountList.firstOrNull()?.id)
            } else {
                _accountId.postValue(null)
            }
        }
    }

    fun createTransaction(transactionRequest: TransactionRequest) {
        createTransactionUseCase.createTransaction(transactionRequest) { success, _ ->
            _transactionResult.value = success
        }
    }

    fun fetchTransactions() {
        transactionUseCase.getTransactions { success, transactionList ->
            if (success) {
                _transactions.postValue(transactionList)
            } else {
                _transactions.postValue(null)
            }
        }
    }
}