package com.example.alkewalletevalacion.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.alkewalletevalacion.data.network.response.UserListResponse
@Dao
interface UserDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser (userEntity: MutableList<UserListResponse>)

}