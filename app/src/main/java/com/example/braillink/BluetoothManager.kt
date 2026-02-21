package com.example.braillink

object BluetoothManager {

    private var client: BluetoothClient? = null

    fun initialize(btClient: BluetoothClient) {
        client = btClient
    }

    fun send(text: String) {
        client?.send(text)
    }
}