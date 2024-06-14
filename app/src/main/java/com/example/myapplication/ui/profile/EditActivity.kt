package com.example.myapplication.ui.profile


import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.lifecycleScope

import com.example.myapplication.R
import com.example.myapplication.ViewModelFactory

import com.example.myapplication.preference.UserModel
import com.example.myapplication.ui.login.LoginActivity

import kotlinx.coroutines.launch


class EditActivity : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editName: EditText
    private lateinit var saveButton: Button
    private lateinit var btnBack: ImageView

    private val viewModel: EditViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        editEmail = findViewById(R.id.editEmail)
        editName = findViewById(R.id.editName)
        saveButton = findViewById(R.id.saveButton)
        btnBack = findViewById(R.id.btnBack)

        viewModel.getSession().observe(this) { user ->
            if (user == null) {
                // Handle null user scenario, possibly redirect to login or initialization flow
                Log.e("EditActivity", "Current user is null")
                // Example: Redirect to LoginActivity or perform initialization steps
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                // Proceed with editing logic using the user object
                editEmail.setText(user.email)
                editName.setText(user.name)

                saveButton.setOnClickListener {
                    val email = editEmail.text.toString().trim()
                    val name = editName.text.toString().trim()
                    lifecycleScope.launch {
                        try {
                            viewModel.saveSession(
                                UserModel(
                                    email,
                                    user.token,
                                    true,
                                    name,
                                    user.userId
                                )
                            )
                            Log.d("EditActivity", "Save successful")
                            startActivity(Intent(this@EditActivity, ProfileFragment::class.java))
                            finish()
                            // Proceed with navigation or other actions
                        } catch (e: Exception) {
                            Log.e("EditActivity", "Failed to save session", e)
                            Toast.makeText(
                                this@EditActivity,
                                "Failed to save changes",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            btnBack.setOnClickListener {
                finish()
            }
        }
    }
}

//    override fun onBackPressed() {
//        // Navigate to ProfileFragment or ProfileActivity
//        startActivity(Intent(this@EditActivity, ProfileFragment::class.java)) // Change to the appropriate activity or fragment
//        finish()
//    }


//    private lateinit var binding: ActivityEditBinding
//
//    private val viewModel by viewModels<EditViewModel> {
//        ViewModelFactory.getInstance(this)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityEditBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val username = intent.getStringExtra("username")
//        val email = intent.getStringExtra("email")
//
//        binding.editName.setText(username)
//        binding.editEmail.setText(email)
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        binding.btnBack.setOnClickListener {
//            finish()
//        }
//
//        viewModel.getSession().observe(this) { user ->
//            if (user != null && user.isLogin) {
//                val token = user.token
//                val userId = user.userId
//
//                binding.saveButton.setOnClickListener {
//                    val newUsername = binding.editName.text.toString()
//                    val newEmail = binding.editEmail.text.toString()
//                    updateUserProfile(token, userId, newUsername, newEmail)
//                }
//            }
//        }
//    }
//
//    private fun updateUserProfile(token: String, userId: String, name: String, email: String) {
//        lifecycleScope.launch {
//            try {
//                val apiService = ApiConfig.getApiService()
//                val response = apiService.editUser("Bearer $token", userId, mapOf("name" to name, "email" to email))
//                if (response.isSuccessful) {
//                    val successResponse = response.body()?.message
//
//
//                    successResponse?.let { showToast(it) }
//
//                    if (message != null) {
//                        viewModel.saveSession(UserModel(updatedUser.email ?: "", token, true, updatedUser.name ?: "", updatedUser.userId ?: ""))
//                    }
//                    showToast(getString(R.string.success_profile_update))
//                    finish()
//                } else {
//                    val errorBody = response.errorBody()?.string()
//                    val errorResponse = Gson().fromJson(errorBody, UpdateProfileResponse::class.java)
//                    errorResponse.message?.let { showToast(it) }
//                }
//            } catch (e: HttpException) {
//                val errorBody = e.response()?.errorBody()?.string()
//                val errorResponse = Gson().fromJson(errorBody, UpdateProfileResponse::class.java)
//                errorResponse.message?.let { showToast(it) }
//            } catch (e: Exception) {
//                showToast("Error: ${e.message}")
//            }
//        }
//    }
//
//    private fun showToast(message: String) {
//        Toast.makeText(this@editProfileActivity, message, Toast.LENGTH_SHORT).show()
//    }
//}
