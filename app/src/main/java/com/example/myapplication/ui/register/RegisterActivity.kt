package com.example.myapplication.ui.register

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityRegisterBinding
import com.example.myapplication.response.RegisterResponse
import com.example.myapplication.retrofit.ApiConfig
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.btSignup.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val name = binding.edtUsername.text.toString()
            val password = binding.edtPassword.text.toString()

            if (email.isBlank() || name.isBlank() || password.isBlank()) {
                showToast("Please fill in all fields")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val apiService = ApiConfig.getApiService()
                    val successResponse = apiService.register(name, email, password).message
                    showToast(successResponse.toString())
                    showLoading(true)
                    AlertDialog.Builder(this@RegisterActivity).apply {
                        setTitle("Yeah!")
                        setMessage("Please Login")
                        setPositiveButton(getString(R.string.next)) { _, _ ->
                            finish()
                        }
                        create()
                        show()
                    }

                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
                    val errorMessage = parseErrorMessage(errorResponse.message)
                    showToast(errorMessage)
                    showLoading(false)

                }
            }
        }
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun parseErrorMessage(message: Any?): String {
        return if (message is Map<*, *>) {
            val emailErrors = message["email"] as? List<*>
            emailErrors?.joinToString(", ") ?: "Unknown error"
        } else {
            message?.toString() ?: "Unknown error"
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
