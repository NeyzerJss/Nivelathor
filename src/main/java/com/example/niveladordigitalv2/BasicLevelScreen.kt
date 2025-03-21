// BasicLevelScreen.kt
package com.example.niveladordigitalv2

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlin.math.abs

@Composable
fun BasicLevelScreen(navController: NavController) {
    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var x by remember { mutableStateOf(0f) }
    var y by remember { mutableStateOf(0f) }

    val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                x = it.values[0]
                y = it.values[1]
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    DisposableEffect(Unit) {
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    val animatedX by animateFloatAsState(targetValue = x)
    val animatedY by animateFloatAsState(targetValue = y)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Nivelador Básico", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Inclinación X: %.3f°".format(animatedX), style = MaterialTheme.typography.bodyLarge)
        Text(text = "Inclinación Y: %.3f°".format(animatedY), style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(20.dp))
        BubbleLevel(x = animatedX, y = animatedY)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { navController.navigate("menu") }) {
            Text("Volver al Menú")
        }
    }
}

@Composable
fun BubbleLevel(x: Float, y: Float) {
    Canvas(modifier = Modifier.size(200.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val bubbleRadius = 20.dp.toPx()
        val maxOffset = size.width / 2 - bubbleRadius

        val bubbleX = centerX + (x / 10) * maxOffset
        val bubbleY = centerY - (y / 10) * maxOffset

        val isCentered = abs(bubbleX - centerX) < bubbleRadius && abs(bubbleY - centerY) < bubbleRadius
        val bubbleColor = if (isCentered) Color.Green  else Color.Blue

        drawRoundRect(
            color = Color.Gray,
            topLeft = Offset(0f, 0f),
            size = size,
            cornerRadius = CornerRadius(16.dp.toPx())
        )

        drawCircle(
            color = bubbleColor,
            radius = bubbleRadius,
            center = Offset(bubbleX.coerceIn(bubbleRadius, size.width - bubbleRadius), bubbleY.coerceIn(bubbleRadius, size.height - bubbleRadius))
        )
    }
}