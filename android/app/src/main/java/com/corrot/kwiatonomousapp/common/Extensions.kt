package com.corrot.kwiatonomousapp.common

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
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

// From Google Compose code samples
// https://github.com/android/compose-samples/blob/44421ce2a78f1d53e66558cb4a9ebf9a3ddad35d/JetNews/app/src/main/java/com/example/jetnews/utils/LazyListUtils.kt
val LazyListState.isScrolled: Boolean
    get() = derivedStateOf { firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0 }.value

// From Google Code Lab
// https://github.com/googlecodelabs/android-compose-codelabs/blob/main/AnimationCodelab/finished/src/main/java/com/example/android/codelab/animation/ui/home/Home.kt#L339
@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}