package com.corrot

object Constants {
    const val DEBUG_MODE = false

    const val DEFAULT_SLEEP_TIME_MINUTES = 30
    const val DEFAULT_TIME_ZONE_OFFSET = 1
    const val DEFAULT_WATERING_ON = false
    const val DEFAULT_WATERING_INTERVAL_DAYS = 2
    const val DEFAULT_WATERING_AMOUNT = 50
    const val DEFAULT_WATERING_TIME = "12:00"

    const val KWIATONOMOUS_DIGEST_AUTH = "KwiatonomousDigestAuth"
    const val KWIATONOMOUS_REALM = "KwiatonomousRealm"

    const val BASE_URL = "192.168.1.76"
    const val PORT = 8124

    const val MQTT_BROKER_URL = "tcp://192.168.1.120:1883"
    const val MQTT_TOPIC_UPDATES = "kwiatonomous/updates/"
    const val MQTT_LOGIN = "mqttuser"
    const val MQTT_PASSWORD = "mqttpassword"

    val DB_FILE_PATH = "${System.getProperty("user.home")}/Desktop/kwiatonomous"
    const val DB_FILE_NAME = "kwiatonomous.sqlite"
}