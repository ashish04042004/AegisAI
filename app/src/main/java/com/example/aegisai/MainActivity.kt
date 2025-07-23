package com.example.aegisai

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.aegisai.onnx.CrashPredictor
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var crashPredictor: CrashPredictor
    private lateinit var resultText: TextView
    private lateinit var sensorManager: SensorManager

    private var accelData = FloatArray(3)
    private var gyroData = FloatArray(3)
    private var isAccelReady = false
    private var isGyroReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        crashPredictor = CrashPredictor(this)
        resultText = findViewById(R.id.outputText)
        val button = findViewById<Button>(R.id.generateButton)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        val gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_UI)

        button.setOnClickListener {
            if (isAccelReady && isGyroReady) {
                val inputData = floatArrayOf(
                    accelData[0], accelData[1], accelData[2],
                    gyroData[0], gyroData[1], gyroData[2]
                )

                val score = crashPredictor.predict(inputData)
                val severity = if (score > 0.5) "🚨 Crash Detected" else "✅ Normal Movement"
                resultText.text = "Score: %.3f\n$severity".format(score)
            } else {
                resultText.text = "Waiting for sensor data..."
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                System.arraycopy(event.values, 0, accelData, 0, 3)
                isAccelReady = true
            }
            Sensor.TYPE_GYROSCOPE -> {
                System.arraycopy(event.values, 0, gyroData, 0, 3)
                isGyroReady = true
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}
