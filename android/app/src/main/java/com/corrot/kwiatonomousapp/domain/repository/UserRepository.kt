package com.corrot.kwiatonomousapp.domain.repository

import com.corrot.kwiatonomousapp.data.local.database.entity.UserEntity
import com.corrot.kwiatonomousapp.data.remote.dto.UserDto
import com.corrot.kwiatonomousapp.domain.model.RegisterCredentials
import com.corrot.kwiatonomousapp.domain.model.User
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    // Remote
    suspend fun fetchCurrentUser(): UserDto

    suspend fun updateCurrentUserAddedDevices(userDevices: List<UserDevice>)

    suspend fun registerNewAccount(registerCredentials: RegisterCredentials)

    // Local
    fun getUserFromDatabase(userId: String): Flow<User>

    fun getCurrentUserFromDatabase(): Flow<User?>

    suspend fun updateUser(user: User)

    suspend fun saveFetchedUser(user: UserEntity)
}