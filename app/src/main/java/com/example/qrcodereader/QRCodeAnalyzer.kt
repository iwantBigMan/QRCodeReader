package com.example.qrcodereader

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class QRCodeAnalyzer(val onDectectListener: OnDectectListener) : ImageAnalysis.Analyzer {

    // 바코드 스캐닝 객체 생성
    private val scanner = BarcodeScanning.getClient()

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null){
            val image =
                InputImage.fromMediaImage(mediaImage, imageProxy.
                imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { qrCodes ->
                    // 리스너가 들어갈 자리
                    for (qrCode in qrCodes){
                        onDectectListener.onDetect(qrCode.rawValue?: "")
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}