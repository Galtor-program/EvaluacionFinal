package com.example.alkewalletevalacion.presentation.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alkewalletevalacion.R
import com.example.alkewalletevalacion.data.network.response.TransactionResponse
import com.example.alkewalletevalacion.domain.utils.GlobalUserList
import com.squareup.picasso.Picasso

class TransactionAdapter(private var transactions: List<TransactionResponse>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transaccionTxt: TextView = itemView.findViewById(R.id.transaccionTxt)
        val dateTxT: TextView = itemView.findViewById(R.id.dateTxT)
        val nombreUser: TextView = itemView.findViewById(R.id.nombreUser)
        val img: ImageView = itemView.findViewById(R.id.avatar1)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return TransactionViewHolder(view)
    }

    /**
     * Pintamos los datos en el RecyclerView
     */
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.transaccionTxt.text = "+${transaction.amount}"
        holder.dateTxT.text = transaction.date
        Picasso.get()
            .load("https://avatar.iran.liara.run/public")
            .resize(80, 80)
            .centerCrop()
            .into(holder.img)
        val accountId = transaction.toAccountId

        Log.d("TransactionAdapter", "Binding transaction with toAccountId: $accountId")

        // Validar toAccountId
        if (accountId == 0) {
            Log.e("TransactionAdapter", "Invalid toAccountId: $accountId")
            holder.nombreUser.text = "Invalid Account"
            return
        }

        /**
         * Utilizamos el AccountId para buscar el id del usuario
         */
        val account = GlobalUserList.getAccountById(accountId)
        if (account != null) {
            Log.d("TransactionAdapter", "Account found for toAccountId $accountId: $account")
            // Buscar el usuario usando userId de la cuenta
            val user = account.userId?.let { GlobalUserList.getUserById(it) }
            if (user != null) {
                Log.d("TransactionAdapter", "User found for userId ${account.userId}: $user")
                holder.nombreUser.text = "${user.firstName}, ${user.lastName}"
            } else {
                Log.e("TransactionAdapter", "User not found for userId ${account.userId}")
                holder.nombreUser.text = "Error al cargar nombre"
            }
        } else {
            Log.e("TransactionAdapter", "Account not found for toAccountId $accountId")
            holder.nombreUser.text = "Error"
        }
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun updateTransactions(newTransactions: List<TransactionResponse>) {
        transactions = newTransactions
        Log.d("TransactionAdapter", "Transactions updated: $transactions")
        notifyDataSetChanged()
    }
}