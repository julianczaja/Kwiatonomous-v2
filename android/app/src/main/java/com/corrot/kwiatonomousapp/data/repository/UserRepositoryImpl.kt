package com.corrot.kwiatonomousapp.data.repository

import androidx.room.withTransaction
import com.corrot.kwiatonomousapp.data.local.database.KwiatonomousDatabase
import com.corrot.kwiatonomousapp.data.local.database.entity.UserEntity
import com.corrot.kwiatonomousapp.data.local.database.entity.toUser
import com.corrot.kwiatonomousapp.data.remote.api.KwiatonomousApi
import com.corrot.kwiatonomousapp.data.remote.dto.UserDto
import com.corrot.kwiatonomousapp.domain.model.RegisterCredentials
import com.corrot.kwiatonomousapp.domain.model.User
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.domain.model.toUserEntity
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val kwiatonomousApi: KwiatonomousApi,
    private val kwiatonomousDb: KwiatonomousDatabase
) : UserRepository {

    override suspend fun fetchCurrentUser(): UserDto {
        return kwiatonomousApi.getCurrentUser()
    }

    override suspend fun updateCurrentUserAddedDevices(userDevices: List<UserDevice>) {
        kwiatonomousApi.updateCurrentUserDevices(userDevices)
    }

    @Throws(Exception::class)
    override suspend fun registerNewAccount(registerCredentials: RegisterCredentials) {
        kwiatonomousApi.registerNewAccount(registerCredentials).run {
            if (code() != 200) {
                throw Exception(errorBody()?.string() ?: "Unknown error")
            }
        }
    }

    override fun getUserFromDatabase(userId: String): Flow<User> =
        kwiatonomousDb.userDao().getByUserId(userId)
            .map { it.toUser() }
            // When database is empty null will be returned and `toUser` will throw exception.
            // Let's catch it - this will also emit some kind of empty flow to notify when we call
            // `query().firstOrNull()` in networkBoundResource
            .catch { t ->
                Timber.e("getUserFromDatabase: $t")
            }

    override fun getCurrentUserFromDatabase(): Flow<User?> =
        kwiatonomousDb.userDao().getAll()
            .map { users ->
                users.firstOrNull { it.isLoggedIn }?.toUser()
            }

    override suspend fun updateUser(user: User) {
        kwiatonomousDb.withTransaction {
            kwiatonomousDb.userDao().insertOrUpdate(user.toUserEntity())
        }
    }

    override suspend fun saveFetchedUser(user: UserEntity) {
        kwiatonomousDb.withTransaction {
            kwiatonomousDb.userDao().insertOrUpdate(user)
        }
    }
}