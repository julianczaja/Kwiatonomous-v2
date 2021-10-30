package com.corrot.utils

import org.joda.time.DateTime

object TimeUtils {

    fun getCurrentTimestamp(): Long = DateTime.now().millis / 1000
}