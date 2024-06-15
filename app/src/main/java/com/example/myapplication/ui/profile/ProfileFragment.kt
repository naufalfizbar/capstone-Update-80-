package com.example.myapplication.ui.profile

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myapplication.ViewModelFactory
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.response.ProfileResponse
import com.example.myapplication.retrofit.ApiConfig
import com.example.myapplication.ui.main.MainViewModel
import com.example.myapplication.ui.welcome.WelcomeActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private var token = "token"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logoutBtn.setOnClickListener {
            viewModel.logout()
        }

        binding.editProfileLayout.setOnClickListener {
            val intent = Intent(requireContext(), EditActivity::class.java)
            intent.putExtra("username", binding.nameTextView.text.toString())
            startActivity(intent)
        }

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                val intent = Intent(requireContext(), WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                activity?.finish()
            } else {
                token = user.token
                Log.d(ContentValues.TAG, "token: $token")
                fetchProfile(token)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun fetchProfile(token: String) {
        val apiService = ApiConfig.getApiService()
        apiService.getProfile("Bearer $token").enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    val profile = response.body()
                    binding.nameTextView.text = profile?.name
                    binding.emailTextView.text = profile?.email
                    profile?.profilePicture?.let { profileUrl ->
                        Glide.with(this@ProfileFragment)
                            .load(profileUrl)
                            .into(binding.profileImageView)
                    }
                } else {
                    Log.e(ContentValues.TAG, "Failed to fetch profile: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "Error fetching profile", t)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
