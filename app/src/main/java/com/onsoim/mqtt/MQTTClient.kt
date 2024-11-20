package com.onsoim.mqtt

import android.util.Log
import org.eclipse.paho.client.mqttv3.*

class MQTTClient(private val context: android.content.Context) {

    private val TAG = "MQTTClient"
    private var mqttClient: MqttClient? = null
    private var mqttOptions: MqttConnectOptions? = null

    // The MQTT broker address
    private val brokerUrl = "tcp://broker.hivemq.com:1883"
    private val clientId = "AndroidClient" // Unique ID for the client
    private val topic = "test/android" // Topic to subscribe and publish to

    // Callback for receiving messages
    private var messageCallback: MqttCallback? = null

    init {
        initializeClient()
    }

    private fun initializeClient() {
        try {
            // Create MQTT Client
            mqttClient = MqttClient(brokerUrl, clientId, null)

            // Set connection options
            mqttOptions = MqttConnectOptions().apply {
                isCleanSession = true
                // Optional: Set username and password
                // userName = "your-username"
                // password = "your-password".toCharArray()
            }

            // Set callback to handle messages and connection events
            mqttClient?.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    Log.e(TAG, "Connection lost: ${cause?.message}")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    Log.d(TAG, "Message received: ${String(message?.payload ?: byteArrayOf())}")
                    // Handle the received message (e.g., update UI)
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d(TAG, "Message delivery complete")
                }
            })

        } catch (e: MqttException) {
            Log.e(TAG, "Error initializing MQTT client: ${e.message}")
        }
    }

    // Connect to MQTT broker
    fun connect() {
        try {
            mqttClient?.connect(mqttOptions)
            Log.d(TAG, "Connected to broker")
        } catch (e: MqttException) {
            Log.e(TAG, "Connection failed: ${e.message}")
        }
    }

    // Subscribe to a topic
    fun subscribe() {
        try {
            mqttClient?.subscribe(topic, 1)
            Log.d(TAG, "Subscribed to topic: $topic")
        } catch (e: MqttException) {
            Log.e(TAG, "Subscription failed: ${e.message}")
        }
    }

    // Publish a message
    fun publish(message: String) {
        try {
            val mqttMessage = MqttMessage(message.toByteArray())
            mqttMessage.qos = 1
            mqttClient?.publish(topic, mqttMessage)
            Log.d(TAG, "Message published: $message")
        } catch (e: MqttException) {
            Log.e(TAG, "Publish failed: ${e.message}")
        }
    }

    // Disconnect from the broker
    fun disconnect() {
        try {
            mqttClient?.disconnect()
            Log.d(TAG, "Disconnected from broker")
        } catch (e: MqttException) {
            Log.e(TAG, "Disconnect failed: ${e.message}")
        }
    }
}