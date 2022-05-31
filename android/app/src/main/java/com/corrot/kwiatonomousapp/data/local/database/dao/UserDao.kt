package com.corrot.kwiatonomousapp.data.local.database.dao

import androidx.room.*
import com.corrot.kwiatonomousapp.data.local.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    fun getAll(): Flow<List<UserEntity>>

    @Query("SELECT * FROM user WHERE userId = :userId")
    fun getByUserId(userId: String): Flow<UserEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(users: List<UserEntity>): List<Long>

    @Update
    suspend fun update(user: UserEntity)

    @Update
    suspend fun update(users: List<UserEntity>)

    @Query("DELETE FROM user WHERE userId = :userId")
    suspend fun removeByUserId(userId: String)

    @Query("DELETE FROM user")
    suspend fun removeAll()

    @Transaction
    suspend fun insertOrUpdate(user: UserEntity) {
        val id = insert(user)
        if (id == -1L) update(user)
    }

    @Transaction
    suspend fun insertOrUpdate(users: List<UserEntity>) {
        val insertResult = insert(users)
        val updateList = mutableListOf<UserEntity>()

        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) updateList.add(users[i])
        }

        if (updateList.isNotEmpty()) update(updateList)
    }
}