package com.corrot.kwiatonomousapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PumpCleaningNotificationExtras(
    val notificationIntentType: NotificationIntentType,
    val deviceId: String,
    val notificationId: Int,
    val isTest: Boolean,
) : Parcelable