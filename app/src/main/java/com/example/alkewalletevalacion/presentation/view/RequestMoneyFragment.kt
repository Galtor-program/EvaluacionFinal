package com.example.alkewalletevalacion.presentation.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.alkewalletevalacion.data.network.response.TransactionRequest
import com.example.alkewalletevalacion.data.network.retrofit.RetrofitHelper
import com.example.alkewalletevalacion.databinding.FragmentRequestMoneyBinding
import com.example.alkewalletevalacion.domain.usecases.AccountInfoUseCase
import com.example.alkewalletevalacion.domain.usecases.CreateTransactionUseCase
import com.example.alkewalletevalacion.domain.usecases.TransactionUseCase
import com.example.alkewalletevalacion.domain.usecases.UserListUseCase
import com.example.alkewalletevalacion.presentation.viewmodel.RequestMoneyViewModel
import com.example.alkewalletevalacion.presentation.viewmodel.RequestMoneyViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RequestMoneyFragment : Fragment() {

    private var _binding: FragmentRequestMoneyBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RequestMoneyViewModel
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRequestMoneyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        val authService = RetrofitHelper.getAuthService(requireContext())
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userListUseCase = UserListUseCase(authService, sharedPreferences)
        val createTransactionUseCase = CreateTransactionUseCase(authService)
        val transactionUseCase = TransactionUseCase(authService)
        val accountInfoUseCase = AccountInfoUseCase(authService)

        viewModel = ViewModelProvider(this, RequestMoneyViewModelFactory(
            userListUseCase, createTransactionUseCase, transactionUseCase, accountInfoUseCase
        )).get(RequestMoneyViewModel::class.java)

        setupObservers()

        binding.ingresarDineroButton.setOnClickListener {
            handleRequestMoney()
        }

        if (savedInstanceState == null) {
            viewModel.fetchUserById(5)
            viewModel.fetchUserId()
            viewModel.fetchUserAccountId()
        }
    }

    private fun setupObservers() {
        viewModel.selectedUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.nombre.text = "${user.firstName} ${user.lastName}"
                binding.correo.text = user.email
            }
        }

        viewModel.transactionResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(
                    requireContext(),
                    "Solicitud de dinero enviada con éxito",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error al enviar la solicitud de dinero",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.transactions.observe(viewLifecycleOwner) { transactionList ->
            transactionList?.let {
                transactionAdapter.updateTransactions(it)
            }
        }
    }

    private fun handleRequestMoney() {
        val userId = viewModel.userId.value
        val accountId = viewModel.accountId.value
        val selectedUser = viewModel.selectedUser.value

        if (selectedUser != null && userId != null && accountId != null) {
            val amount = binding.ingresarDienero.text.toString().toIntOrNull()
            val concept = binding.editTextTextMultiLine2.text.toString()
            if (amount != null && concept.isNotEmpty()) {
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val transactionRequest = TransactionRequest(
                    amount = amount,
                    concept = concept,
                    date = date,
                    type = "request",
                    accountId = 11,
                    userId = 5,
                    toAccountId = accountId
                )
                viewModel.createTransaction(transactionRequest)
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