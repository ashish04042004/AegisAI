package com.example.aegisai.onnx

import android.content.Context
import ai.onnxruntime.*
import java.io.InputStream
import java.nio.FloatBuffer

class CrashPredictor(context: Context) {
    private val ortEnv: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val ortSession: OrtSession

    init {
        val modelStream: InputStream = context.assets.open("crash_model.onnx")
        val modelBytes = modelStream.readBytes()
        modelStream.close()

        val sessionOptions = OrtSession.SessionOptions()
        ortSession = ortEnv.createSession(modelBytes, sessionOptions)
    }

    fun predict(inputData: FloatArray): Float {
        require(inputData.size == 6) { "Model expects 6 features (accel+gyro)." }

        val inputName = ortSession.inputNames.iterator().next()
        val inputShape = longArrayOf(1, 6)
        val inputTensor = OnnxTensor.createTensor(ortEnv, FloatBuffer.wrap(inputData), inputShape)

        val result = ortSession.run(mapOf(inputName to inputTensor))
        val outputArray = (result[0].value as Array<FloatArray>)[0]
        return outputArray[0] // Probability of crash (0–1)
    }
}
