package com.corrot.kwiatonomousapp.data.remote

import android.util.Log
import com.corrot.kwiatonomousapp.common.Constants
import com.corrot.kwiatonomousapp.common.toMD5
import com.corrot.kwiatonomousapp.domain.repository.NetworkPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class DigestAuthInterceptor @Inject constructor(
    private var networkPreferencesRepository: NetworkPreferencesRepository
) : Interceptor {

    // FIXME
    companion object {
        var username = "test"
        var password = "test"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()

        val nonce = runBlocking { networkPreferencesRepository.lastNonce().first() }

        if (nonce.isEmpty()) {
            Log.e("TAG", "NONCE UNKNOWN")
            return chain.proceed(builder.build())
        }

        val method = request.method
        val uri = request.url.encodedPath

        // HA1 -> MD5(username:realm:password)
        // HA2 -> MD5(method:digestURI)
        // response -> MD5(HA1:nonce:HA2)
        val HA1 = "$username:${Constants.REALM}:$password".toMD5()
        val HA2 = "$method:$uri".toMD5()
        val response = "$HA1:$nonce:$HA2".toMD5()

        val authHeader = getDigestAuthHeader(
            username,
            Constants.REALM,
            nonce,
            uri,
            response
        )

        builder.addHeader("Authorization", authHeader)

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
