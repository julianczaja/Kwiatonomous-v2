package com.corrot

import com.corrot.Constants.KWIATONOMOUS_DIGEST_AUTH
import com.corrot.Constants.KWIATONOMOUS_REALM
import com.corrot.db.data.dao.UserDao
import io.ktor.server.application.*
import io.ktor.server.auth.*
import java.nio.charset.StandardCharsets
import java.security.MessageDigest


fun Application.configureSecurity(userDao: UserDao) {

    authentication {
        digest(KWIATONOMOUS_DIGEST_AUTH) {
            realm = KWIATONOMOUS_REALM
            digestProvider { userId, realm ->
                val ha1 = userDao.getUser(userId)?.ha1

//                println("----------- AUTH -----------")
//                println("userId = $userId")
//                println("ha1 = ${ha1?.joinToString("") { "%02x".format(it) }}")
//                println(if (ha1 == null) "FAILED" else "OK")
//                println("---------------------------")

                return@digestProvider ha1
            }
        }
    }
}

/**
 * Calculates HA1 = MD5(username:realm:password)
 */
fun calculateHA1(username: String, password: String, realm: String = KWIATONOMOUS_REALM): ByteArray {
    return getMd5Digest("$username:$realm:$password")
}

private fun getMd5Digest(str: String): ByteArray {
    val md = MessageDigest.getInstance("MD5")
    return md.digest(str.toByteArray(StandardCharsets.UTF_8))
}
