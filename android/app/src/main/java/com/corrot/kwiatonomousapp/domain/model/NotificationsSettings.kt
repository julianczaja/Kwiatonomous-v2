package com.corrot.kwiatonomousapp.domain.model

import java.time.LocalTime

data class NotificationsSettings(
    val notificationsOn: Boolean = true,
    val notificationsTime: LocalTime = LocalTime.of(12, 0),
)