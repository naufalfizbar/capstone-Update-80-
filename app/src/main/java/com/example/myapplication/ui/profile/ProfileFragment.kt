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
import com.example.myapplication.ViewModelFactory
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.ui.main.MainViewModel
import com.example.myapplication.ui.welcome.WelcomeActivity
import gen._base._base_java__assetres.srcjar.R.id.text

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

        // Uncomment if logout functionality is needed
         binding.logoutBtn.setOnClickListener {
             viewModel.logout()
         }

        binding.editProfileLayout.setOnClickListener {
            val intent = Intent(requireContext(), EditActivity::class.java)
            intent.putExtra("username", binding.nameTextView.text.toString())
//            intent.putExtra("email", binding.emailTextView.text.toString())
            startActivity(intent)
        }

        // Uncomment if change password functionality is needed
        // binding.btnChangePassword.setOnClickListener {
        //     startActivity(Intent(requireContext(), EditPasswordActivity::class.java))
        // }

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                val intent = Intent(requireContext(), WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                activity?.finish()
            } else {
                val mainViewModel = obtainViewModel()

                token = user.token
                Log.d(ContentValues.TAG, "token: $token")
//                binding.emailTextView.text = user.email
                binding.nameTextView.text = user.email
                // Uncomment and implement these lines if you need story data
                // mainViewModel.getStory(token)
                // mainViewModel.story.observe(viewLifecycleOwner) { storyList ->
                //     Log.d(ContentValues.TAG, "Story: $storyList")
                //     setStoryData(storyList)
                // }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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
