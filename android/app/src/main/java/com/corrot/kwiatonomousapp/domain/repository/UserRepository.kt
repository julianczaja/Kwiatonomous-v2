package com.corrot.kwiatonomousapp.domain.repository

import com.corrot.kwiatonomousapp.data.local.database.entity.UserEntity
import com.corrot.kwiatonomousapp.data.remote.dto.UserDto
import com.corrot.kwiatonomousapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    // Remote
    suspend fun fetchUserById(userId: String): UserDto

    suspend fun fetchAddedDevicesIdsByUserId(userId: String): List<String>

    suspend fun updateAddedDevicesIdsByDeviceId(userId: String, addedDevicesIds: List<String>)

    // Local
    fun getUserFromDatabase(userId: String): Flow<User>

    fun getUsersFromDatabase(): Flow<List<User>>

    suspend fun saveFetchedUser(user: UserEntity)
}