package com.test.mande.activity.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.test.mande.R
import com.test.mande.activity.home.viewModel.HomeActivityVM
import com.test.mande.data.api.service.Service
import com.test.mande.data.database.repo.impl.general.RepoImpl
import com.test.mande.databinding.ActivityHomeBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HomeActivity : AppCompatActivity() {

    companion object {
        private const val BASE_URL = "http://192.168.0.87:5000"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        lateinit var predominantEmotion: String
    }

    private lateinit var binding: ActivityHomeBinding
    private val viewModel = HomeActivityVM(
        RepoImpl(
            Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Service::class.java)
        )
    )
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        } else {
            startCamera()
        }

        binding.detectEmotionBtn.setOnClickListener {
            takePhotoAndGetDetectionResult()
        }

        binding.retryBtn.setOnClickListener {
            manageRetryAction()
        }

        binding.getSongBtn.setOnClickListener() {
            getSong()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(binding.root.context, R.string.home_grantCameraPermission, Toast.LENGTH_LONG).show()
                finishAffinity()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_FRONT_CAMERA, preview, imageCapture)
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhotoAndGetDetectionResult() {
        binding.detectEmotionBtn.isEnabled = false
        binding.emotionResult.text = getString(R.string.home_pleaseWait)
        binding.viewFinder.visibility = View.GONE
        binding.previewImage.visibility = View.VISIBLE
        binding.previewImage.setImageBitmap(binding.viewFinder.bitmap)

        imageCapture.takePicture(ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {

                val start = System.currentTimeMillis()
                val buffer = image.planes[0].buffer
                val byteArray = ByteArray(buffer.remaining())
                buffer.get(byteArray)

                val initialOptions = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, initialOptions)
                var scale = 1
                while (initialOptions.outWidth / scale >= 1024 && initialOptions.outHeight / scale >= 1024) {
                    scale += 1
                }
                val options = BitmapFactory.Options().apply {
                    inSampleSize = scale
                }
                var result = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
                val matrix = Matrix()
                matrix.postRotate(270f)
                result = Bitmap.createBitmap(result, 0, 0, result.width, result.height, matrix, true)
                val stream = ByteArrayOutputStream()
                result.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                val resizedImage = stream.toByteArray()
                val base64 = Base64.encodeToString(resizedImage, Base64.NO_WRAP)
                viewModel.getDetectionResult(base64, binding.emotionResult, binding.retryBtn, binding.getSongBtn, start, binding.detectEmotionBtn)

                super.onCaptureSuccess(image)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    @SuppressLint("SetTextI18n")
    private fun manageRetryAction() {
        binding.emotionResult.text = getString(R.string.home_scanFace)
        binding.retryBtn.visibility = View.GONE
        binding.retryBtn.isEnabled = false
        binding.getSongBtn.visibility = View.GONE
        binding.getSongBtn.isEnabled = false
        binding.previewImage.visibility = View.GONE
        binding.viewFinder.visibility = View.VISIBLE

        startCamera()

        binding.detectEmotionBtn.visibility = View.VISIBLE
        binding.detectEmotionBtn.isEnabled = true
    }

    private fun getSong() {
        binding.getSongBtn.isEnabled = false
        viewModel.getSongResult(predominantEmotion, binding.emotionResult, binding.getSongBtn)
    }

}
