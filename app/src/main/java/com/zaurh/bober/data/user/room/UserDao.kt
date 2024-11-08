package com.zaurh.bober.data.user.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userEntity: UserEntity)

    @Query("DELETE FROM userEntity WHERE userId = :userId")
    suspend fun deleteUser(userId: String): Int


    @Query("SELECT * FROM userEntity WHERE userId = :userId")
    suspend fun getUserById(userId: String): UserEntity?

}