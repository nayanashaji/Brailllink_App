package com.example.braillink

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.util.*

class BluetoothClient {

    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var socket: BluetoothSocket? = null

    private val SPP_UUID: UUID =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private var isConnected = false

    fun connect(
        deviceName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Thread {
            try {
                val device: BluetoothDevice? = adapter?.bondedDevices?.find {
                    it.name == deviceName
                }

                if (device == null) {
                    onError("Device not found. Pair in settings first.")
                    return@Thread
                }

                socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
                adapter?.cancelDiscovery()
                socket?.connect()
                isConnected = true
                onSuccess()
            } catch (e: IOException) {
                onError("Connection failed: ${e.message}")
            }
        }.start()
    }

    fun send(text: String) {
        Thread {
            try {
                socket?.outputStream?.write(text.toByteArray())
            } catch (_: IOException) {}
        }.start()
    }

    fun close() {
        try { socket?.close()
            isConnected = false } catch (_: IOException) {}
    }

    fun isConnected(): Boolean {
        return isConnected
    }
}
