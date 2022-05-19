package com.corrot.kwiatonomousapp

import android.util.Log
import com.corrot.kwiatonomousapp.data.remote.DigestAuthInterceptor
import com.corrot.kwiatonomousapp.data.remote.api.KwiatonomousApi
import com.corrot.kwiatonomousapp.domain.repository.NetworkPreferencesRepository
import com.corrot.kwiatonomousapp.presentation.login.AuthHeader
import javax.inject.Inject

class LoginManager @Inject constructor(
    private val kwiatonomousApi: KwiatonomousApi,
    private val networkPreferencesRepository: NetworkPreferencesRepository
) {
    suspend fun checkIfLoggedIn(): Boolean {
        // TODO: load login/password from memory
        val login = "test2"
        val password = "test"

        if (login.isNullOrEmpty() || password.isNullOrEmpty()) {
            return false
        }

        return checkIfLoggedIn(login, password)
    }

    suspend fun checkIfLoggedIn(login: String, password: String): Boolean {
        // FIXME
        DigestAuthInterceptor.username = login
        DigestAuthInterceptor.password = password

        return kwiatonomousApi.checkAccess(login).code() != 401
    }

    @Throws(Exception::class)
    suspend fun tryToLogin(login: String, password: String): Boolean {
        // FIXME
        DigestAuthInterceptor.username = login
        DigestAuthInterceptor.password = password

        val response = kwiatonomousApi.checkAccess(login)

        when (response.code()) {
            401 -> {
                val authHeader = response.headers()["WWW-Authenticate"]
                if (authHeader?.startsWith("Digest") == true) {

                    val authHeaderObj = parseAuthHeader(authHeader)
                        ?: throw Exception("Error while parsing authentication header")

                    networkPreferencesRepository.updateLastNonce(authHeaderObj.nonce)

                    if (checkIfLoggedIn(login, password)) {
                        return true
                    } else {
                        throw Exception("Wrong credentials")
                    }
                }
            }
            200 -> {
                Log.e("TAG", "200 while not logged in???")
                // ???
                return true
            }
            else -> {
                throw Exception(response.errorBody()?.string() ?: "Error code: ${response.code()}")
            }
        }

        return false
    }

    fun logOut() {
        // TODO: Remove saved credentials
        DigestAuthInterceptor.username = ""
        DigestAuthInterceptor.password = ""
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