package com.whodaparking.app.ocr

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class OcrAnalyzer(
    private val onTextDetected: (List<String>) -> Unit
) : ImageAnalysis.Analyzer {
    
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            
            textRecognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val detectedTexts = visionText.textBlocks
                        .flatMap { it.lines }
                        .map { it.text.trim() }
                        .filter { it.isNotBlank() }
                    
                    onTextDetected(detectedTexts)
                }
                .addOnFailureListener { exception ->
                    Timber.e(exception, "Text recognition failed")
                    onTextDetected(emptyList())
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
    
    /**
     * Process a bitmap image for text recognition
     */
    suspend fun processImage(bitmap: Bitmap): Result<List<String>> {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val visionText = textRecognizer.process(image).await()
            
            val detectedTexts = visionText.textBlocks
                .flatMap { it.lines }
                .map { it.text.trim() }
                .filter { it.isNotBlank() }
            
            Timber.d("OCR detected ${detectedTexts.size} text blocks: $detectedTexts")
            Result.success(detectedTexts)
        } catch (e: Exception) {
            Timber.e(e, "OCR processing failed")
            Result.failure(e)
        }
    }
    
    fun release() {
        textRecognizer.close()
    }
}