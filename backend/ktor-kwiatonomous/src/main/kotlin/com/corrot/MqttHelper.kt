package com.corrot

import com.corrot.Constants.MQTT_BROKER_URL
import com.corrot.Constants.MQTT_LOGIN
import com.corrot.Constants.MQTT_PASSWORD
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage

object MqttHelper {

    private val client = MqttClient(MQTT_BROKER_URL, MqttClient.generateClientId())
    private val options = MqttConnectOptions().apply {
        userName = MQTT_LOGIN
        password = MQTT_PASSWORD.toCharArray()
    }

    init {
        connect()
    }

    private fun connect() {
        if (!client.isConnected) {
            client.connect(options)
        }
    }

    fun publish(topic: String, payload: String) {
        try {
            if (!client.isConnected) {
                connect()
            }
            client.publish(topic, MqttMessage(payload.toByteArray()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
