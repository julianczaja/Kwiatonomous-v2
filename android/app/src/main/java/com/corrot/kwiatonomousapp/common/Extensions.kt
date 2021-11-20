package com.corrot.kwiatonomousapp.common

fun Float.mapBetween(inMin: Float, inMax: Float, outMin: Float, outMax: Float): Float {
    return (this - inMin) * (outMax - outMin) / (inMax - inMin) + outMin
}