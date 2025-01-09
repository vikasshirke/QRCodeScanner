package com.dtt.qrcodereader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Size
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QRCodeScannerActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var cameraExecutor: ExecutorService
    private var isQrCodeDetected = false
    private var cameraControl: CameraControl? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        this.enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_scanner)

        previewView = findViewById(R.id.previewView)

        cameraExecutor = Executors.newSingleThreadExecutor()

        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalyzer.setAnalyzer(cameraExecutor) { imageProxy ->
                if (!isQrCodeDetected) {
                    processImageProxy(imageProxy)
                } else {
                    imageProxy.close()
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)

            cameraControl = camera.cameraControl

            setContinuousFocus()

        }, ContextCompat.getMainExecutor(this))
    }

    private fun setContinuousFocus() {

        val focusPointX = previewView.width / 2f
        val focusPointY = previewView.height / 2f

        val meteringPointFactory = previewView.meteringPointFactory

        val meteringPoint = meteringPointFactory.createPoint(focusPointX, focusPointY)

        val action = FocusMeteringAction.Builder(meteringPoint)
            .setAutoCancelDuration(3, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        cameraControl?.startFocusAndMetering(action)
    }

    private fun processImageProxy(imageProxy: ImageProxy) {
        val buffer: ByteBuffer = imageProxy.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val source = PlanarYUVLuminanceSource(
            bytes,
            imageProxy.width,
            imageProxy.height,
            0,
            0,
            imageProxy.width,
            imageProxy.height,
            false
        )

        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        try {
            val result = MultiFormatReader().decode(binaryBitmap)

            runOnUiThread {
                isQrCodeDetected = true
                val resultIntent = Intent().apply {
                    putExtra("QR_CODE_RESULT", result.text)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        } catch (e: NotFoundException) {

        } catch (e: Exception) {
            runOnUiThread {
            }
            setResult(Activity.RESULT_CANCELED)
            finish()
        } finally {
            imageProxy.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
