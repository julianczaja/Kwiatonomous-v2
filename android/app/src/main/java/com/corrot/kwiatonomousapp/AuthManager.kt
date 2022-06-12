package com.corrot.kwiatonomousapp

import com.corrot.kwiatonomousapp.common.Constants
import com.corrot.kwiatonomousapp.common.toMD5
import com.corrot.kwiatonomousapp.data.remote.dto.toUserEntity
import com.corrot.kwiatonomousapp.domain.model.RegisterCredentials
import com.corrot.kwiatonomousapp.domain.repository.NetworkPreferencesRepository
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.HttpException
import javax.inject.Inject

data class AuthHeader(
    val realm: String,
    val nonce: String,
    val algorithm: String
)

class AuthManager @Inject constructor(
    private val userRepository: UserRepository,
    private val networkPreferencesRepository: NetworkPreferencesRepository
) {
    @Throws(Exception::class)
    suspend fun checkIfLoggedIn(): Boolean {
        return try {
            userRepository.fetchCurrentUser().let {
                userRepository.saveFetchedUser(it.toUserEntity(true))
            }
            true
        } catch (e: HttpException) {
            if (e.code() == 401) {
                return false
            } else {
                throw Exception(e.message() ?: "Error code: ${e.code()}")
            }
        }
    }

    @Throws(Exception::class)
    suspend fun tryToLogin(login: String, password: String) {
        try {
            userRepository.fetchCurrentUser().let {
                userRepository.saveFetchedUser(it.toUserEntity(true))
            }
        } catch (e: HttpException) {
            if (e.code() == 401) {
                val response =
                    e.response() ?: throw Exception("Error while parsing authentication header")

                val authHeader = response.headers()["WWW-Authenticate"]
                val authHeaderObj = parseAuthHeader(authHeader!!)
                    ?: throw Exception("Error while parsing authentication header")

                val ha1 = "$login:${Constants.API_REALM}:$password".toMD5()
                networkPreferencesRepository.run {
                    updateLogin(login)
                    updateHa1(ha1)
                    updateLastNonce(authHeaderObj.nonce)
                }

                userRepository.fetchCurrentUser().let {
                    userRepository.saveFetchedUser(it.toUserEntity(true))
                }
            } else {
                throw Exception(e.message() ?: "Error code: ${e.code()}")
            }
        }
    }

    suspend fun logOut() {
        val user = userRepository.getCurrentUserFromDatabase().firstOrNull()
            ?: throw Exception("User null before logOut")
        userRepository.updateUser(user.copy(isLoggedIn = false))
        networkPreferencesRepository.clearCredentials()
    }

    // FIXME: Sending credentials in plain text using HTTP is stupid idea,
    //  but for now it has to stay like that
    @Throws(Exception::class)
    suspend fun tryToRegister(login: String, password: String) {
        val credentials = RegisterCredentials(login, password)
        userRepository.registerNewAccount(credentials)
    }

    private fun parseAuthHeader(header: String): AuthHeader? {
        val parts = header.removePrefix("Digest").split(",")
        val mapped = parts.map {
            val split = it.split("=")
            return@map Pair(split.first().trim(), split.last().removeSurrounding("\""))
        }

        val realm = mapped.find { it.first == "realm" }?.second
        val nonce = mapped.find { it.first == "nonce" }?.second
        val algorithm = mapped.find { it.first == "algorithm" }?.second

        if (realm.isNullOrEmpty() || nonce.isNullOrEmpty() || algorithm.isNullOrEmpty()) {
            return null
        }

        return AuthHeader(realm, nonce, algorithm)
    }
}