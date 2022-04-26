package com.corrot.kwiatonomousapp.common

import java.security.MessageDigest

fun Float.mapBetween(inMin: Float, inMax: Float, outMin: Float, outMax: Float): Float {
    return (this - inMin) * (outMax - outMin) / (inMax - inMin) + outMin
}

fun Int.toBoolean() = this == 1

fun Boolean.toInt() = if (this) 1 else 0

fun String.toMD5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.toHex()
}

fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}