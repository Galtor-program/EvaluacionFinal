package com.example.alkewalletevalacion.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.alkewalletevalacion.data.local.dao.UserDao
import com.example.alkewalletevalacion.data.network.response.AccountResponse
import com.example.alkewalletevalacion.data.network.response.TransactionResponse
import com.example.alkewalletevalacion.data.network.response.UserListResponse
import kotlinx.coroutines.InternalCoroutinesApi
//TODO
@Database(entities = [UserListResponse::class, AccountResponse::class, TransactionResponse::class], version = 1, exportSchema = false)
abstract class AppDataBase: RoomDatabase() {

    abstract fun users(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "usuarios"
                ).build()
                INSTANCE = instance
                instance
            }


        }

    }
}