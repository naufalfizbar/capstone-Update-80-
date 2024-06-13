package com.example.test.ui.home

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.ViewModelFactory
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.home.HomeViewModel
import com.example.myapplication.ui.main.MainViewModel
import com.example.myapplication.ui.profil.EditActivity
import com.example.myapplication.ui.welcome.WelcomeActivity

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
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.historyCardView.setOnClickListener{
            val intent = Intent(requireContext(), EditActivity::class.java)
            startActivity(intent)
        }

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
                binding.tvName.text = user.email

                // Uncomment and implement these lines if you need story data
                // mainViewModel.getStory(token)
                // mainViewModel.story.observe(viewLifecycleOwner) { storyList ->
                //     Log.d(ContentValues.TAG, "Story: $storyList")
                //     setStoryData(storyList)
                // }
            }
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