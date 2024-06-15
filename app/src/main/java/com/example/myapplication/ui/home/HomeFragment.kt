package com.example.test.ui.home

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myapplication.ViewModelFactory
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.response.ProfileResponse
import com.example.myapplication.retrofit.ApiConfig
import com.example.myapplication.ui.home.HomeViewModel
import com.example.myapplication.ui.main.MainViewModel
import com.example.myapplication.ui.profile.EditActivity
import com.example.myapplication.ui.welcome.WelcomeActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private var token = "token"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.historyCardView.setOnClickListener{
//            val intent = Intent(requireContext(), EditActivity::class.java)
//            startActivity(intent)
//        }

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                val intent = Intent(requireContext(), WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                activity?.finish()
            } else {
                token = user.token
                fetchProfile(token)
                Log.d(ContentValues.TAG, "token: $token")
//                binding.tvName.text = user.name


            }
        }
    }

    private fun fetchProfile(token: String) {
        val apiService = ApiConfig.getApiService()
        apiService.getProfile("Bearer $token").enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    val profile = response.body()
                    binding.tvName.text = profile?.name

                    profile?.profilePicture?.let { profileUrl ->
//                        Glide.with(this@ProfileFragment)
//                            .load(profileUrl)
//                            .into(binding.profileImageView)
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
    private fun obtainViewModel(): MainViewModel {
        val factory = ViewModelFactory.getInstance(requireActivity().application)
        return ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}