package com.corrot.kwiatonomousapp.data.remote

import android.util.Log
import com.corrot.kwiatonomousapp.common.Constants.API_REALM
import com.corrot.kwiatonomousapp.common.toMD5
import com.corrot.kwiatonomousapp.domain.repository.NetworkPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class DigestAuthInterceptor @Inject constructor(
    private val networkPreferencesRepository: NetworkPreferencesRepository
) : Interceptor {

    private companion object {
        const val TAG = "DigestAuthInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()

        if (request.url.encodedPath == "/kwiatonomous/android/register") {
            return chain.proceed(builder.build())
        }

        val login = runBlocking { networkPreferencesRepository.getLogin().first() }
        if (login.isEmpty()) {
            Log.e(TAG, "There is no login set")
//            throw Exception("There is no login set")
            return chain.proceed(builder.build())
        }

        val nonce = runBlocking { networkPreferencesRepository.getLastNonce().first() }
        if (nonce.isEmpty()) {
            Log.e(TAG, "There is no nonce set")
            return chain.proceed(builder.build())
        }

        // HA1 -> MD5(username:realm:password)
        val ha1 = runBlocking { networkPreferencesRepository.getHa1().first() }
        if (ha1.isEmpty()) {
            Log.e(TAG, "There is no HA1 set")
//            throw Exception("There is no HA1 set")
            return chain.proceed(builder.build())
        }

        // HA2 -> MD5(method:digestURI)
        val method = request.method
        val uri = request.url.encodedPath
        val ha2 = "$method:$uri".toMD5()

        // response -> MD5(HA1:nonce:HA2)
        val response = "$ha1:$nonce:$ha2".toMD5()

        Log.d(TAG, "login =\t$login")
        Log.d(TAG, "HA1 =\t$ha1")
        Log.d(TAG, "nonce =\t$nonce")

        getDigestAuthHeader(
            username = login,
            realm = API_REALM,
            nonce = nonce,
            uri = uri,
            response = response
        ).let { authHeader ->
            builder.addHeader("Authorization", authHeader)
        }

        return chain.proceed(builder.build())
    }

    private fun getDigestAuthHeader(
        username: String,
        realm: String,
        nonce: String,
        uri: String,
        response: String
    ): String {
        return StringBuilder()
            .append("Digest ")
            .append("username=\"$username\", ")
            .append("realm=\"$realm\", ")
            .append("nonce=\"$nonce\", ")
            .append("uri=\"$uri\", ")
            .append("algorithm=MD5, ")
            .append("response=\"$response\"")
            .toString()
    }
}
