package com.example.alkewalletevalacion.presentation.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alkewalletevalacion.R
import com.example.alkewalletevalacion.data.network.response.AccountResponse
import com.example.alkewalletevalacion.data.network.response.UserListResponse
import com.example.alkewalletevalacion.data.network.response.UserListWrapper

import com.example.alkewalletevalacion.databinding.FragmentHomeBinding
import com.example.alkewalletevalacion.domain.usecases.UserInfoUseCase
import com.example.alkewalletevalacion.data.network.retrofit.RetrofitHelper
import com.example.alkewalletevalacion.domain.usecases.AccountInfoUseCase
import com.example.alkewalletevalacion.domain.usecases.TransactionUseCase
import com.example.alkewalletevalacion.domain.utils.GlobalUserList
import com.example.alkewalletevalacion.presentation.viewmodel.HomeViewModel
import com.example.alkewalletevalacion.presentation.viewmodel.HomeViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var transactionAdapter: TransactionAdapter

    private val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicialización del RecyclerView y el Adaptador
        transactionAdapter = TransactionAdapter(emptyList())
        binding.recyclerTrasactions.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        // Configuración del ViewModel y Observadores
        val authService = RetrofitHelper.getAuthService(requireContext())
        val userInfoUseCase = UserInfoUseCase(authService)
        val accountInfoUseCase = AccountInfoUseCase(authService)
        val transactionUseCase = TransactionUseCase(authService)
        val viewModelFactory = HomeViewModelFactory(userInfoUseCase, accountInfoUseCase, transactionUseCase)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        viewModel.userInfo.observe(viewLifecycleOwner, Observer { userInfo ->
            userInfo?.let {
                binding.nombreUsuario.text = "Bienvenido! ${it.firstName}"
            }
        })

        viewModel.accountInfo.observe(viewLifecycleOwner, Observer { accountInfoList ->
            accountInfoList?.let {
                if (it.isNotEmpty()) {
                    val accountInfo = it[0]
                    binding.cuentaUsuario.text = "${accountInfo.money}"
                    Log.d(TAG, "accountInfo LiveData Observer - AccountResponse List: $accountInfoList")
                }
            }
        })

        viewModel.transactions.observe(viewLifecycleOwner, Observer { transactionList ->
            transactionList?.let {
                transactionAdapter.updateTransactions(it)
                Log.d(TAG, "transactions LiveData Observer - TransactionResponse List: $transactionList")
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Toast.makeText(requireContext(), "Error en el Observer : $it", Toast.LENGTH_SHORT).show()
            }
        })

        // Botón para navegar al SendingMoneyFragment
        binding.buttonEnviar.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_sendingMoneyFragment)
        }
        /**
         * Boton para navegar al RequestMoneyFragment
         */
        binding.botonIngresar.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_requestMoneyFragment)
        }


        /**
         *   Obtener información del usuario, cuentas y transacciones
         */

        viewModel.fetchUserInfo()
        fetchAllUsersAndAccounts()
    }


    private fun fetchAllUsersAndAccounts() {
        val authService = RetrofitHelper.getAuthService(requireContext())

        // Fetch Users
        authService.getUsers().enqueue(object : Callback<UserListWrapper> {
            override fun onResponse(call: Call<UserListWrapper>, response: Response<UserListWrapper>) {
                if (response.isSuccessful) {
                    response.body()?.data?.let { GlobalUserList.setUsers(it) }
                    Log.d(TAG, "Users fetched successfully")
                    // Fetch Accounts after Users are fetched
                    fetchAccounts()
                } else {
                    Log.e(TAG, "Failed to fetch users: ${response.code()} ${response.message()}")
                    Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserListWrapper>, t: Throwable) {
                Log.e(TAG, "Error fetching users", t)
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchAccounts() {
        val apiService = RetrofitHelper.getAuthService(requireContext())

        apiService.getAccountInfo().enqueue(object : Callback<List<AccountResponse>> {
            override fun onResponse(call: Call<List<AccountResponse>>, response: Response<List<AccountResponse>>) {
                if (response.isSuccessful) {
                    val accounts = response.body() ?: emptyList()
                    GlobalUserList.setAccounts(accounts)
                    Log.d("HomeFragment", "Accounts fetched successfully: $accounts")
                    // Actualizar el RecyclerView con las transacciones del usuario
                    viewModel.fetchTransactions()
                } else {
                    Log.e("HomeFragment", "Failed to fetch accounts: ${response.code()} ${response.message()}")
                    Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<AccountResponse>>, t: Throwable) {
                Log.e("HomeFragment", "Error fetching accounts", t)
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onResume() {
        super.onResume()
        // Forzar la actualización de los datos cuando el fragmento vuelve a ser visible
        viewModel.fetchUserInfo()
        fetchAllUsersAndAccounts()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
