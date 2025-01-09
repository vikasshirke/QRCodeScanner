package com.dtt.qrcodereader

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class HomeActivity : AppCompatActivity() {

    private val requestCode = 1001
    private val cameraPermissionRequestCode = 1002

    private lateinit var btnOpenScanner: Button
    private lateinit var etResult: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        this.enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        btnOpenScanner = findViewById(R.id.btnOpenScanner)
        etResult = findViewById(R.id.etResult)

        btnOpenScanner.setOnClickListener {
            if (hasCameraPermission()) {
                openQRCodeScanner()
            } else {
                requestCameraPermission()
            }
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            cameraPermissionRequestCode
        )
    }

    private fun openQRCodeScanner() {
        val intent = Intent(this, QRCodeScannerActivity::class.java)
        startActivityForResult(intent, requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openQRCodeScanner()
            } else {
                etResult.setText(getString(R.string.camera_permission_denied_cannot_open_scanner))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == this.requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                val qrCodeResult = data?.getStringExtra("QR_CODE_RESULT")
                etResult.setText(qrCodeResult)
                etResult.isClickable = false
                etResult.isFocusable = true
                etResult.isFocusableInTouchMode = true
            } else {
                etResult.setText(getString(R.string.qr_code_scanning_failed_or_canceled))
            }
        }
    }


    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                super.onBackPressed()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }

}
