package com.dicoding.storyappsub1.camera

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
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
import com.dicoding.storyappsub1.main.MainActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.util.*

class MainCamera : AppCompatActivity(), View.OnClickListener {
    private lateinit var mainCameraBinding: ActivityMainCameraBinding
    private val mainCameraViewModel: MainCameraViewModel by viewModels()

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
        var file = createTempFile(this)

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

            mainCameraViewModel.setImage(image)

            mainCameraViewModel.fileImage.observe(this) { file ->
                mainCameraViewModel.fileBitmap.observe(this) { bit ->
                    mainCameraViewModel.setBitmap(result)
                    mainCameraViewModel.setImage(image)
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

            mainCameraViewModel.setImage(myFile)

            mainCameraViewModel.fileImage.observe(this) {
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
                    "Tidak mendapatkan permission.",
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

        mainCameraViewModel.fileImage.observe(this) {
            mainCameraBinding.previewImageView.setImageURI(it.toUri())
        }

        mainCameraViewModel.fileBitmap.observe(this) {
            mainCameraBinding.previewImageView.setImageBitmap(it)
        }

        mainCameraBinding.btnGallery.setOnClickListener { startGallery() }
        mainCameraBinding.btnCamera.setOnClickListener { startCameraX() }
        mainCameraBinding.btnUpload.setOnClickListener { uploadImage() }

        mainCameraViewModel.mediator.observe(this) {}
        mainCameraViewModel.isRegistred.observe(this) { regist ->
            if (regist == false) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        mainCameraViewModel.messageRegistred.observe(this) { mesg ->
            if (mesg == null) {
                showToast(getString(R.string.error_add_story))
            } else {
                showToast(mesg)
            }
        }

        mainCameraViewModel.isLoading.observe(this) { loading ->
            showLoading(loading)
        }
    }

    private fun uploadImage() {
        mainCameraViewModel.fileImage.observe(this) { file ->
            if (file != null) {
                reduceFileImage(file)
                val description = mainCameraBinding.etDescription.text.toString()
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
                        mainCameraViewModel.user.observe(this) { token ->
                            if (token != null) {
                                mainCameraViewModel.uploadImage(
                                    imageMultipart = imageMultipart,
                                    description = descriptionFile,
                                    token = token
                                )
                            }
                        }
                    }
                }
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

    override fun onClick(v: View?) {

    }

}