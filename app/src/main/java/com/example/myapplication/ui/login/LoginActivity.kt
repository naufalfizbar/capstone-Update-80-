package com.example.myapplication.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.ViewModelFactory
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.ui.main.MainActivity
import com.example.myapplication.preference.UserModel
import com.example.myapplication.response.LoginResponse
import com.example.myapplication.retrofit.ApiConfig
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
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

    private fun setupAction() {
        binding.btLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            // Log the email and password used for login request
            Log.d(TAG, "Login request with email: $email, password: $password")

            lifecycleScope.launch {
                try {
                    val apiService = ApiConfig.getApiService()
                    val response = apiService.login(email, password)
                    val successResponse = response.message
                    val token = response.loginResult?.token
                    val name = response.loginResult?.name
                    val userId = response.loginResult?.userId ?: "" // Assuming userId is part of the response

                    // Log the success message from the API
                    Log.d(TAG, "Login successful: $successResponse")

                    viewModel.saveSession(UserModel(email, token.toString(), isLogin = true, name ?: "default_name", userId = userId))

                    AlertDialog.Builder(this@LoginActivity).apply {
                        setTitle("Yeah!")
                        setMessage(getString(R.string.success_login))
                        setPositiveButton(getString(R.string.next)) { _, _ ->
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }

                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
                        showToast(errorResponse.message)
                    } else {
                        showToast("Unknown error occurred")
                    }
                    // Log the exception details
                    Log.e(TAG, "Login failed: ${e.message}", e)
                } catch (e: Exception) {
                    showToast("Error: ${e.message}")
                    // Log other exceptions
                    Log.e(TAG, "Error occurred during login", e)
                }
            }
        }
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message ?: "Login failed. Please try again.", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}



//    private val viewModel by viewModels<LoginViewModel> {
//        ViewModelFactory.getInstance(this)
//    }
//    private lateinit var binding: ActivityLoginBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setupView()
//        setupAction()
//
//    }
//
//    private fun setupView() {
//        @Suppress("DEPRECATION")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.insetsController?.hide(WindowInsets.Type.statusBars())
//        } else {
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//            )
//        }
//        supportActionBar?.hide()
//    }
//
//    private fun setupAction() {
//        binding.btLogin.setOnClickListener {
//            //showLoading(true)
//            val email = binding.edtEmail.text.toString()
//            val password = binding.edtPassword.text.toString()
//
//            lifecycleScope.launch {
//                try {
//                    val apiService = ApiConfig.getApiService()
//                    val successResponse = apiService.login(email, password).message
//                    val token = apiService.login(email, password).loginResult?.token
//                    showToast(successResponse)
//
//                    viewModel.saveSession(UserModel(email, token.toString(), isLogin = true, name = "default_name"))
//
////                    showLoading(false)
//                    AlertDialog.Builder(this@LoginActivity).apply {
//                        setTitle("Yeah!")
//                        setMessage(getString(R.string.success_login))
//                        setPositiveButton(getString(R.string.next)) { _, _ ->
//                            val intent = Intent(context, MainActivity::class.java)
//                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                            startActivity(intent)
//                            finish()
//                        }
//                        create()
//                        show()
//                    }
//
//                } catch (e: retrofit2.HttpException) {
//                    val errorBody = e.response()?.errorBody()?.string()
//                    if (!errorBody.isNullOrEmpty()) {
//                        val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
//                        showToast(errorResponse.message)
//                    } else {
//                        showToast("Unknown error occurred")
//                    }
//                }
//            }
//
//        }
//    }
//
//    private fun showToast(message: String?) {
//        Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show()
//    }
//
//
//}