package com.corrot.kwiatonomousapp.domain.model

import java.time.LocalDateTime

data class Device(
    val id: String,
    val birthday: LocalDateTime,
    val lastUpdate: LocalDateTime,
)