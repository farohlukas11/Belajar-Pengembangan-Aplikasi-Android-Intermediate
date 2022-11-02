package com.dicoding.storyappsub1.add_story

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.dicoding.storyappsub1.R
import com.dicoding.storyappsub1.databinding.ActivityCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var cameraBinding: ActivityCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var outputDirectory: File

    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(cameraBinding.root)

        hideSystemUI()

        cameraExecutor = Executors.newSingleThreadExecutor()
        outputDirectory = getOutputDirectory()

        cameraBinding.captureImage.setOnClickListener(this)
        cameraBinding.switchCamera.setOnClickListener(this)

        startCamera()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(cameraBinding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.captureImage -> {
                imageCapture.let {
                    val photoFile = File(
                        outputDirectory,
                        SimpleDateFormat(
                            FILENAME_FORMAT,
                            Locale.US
                        ).format(System.currentTimeMillis()) + ".jpg"
                    )
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    it?.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(this),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                Log.i(TAG, "The image has been saved in ${photoFile.toUri()}")
                                val savedUri = Uri.fromFile(photoFile)
                                Toast.makeText(
                                    this@CameraActivity,
                                    "succes $savedUri",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent = Intent()
                                intent.putExtra("picture", photoFile)
                                intent.putExtra(
                                    AddStoryActivity.POSITION_CAMERA,
                                    cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                                )
                                setResult(AddStoryActivity.CAMERA_X_RESULT, intent)
                                finish()
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Toast.makeText(
                                    this@CameraActivity,
                                    "Gagal mengambil gambar.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    )
                }
            }
            R.id.switchCamera -> {
                cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
                startCamera()
            }
        }
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val TAG = "camera_activity"
    }
}