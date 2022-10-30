package com.corrot.kwiatonomousapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDevice(
    val deviceId: String,
    val deviceName: String,
    val deviceImageName: String,
    val isFavourite: Boolean = false,
    val notificationsOn: Boolean = false,
): Parcelable
