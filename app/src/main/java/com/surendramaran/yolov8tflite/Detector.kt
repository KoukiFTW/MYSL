package com.surendramaran.yolov8tflite

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Collections.synchronizedList

class Detector(
    private val context: Context,
    private val modelPath: String,
    private val labelPath: String,
    private val detectorListener: DetectorListener
) {
    private var isClosed = false
    private val lock = Any()
    private val TAG = "Detector"

    private var interpreter: Interpreter? = null
    private var labels = synchronizedList(mutableListOf<String>())

    private var tensorWidth = 0
    private var tensorHeight = 0
    private var numChannel = 0
    private var numElements = 0

    private val imageProcessor = ImageProcessor.Builder()
        .add(NormalizeOp(INPUT_MEAN, INPUT_STANDARD_DEVIATION))
        .add(CastOp(INPUT_IMAGE_TYPE))
        .build()

    fun setup() {
        synchronized(lock) {
            if (isClosed) {
                Log.w(TAG, "Setup called after detector closed")
                return
            }

            try {
                val model = FileUtil.loadMappedFile(context, modelPath)
                val options = Interpreter.Options().apply {
                    numThreads = 4
                }
                interpreter = Interpreter(model, options)

                val inputShape = interpreter?.getInputTensor(0)?.shape() ?: run {
                    Log.e(TAG, "Failed to get input tensor shape")
                    return
                }
                val outputShape = interpreter?.getOutputTensor(0)?.shape() ?: run {
                    Log.e(TAG, "Failed to get output tensor shape")
                    return
                }

                tensorWidth = inputShape[1]
                tensorHeight = inputShape[2]
                numChannel = outputShape[1]
                numElements = outputShape[2]

                loadLabels()
            } catch (e: Exception) {
                Log.e(TAG, "Setup failed: ${e.message}")
                clear()
            }
        }
    }

    private fun loadLabels() {
        try {
            context.assets.open(labelPath).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        line?.let { labels.add(it) }
                    }
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error loading labels: ${e.message}")
        }
    }

    fun clear() {
        synchronized(lock) {
            try {
                interpreter?.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing interpreter: ${e.message}")
            }
            interpreter = null
            isClosed = true
            Log.d(TAG, "Detector cleared and closed")
        }
    }

    fun detect(frame: Bitmap) {
        synchronized(lock) {
            if (isClosed || interpreter == null) {
                Log.d(TAG, "Skipping detection - detector closed or not initialized")
                detectorListener.onEmptyDetect()
                return
            }

            if (tensorWidth == 0 || tensorHeight == 0 || numChannel == 0 || numElements == 0) {
                Log.e(TAG, "Invalid tensor dimensions")
                detectorListener.onEmptyDetect()
                return
            }

            try {
                val inferenceStart = SystemClock.uptimeMillis()
                val resizedBitmap = Bitmap.createScaledBitmap(frame, tensorWidth, tensorHeight, false)
                val tensorImage = TensorImage(DataType.FLOAT32).apply { load(resizedBitmap) }
                val processedImage = imageProcessor.process(tensorImage)

                val output = TensorBuffer.createFixedSize(
                    intArrayOf(1, numChannel, numElements),
                    OUTPUT_IMAGE_TYPE
                )

                // =========== SAFE INTERPRETER USAGE ===========
                interpreter?.run(processedImage.buffer, output.buffer.rewind())

                val bestBoxes = processOutput(output.floatArray)
                val inferenceTime = SystemClock.uptimeMillis() - inferenceStart

                bestBoxes?.let {
                    detectorListener.onDetect(it, inferenceTime)
                } ?: run {
                    detectorListener.onEmptyDetect()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Detection error: ${e.message}")
                detectorListener.onEmptyDetect()
            }
        }
    }

    private fun processOutput(array: FloatArray): List<BoundingBox>? {
        val boundingBoxes = mutableListOf<BoundingBox>()

        for (c in 0 until numElements) {
            var maxConf = -1.0f
            var maxIdx = -1
            var j = 4
            var arrayIdx = c + numElements * j

            while (j < numChannel) {
                if (array[arrayIdx] > maxConf) {
                    maxConf = array[arrayIdx]
                    maxIdx = j - 4
                }
                j++
                arrayIdx += numElements
            }

            if (maxConf > CONFIDENCE_THRESHOLD && maxIdx in labels.indices) {
                val cx = array[c]
                val cy = array[c + numElements]
                val w = array[c + numElements * 2]
                val h = array[c + numElements * 3]

                val x1 = (cx - w/2f).coerceIn(0f, 1f)
                val y1 = (cy - h/2f).coerceIn(0f, 1f)
                val x2 = (cx + w/2f).coerceIn(0f, 1f)
                val y2 = (cy + h/2f).coerceIn(0f, 1f)

                boundingBoxes.add(
                    BoundingBox(
                        x1 = x1, y1 = y1, x2 = x2, y2 = y2,
                        cx = cx, cy = cy, w = w, h = h,
                        cnf = maxConf,
                        cls = maxIdx,
                        clsName = labels.getOrElse(maxIdx) { "Unknown" }
                    )
                )
            }
        }

        return if (boundingBoxes.isEmpty()) null else applyNMS(boundingBoxes)
    }

    private fun applyNMS(boxes: List<BoundingBox>): MutableList<BoundingBox> {
        val sortedBoxes = boxes.sortedByDescending { it.cnf }.toMutableList()
        val selectedBoxes = mutableListOf<BoundingBox>()

        while (sortedBoxes.isNotEmpty()) {
            val first = sortedBoxes.removeAt(0)
            selectedBoxes.add(first)

            val iterator = sortedBoxes.iterator()
            while (iterator.hasNext()) {
                val nextBox = iterator.next()
                if (calculateIoU(first, nextBox) >= IOU_THRESHOLD) {
                    iterator.remove()
                }
            }
        }

        return selectedBoxes
    }

    private fun calculateIoU(box1: BoundingBox, box2: BoundingBox): Float {
        val x1 = maxOf(box1.x1, box2.x1)
        val y1 = maxOf(box1.y1, box2.y1)
        val x2 = minOf(box1.x2, box2.x2)
        val y2 = minOf(box1.y2, box2.y2)

        val intersection = maxOf(0f, x2 - x1) * maxOf(0f, y2 - y1)
        val area1 = box1.w * box1.h
        val area2 = box2.w * box2.h

        return intersection / (area1 + area2 - intersection)
    }

    interface DetectorListener {
        fun onEmptyDetect()
        fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long)
    }

    companion object {
        private const val INPUT_MEAN = 0f
        private const val INPUT_STANDARD_DEVIATION = 255f
        private val INPUT_IMAGE_TYPE = DataType.FLOAT32
        private val OUTPUT_IMAGE_TYPE = DataType.FLOAT32
        private const val CONFIDENCE_THRESHOLD = 0.3f
        private const val IOU_THRESHOLD = 0.5f
    }
}