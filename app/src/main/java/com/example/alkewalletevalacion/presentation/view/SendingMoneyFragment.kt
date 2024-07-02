package com.example.alkewalletevalacion.presentation.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.alkewalletevalacion.data.network.response.TransactionRequest
import com.example.alkewalletevalacion.data.network.retrofit.RetrofitHelper
import com.example.alkewalletevalacion.databinding.FragmentSendingMoneyBinding
import com.example.alkewalletevalacion.domain.usecases.AccountInfoUseCase
import com.example.alkewalletevalacion.domain.usecases.CreateTransactionUseCase
import com.example.alkewalletevalacion.domain.usecases.TransactionUseCase
import com.example.alkewalletevalacion.domain.usecases.UserListUseCase
import com.example.alkewalletevalacion.presentation.viewmodel.SendingMoneyViewModel
import com.example.alkewalletevalacion.presentation.viewmodel.SendingMoneyViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SendingMoneyFragment : Fragment() {

    private var _binding: FragmentSendingMoneyBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SendingMoneyViewModel
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var sharedPreferences: SharedPreferences
    val usuarioId : Int = 0
    val cuentaId: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSendingMoneyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        sharedPreferences = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val authService = RetrofitHelper.getAuthService(requireContext())

        val userListUseCase = UserListUseCase(authService, sharedPreferences)
        val createTransactionUseCase = CreateTransactionUseCase(authService)
        val transactionUseCase = TransactionUseCase(authService)
        val accountInfoUseCase = AccountInfoUseCase(authService)
        viewModel = ViewModelProvider(this, SendingMoneyViewModelFactory(userListUseCase, createTransactionUseCase, transactionUseCase, accountInfoUseCase)).get(SendingMoneyViewModel::class.java)

        setupObservers()

        binding.sendingMoney.setOnClickListener {
            handleSendMoney()
        }

        /**
         * Utilizamos los metodos creados mas abajo para
         * solicitar la lista de usuarios el id del suario
         * y el id de la cuenta del usuario
         */
        if (savedInstanceState == null) {
            viewModel.fetchUsuariosList()
            viewModel.fetchUserId()
            viewModel.fetchUserAccountId()
        }
    }

    private fun setupObservers() {
        viewModel.usuariosList.observe(viewLifecycleOwner) { usuarios ->
            usuarios?.let {
                val userNames = it.map { user -> "${user.firstName} ${user.lastName}" }
                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, userNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinner.adapter = adapter
            }
        }
        /**
         * Toast de mensajes si es que la transaccion se realizo con exito
         */

        viewModel.transactionResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(
                    requireContext(),
                    "Transferencia realizada con éxito",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error al realizar la transferencia",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        /**
         * Aca el observer ve la actualizacion de las transacciones
         */
        viewModel.transactions.observe(viewLifecycleOwner) { transactionList ->
            transactionList?.let {
                transactionAdapter.updateTransactions(it)
                Log.d(
                    "SendingMoneyFragment",
                    "transactions LiveData Observer - TransactionResponse List: $transactionList"
                )
            }
        }
    }

    /**
     * Mandamos los datos a base de datos
     */
    private fun handleSendMoney() {
        val selectedUser = viewModel.usuariosList.value?.get(binding.spinner.selectedItemPosition)
        val usuarioId = viewModel.userId.value?.get(usuarioId)
        val cuentaId = viewModel.accountId.value?.get(cuentaId)
        if (selectedUser != null) {
            val amount = binding.dinero.text.toString().toIntOrNull()
            val concept = binding.motivo.text.toString()
            if (amount != null && concept.isNotEmpty()) {
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val transactionRequest = usuarioId?.userId?.let {
                    cuentaId?.id?.let { it1 ->
                        TransactionRequest(
                            amount = amount,
                            concept = concept,
                            date = date,
                            type = "payment",
                            accountId = it1,
                            userId = it,
                            toAccountId = selectedUser.id
                        )
                    }
                }
                if (transactionRequest != null) {
                    viewModel.createTransaction(transactionRequest)
                }
            } else {
                Toast.makeText(requireContext(), "Por favor, introduce una cantidad y un concepto válidos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupToolbar() {
        val toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}