package com.corrot.kwiatonomousapp.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.corrot.kwiatonomousapp.data.local.database.KwiatonomousDatabase
import com.corrot.kwiatonomousapp.data.local.database.entity.UserEntity
import com.corrot.kwiatonomousapp.data.local.database.entity.toUser
import com.corrot.kwiatonomousapp.data.remote.api.KwiatonomousApi
import com.corrot.kwiatonomousapp.data.remote.dto.UserDto
import com.corrot.kwiatonomousapp.domain.model.User
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val kwiatonomousApi: KwiatonomousApi,
    private val kwiatonomousDb: KwiatonomousDatabase
) : UserRepository {

    override suspend fun fetchUserById(userId: String): UserDto {
        return kwiatonomousApi.getUserById(userId)
    }

    override suspend fun fetchAddedDevicesIdsByUserId(userId: String): List<String> {
        return kwiatonomousApi.getAddedDevicesIdsByUserId(userId)
    }

    override suspend fun updateAddedDevicesIdsByDeviceId(
        userId: String,
        addedDevicesIds: List<String>
    ) {
        kwiatonomousApi.updateAddedDevicesIdsByUserId(userId, addedDevicesIds.joinToString(","))
    }

    override fun getUserFromDatabase(userId: String): Flow<User> =
        kwiatonomousDb.userDao().getByUserId(userId)
            .map { it.toUser() }
            // When database is empty null will be returned and `toUser` will throw exception.
            // Let's catch it - this will also emit some kind of empty flow to notify when we call
            // `query().firstOrNull()` in networkBoundResource
            .catch { t ->
                Log.e("UserRepositoryImpl", "getUserFromDatabase: $t")
            }

    override fun getUsersFromDatabase(): Flow<List<User>> {
        return kwiatonomousDb.userDao().getAll().map { users ->
            users.map { it.toUser() }
        }
    }

    override suspend fun saveFetchedUser(user: UserEntity) {
        kwiatonomousDb.withTransaction {
            kwiatonomousDb.userDao().insertOrUpdate(user)
        }
    }
}