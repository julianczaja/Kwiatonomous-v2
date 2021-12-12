package com.corrot.kwiatonomousapp.common

fun Float.mapBetween(inMin: Float, inMax: Float, outMin: Float, outMax: Float): Float {
    return (this - inMin) * (outMax - outMin) / (inMax - inMin) + outMin
}

fun Int.toBoolean() = this == 1

fun Boolean.toInt() = if (this) 1 else 0