package com.dicoding.storyappsub1.add_story

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.dicoding.storyappsub1.R
import com.dicoding.storyappsub1.databinding.ActivityMainCameraBinding
import com.dicoding.storyappsub1.ViewModelFactory
import com.dicoding.storyappsub1.main.MainActivity
import com.dicoding.storyappsub1.model.Result
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*

class AddStoryActivity : AppCompatActivity() {

    private lateinit var mainCameraBinding: ActivityMainCameraBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val addStoryViewModel: AddStoryViewModel by viewModels {
        factory
    }
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var myLongitude: Double? = null
    private var myLatitude: Double? = null

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        // Initialize a new file instance to save bitmap object
        val file = createTempFile(this)

        return try {
            // Compress the bitmap and save in jpg format
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            val bitmapData = bos.toByteArray()

            val fos = FileOutputStream(file)
            fos.write(bitmapData)
            fos.flush()
            fos.close()
            file
        } catch (e: IOException) {
            e.printStackTrace()
            file
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra(POSITION_CAMERA, true) as Boolean

            val result =
                if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    rotateBitmapLandscape(
                        BitmapFactory.decodeFile(myFile.path),
                        isBackCamera
                    )
                } else {
                    rotateBitmap(
                        BitmapFactory.decodeFile(myFile.path),
                        isBackCamera
                    )
                }


            val image = bitmapToFile(result)

            addStoryViewModel.setImage(image)

            addStoryViewModel.fileImage.observe(this) { file ->
                addStoryViewModel.fileBitmap.observe(this) { bit ->
                    addStoryViewModel.setBitmap(result)
                    addStoryViewModel.setImage(image)
                    mainCameraBinding.previewImageView.setImageBitmap(bit)
                    mainCameraBinding.previewImageView.setImageURI(file.toUri())
                }
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)

            addStoryViewModel.setImage(myFile)

            addStoryViewModel.fileImage.observe(this) {
                mainCameraBinding.previewImageView.setImageURI(it.toUri())
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.error_get_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainCameraBinding = ActivityMainCameraBinding.inflate(layoutInflater)
        setContentView(mainCameraBinding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        hideSystemUI()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        addStoryViewModel.fileImage.observe(this) {
            mainCameraBinding.previewImageView.setImageURI(it.toUri())
        }

        addStoryViewModel.fileBitmap.observe(this) {
            mainCameraBinding.previewImageView.setImageBitmap(it)
        }

        mainCameraBinding.btnGallery.setOnClickListener { startGallery() }
        mainCameraBinding.btnCamera.setOnClickListener { startCameraX() }
        mainCameraBinding.btnUpload.setOnClickListener { uploadImage() }
        mainCameraBinding.btnAddPosition.setOnClickListener { addPosition() }
    }

    private fun uploadImage() {
        addStoryViewModel.fileImage.observe(this) { file ->
            if (file != null) {
                reduceFileImage(file)
                val description = mainCameraBinding.etDescription.text.toString()
                val lat = myLatitude ?: 0.0
                val lon = myLongitude ?: 0.0
                when {
                    description.isEmpty() -> mainCameraBinding.etDescription.error =
                        getString(R.string.description_error)
                    else -> {
                        reduceFileImage(file)
                        val descriptionFile =
                            description.toRequestBody("text/plain".toMediaType())
                        val requestImageFile =
                            file.asRequestBody("image/jpeg".toMediaType())
                        val imageMultipart: MultipartBody.Part =
                            MultipartBody.Part.createFormData(
                                "photo",
                                file.name,
                                requestImageFile
                            )
                        addStoryViewModel.mediator.observe(this) {}
                        addStoryViewModel.user.observe(this) { token ->
                            if (token != null) {
                                addStoryViewModel.uploadImage(
                                    imageMultipart = imageMultipart,
                                    description = descriptionFile,
                                    token = token,
                                    lat = lat.toFloat(),
                                    lon = lon.toFloat()
                                ).observe(this) { result ->
                                    if (result != null) {
                                        when (result) {
                                            is Result.Loading -> showLoading(true)
                                            is Result.Success -> {
                                                showLoading(false)
                                                val cameraData = result.data

                                                if (!cameraData.error) {
                                                    showLoading(false)
                                                    showToast(cameraData.message)

                                                    startActivity(
                                                        Intent(
                                                            this,
                                                            MainActivity::class.java
                                                        )
                                                    )
                                                    finish()
                                                }
                                            }
                                            is Result.Error -> {
                                                showLoading(false)
                                                showToast(getString(R.string.failed_upload))
                                            }
                                        }
                                    } else {
                                        showToast(getString(R.string.error_offline))
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                showToast(getString(R.string.errror_file_not_found))
            }
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun addPosition() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getMyLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location == null) {
                showToast(getString(R.string.errror_get_location))
            } else location.apply {
                myLatitude = latitude
                myLongitude = longitude
                showToast(getString(R.string.success_add_location))
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            mainCameraBinding.progressBar.visibility = View.VISIBLE
        } else {
            mainCameraBinding.progressBar.visibility = View.INVISIBLE
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        const val POSITION_CAMERA = "is_back"
    }
}