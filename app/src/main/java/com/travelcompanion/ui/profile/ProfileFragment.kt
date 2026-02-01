package com.travelcompanion.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.travelcompanion.R
import com.travelcompanion.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupUI() {
        // Set user info (would come from repository in real app)
        binding.tvUserName.text = getString(R.string.user_name)
        binding.tvUserEmail.text = getString(R.string.user_email)

        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        binding.tvMemberSince.text = getString(R.string.member_since, dateFormat.format(Date()))

        // App version
        try {
            val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            binding.tvVersion.text = getString(R.string.version_info, packageInfo.versionName)
        } catch (e: Exception) {
            binding.tvVersion.text = getString(R.string.version_info, "1.0.0")
        }
    }

    private fun setupClickListeners() {
        binding.btnEditProfile.setOnClickListener {
            // Navigate to edit profile
        }

        binding.cardSettings.setOnClickListener {
            findNavController().navigate(R.id.navigation_settings)
        }

        binding.cardAbout.setOnClickListener {
            // Show about dialog
        }

        binding.cardRateApp.setOnClickListener {
            // Open Play Store
        }

        binding.cardShareApp.setOnClickListener {
            // Share app
            shareApp()
        }

        binding.btnLogout.setOnClickListener {
            // Handle logout
        }
    }

    private fun observeViewModel() {
        viewModel.profileStats.observe(viewLifecycleOwner) { stats ->
            binding.tvTotalTrips.text = stats.totalTrips.toString()
            binding.tvCountriesVisited.text = stats.countriesVisited.toString()
            binding.tvCitiesVisited.text = stats.citiesVisited.toString()
            binding.tvTotalDistance.text = getString(R.string.total_distance_val, stats.totalDistance)
        }
    }

    private fun shareApp() {
        val shareIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT,
                "Check out Travel Companion - the best app for tracking your travels!")
            type = "text/plain"
        }
        startActivity(android.content.Intent.createChooser(shareIntent, getString(R.string.share_app)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
