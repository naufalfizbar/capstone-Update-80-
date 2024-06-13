package com.example.myapplication.ui.profil

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityEditBinding
import com.example.myapplication.response.ErrorResponse
import com.example.myapplication.response.ProfileResponse
import com.example.myapplication.retrofit.ApiConfig
import com.example.test.data.util.reduceFileImage
import com.example.test.data.util.uriToFile
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    private var currentImageUri: Uri? = null
    private var token = "token"  // You should retrieve this from a secure source

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            binding.profileImageView.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.edImgProfile.setOnClickListener {
            openGallery()
        }

        binding.button.setOnClickListener {
            saveProfile()
        }

        fetchProfileData()
    }

    private fun openGallery() {
        launcherGallery.launch("image/*")
    }

    private fun fetchProfileData() {
        val apiService = ApiConfig.getApiService()
        apiService.getProfile("Bearer $token").enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    val profile = response.body()
                    profile?.let {
                        binding.editName.setText(it.name)
                        binding.editEmail.setText(it.email)
                        if (!it.profilePicture.isNullOrEmpty()) {
                            Glide.with(this@EditActivity)
                                .load(it.profilePicture)
                                .placeholder(R.drawable.profile)
                                .into(binding.profileImageView)
                        } else {
                            binding.profileImageView.setImageResource(R.drawable.profile)
                        }
                    }
                } else {
                    Log.e("Profile Fetch", "Failed to fetch profile: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.e("Profile Fetch", "Error: ${t.message}")
            }
        })
    }

    private fun saveProfile() {
        val username = binding.editName.text.toString()
        val email = binding.editEmail.text.toString()

        val requestBodyMap = mutableMapOf<String, RequestBody>()
        requestBodyMap["username"] = username.toRequestBody("text/plain".toMediaType())
        requestBodyMap["email"] = email.toRequestBody("text/plain".toMediaType())

        var multipartBody: MultipartBody.Part? = null
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage() // Define this utility method
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            multipartBody = MultipartBody.Part.createFormData("profileImage", imageFile.name, requestImageFile)
        }

        val apiService = ApiConfig.getApiService()
        lifecycleScope.launch {
            try {
                val response = apiService.updateProfile("Bearer $token", requestBodyMap, multipartBody)
                if (response.isSuccessful) {
                    showToast("Profile updated successfully")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    showToast(errorResponse?.message ?: "Failed to update profile")
                }
            } catch (e: Exception) {
                showToast("Failed to update profile: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
