package com.example.myapplication.ui.add_image

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.ViewModelFactory
import com.example.myapplication.databinding.ActivityScanBinding
import com.example.myapplication.response.ErrorResponse
import com.example.myapplication.response.ScanResponse
import com.example.myapplication.retrofit.ApiConfig
import com.example.myapplication.ui.main.MainActivity
import com.example.myapplication.ui.welcome.WelcomeActivity
import com.example.test.data.util.reduceFileImage
import com.example.test.data.util.uriToFile
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding
    private val viewModel by viewModels<ScanViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var token = "token"

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Add Your Lungs"

        if (!PermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                token = user.token
                Log.d("ScanActivity", "Token: $token") // Log token untuk pengecekan
                binding.btGallery.setOnClickListener { startGallery() }
                binding.btUpload.setOnClickListener { uploadImage() }
            }
        }

        // Set up Spinner for gender
        val genderSpinner: Spinner = findViewById(R.id.spinner_gender)
        ArrayAdapter.createFromResource(
            this,
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSpinner.adapter = adapter
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun backToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Image Selector", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewparu.setImageURI(it)
        }
    }

    private fun PermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")

            val name = binding.edNamapasien.text.toString()
            val age = binding.edAge.text.toString()
            val gender = binding.spinnerGender.selectedItem.toString()

            if (name.isEmpty() || age.isEmpty() || gender.isEmpty()) {
                showToast(getString(R.string.emptyFields))
                return
            }

            showLoading(true)

            val namePart = name.toRequestBody("text/plain".toMediaType())
            val agePart = age.toRequestBody("text/plain".toMediaType())
            val genderPart = gender.toRequestBody("text/plain".toMediaType())
            val photoPart = MultipartBody.Part.createFormData(
                "photo", imageFile.name, imageFile.asRequestBody("image/jpeg".toMediaType())
            )

            lifecycleScope.launch {
                try {
                    val apiService = ApiConfig.getApiService()
                    val call = apiService.uploadImage("Bearer $token", photoPart, namePart, agePart, genderPart)

                    call.enqueue(object : Callback<ScanResponse> {
                        override fun onResponse(call: Call<ScanResponse>, response: Response<ScanResponse>) {
                            showLoading(false)
                            if (response.isSuccessful) {
                                Log.e(ContentValues.TAG, "response Success: ${response.message()}")
                                showToast("Upload Successful")
                                response.body()?.let { result ->
                                    displayResult(result.message)
                                }
                            } else {
                                val errorBody = response.errorBody()?.string()
                                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                                showToast(errorResponse.message.toString())
                                Log.e(ContentValues.TAG, "add response gagal: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<ScanResponse>, t: Throwable) {
                            showLoading(false)
                            Log.e(ContentValues.TAG, "upload gagal: ${t.message.toString()}")
                            showToast("Upload failed: ${t.message.toString()}")
                        }
                    })
                } catch (e: HttpException) {
                    showLoading(false)
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    showToast(errorResponse.message.toString())
                }
            }
        } ?: showToast(getString(R.string.emptyImage))
    }

    private fun displayResult(result: String?) {
        result?.let {
            binding.tvResult.text = it
            binding.tvResult.visibility = View.VISIBLE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
