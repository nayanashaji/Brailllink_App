package com.example.braillink

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

// Compose basics
import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

// Compose Material
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text

// UI utilities
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Time formatting
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Coroutines
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val btClient = BluetoothClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request runtime permissions for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), 1
            )
        } else {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), 1
            )
        }

        setContent {
            BraillinkControlScreen(btClient)
        }
    }
}

@Composable
fun BraillinkControlScreen(btClient: BluetoothClient) {
    var status by remember { mutableStateOf("Idle") }
    val logs = remember { mutableStateListOf<String>() }
    var lastSent by remember { mutableStateOf<String>("") }

    val scope = rememberCoroutineScope()
    var sendJob by remember { mutableStateOf<Job?>(null) }

    fun appendLog(s: String) {
        pushLog(logs, s)
    }

    val charDelayMs = 300L

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Connect Button
        Button(
            onClick = {
                appendLog("Attempting Bluetooth connect...")
                status = "Connecting..."
                btClient.connect(
                    deviceName = "BrailleLink_ESP32",
                    onSuccess = {
                        status = "Connected"
                        appendLog("Connected to BrailleLink_ESP32")
                    },
                    onError = {
                        status = "Error"
                        appendLog("Connection error: $it")
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1565C0))
        ) {
            Text("CONNECT BLUETOOTH", color = Color.White, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Status
        Text(text = "Status: $status", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(12.dp))

        // Capture and Stop buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    if (status != "Connected") {
                        appendLog("Error: Not connected over Bluetooth")
                        return@Button
                    }
                    if (sendJob != null) {
                        appendLog("Already sending — press STOP to cancel")
                        return@Button
                    }
                    status = "Capturing..."
                    appendLog("Capture pressed")

                    sendJob = scope.launch {
                        status = "Sending..."
                        val textToSend = ScreenTextAccessibilityService.getLatestText()
                        if (textToSend.isBlank()) {
                            appendLog("No text found on screen.")
                            status = "Idle"
                            sendJob = null
                            return@launch
                        }
                        appendLog("Captured: \"$textToSend\"")

                        for (ch in textToSend) {
                            if (!isActive) break
                            val s = ch.toString()
                            appendLog("Sending '$s'")
                            btClient.send(s)
                            lastSent = s
                            delay(charDelayMs)
                        }

                        appendLog("Finished sending")
                        status = "Idle"
                        sendJob = null
                    }
                },
                modifier = Modifier.weight(1f).height(60.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF2E7D32))
            ) {
                Text("CAPTURE", color = Color.White, fontSize = 18.sp)
            }

            Button(
                onClick = {
                    appendLog("Stop pressed")
                    sendJob?.cancel()
                    sendJob = null
                    status = "Stopped"
                    lastSent = ""
                },
                modifier = Modifier.weight(1f).height(60.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFC62828))
            ) {
                Text("STOP", color = Color.White, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Last sent: ${if (lastSent.isEmpty()) "—" else lastSent}", fontSize = 14.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Text("Activity Log:", fontSize = 16.sp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(color = Color(0xFFF5F5F5))
                .padding(8.dp)
        ) {
            if (logs.isEmpty()) {
                Text("No activity yet", color = Color.Gray)
            } else {
                LazyColumn {
                    items(logs) { item ->
                        Text(item, fontSize = 14.sp, modifier = Modifier.padding(vertical = 6.dp))
                    }
                }
            }
        }
    }
}

fun pushLog(logs: MutableList<String>, s: String) {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val time = sdf.format(Date())
    logs.add(0, "$time — $s")
}
