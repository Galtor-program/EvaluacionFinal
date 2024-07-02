package com.example.alkewalletevalacion.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alkewalletevalacion.data.network.response.AccountResponse
import com.example.alkewalletevalacion.data.network.response.TransactionRequest
import com.example.alkewalletevalacion.data.network.response.TransactionResponse

import com.example.alkewalletevalacion.data.network.response.UserListResponse
import com.example.alkewalletevalacion.domain.usecases.AccountInfoUseCase
import com.example.alkewalletevalacion.domain.usecases.CreateTransactionUseCase
import com.example.alkewalletevalacion.domain.usecases.TransactionUseCase
import com.example.alkewalletevalacion.domain.usecases.UserListUseCase


class SendingMoneyViewModel(
    private val userListUseCase: UserListUseCase,
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val transactionUseCase: TransactionUseCase,
    private val accountInfoUseCase: AccountInfoUseCase
) : ViewModel() {

    /**
     * Para la lista de usuarios
     */
    private val _usuariosList = MutableLiveData<List<UserListResponse>?>()
    val usuariosList: MutableLiveData<List<UserListResponse>?> get() = _usuariosList
    /**
     * Para la id del usuario
     */
    private val _userId = MutableLiveData<List<AccountResponse>?>()
    val userId: MutableLiveData<List<AccountResponse>?>
        get() = _userId

    /**
     * Para la id de la cuenta
     */

    private val _accountId = MutableLiveData<List<AccountResponse>?>()
    val accountId: MutableLiveData<List<AccountResponse>?>
        get() = _accountId



    /**
     * Realizar transacciones
     */
    private val _transactionResult = MutableLiveData<Boolean>()
    val transactionResult: LiveData<Boolean> get() = _transactionResult


    /**
     * Ver transacciones
     */
    private val _transactions = MutableLiveData<List<TransactionResponse>?>()
    val transactions: LiveData<List<TransactionResponse>?> get() = _transactions

    /**
     * Sacar el Id del usuario desde la informacion del
     * usuario logueado
     */
    fun fetchUserId(){
        accountInfoUseCase.getAccountInfo { success, accountResponses ->
            if(success){
                _userId.value = accountResponses
            }
        }
    }

    /**
     * Para sacar el id de la cuenta del usuario logueado
     */
    fun fetchUserAccountId(){
        accountInfoUseCase.getAccountInfo { success, accountResponses ->
            if(success){
                _accountId.value = accountResponses
            }
        }
    }


    /**
     * Lista de usuarios para el spinner
     */

    fun fetchUsuariosList() {
        userListUseCase.getUsers { success, userList ->
            if (success) {
                _usuariosList.value = userList
            }
        }
    }

    /**
     * Para la creacion de la Transaccion
     */
    fun createTransaction(transactionRequest: TransactionRequest) {
        createTransactionUseCase.createTransaction(transactionRequest) { success, _ ->
            _transactionResult.value = success
            if (success) {
                // Fetch updated transactions after a successful transaction creation
                fetchTransactions()
            }
        }
    }

    fun fetchTransactions() {
        transactionUseCase.getTransactions { success, transactionResponse ->
            if (success) {
                if (transactionResponse != null) {
                    _transactions.value = transactionResponse
                } else {
                    Log.e("SendingMoneyViewModel", "fetchTransactions - Success but transactionResponse is null")
                }
            } else {
                Log.e("SendingMoneyViewModel", "fetchTransactions - Error fetching transactions")
            }
        }
    }
}