package com.travelcompanion.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.travelcompanion.R
import com.travelcompanion.core.ui.safeNavigate
import com.travelcompanion.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        // Carica il timestamp del primo accesso salvato
        viewLifecycleOwner.lifecycleScope.launch {
            val ts = viewModel.getMemberSince()
            binding.tvMemberSince.text = getString(R.string.member_since, dateFormat.format(Date(ts)))
        }

        try {
            val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            binding.tvVersion.text = getString(R.string.version_info, packageInfo.versionName)
        } catch (e: Exception) {
            binding.tvVersion.text = getString(R.string.version_info, "1.0.0")
        }
    }

    private fun setupClickListeners() {
        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        binding.cardSettings.setOnClickListener {
            findNavController().safeNavigate(R.id.action_profile_to_settings)
        }

        binding.cardTips.setOnClickListener {
            findNavController().safeNavigate(R.id.action_profile_to_tips)
        }

        binding.cardAbout.setOnClickListener {
            showAboutDialog()
        }

        binding.cardRateApp.setOnClickListener {
            openPlayStore()
        }

        binding.cardShareApp.setOnClickListener {
            shareApp()
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.profileStats.collect { stats ->
                        stats?.let {
                            binding.tvTotalTrips.text = it.totalTrips.toString()
                            binding.tvCountriesVisited.text = it.countriesVisited.toString()
                            binding.tvCitiesVisited.text = it.citiesVisited.toString()
                            binding.tvTotalDistance.text = getString(R.string.total_distance_val, it.totalDistance)
                        }
                    }
                }
                launch {
                    viewModel.userName.collect { name ->
                        binding.tvUserName.text = name.ifEmpty { getString(R.string.user_name) }
                    }
                }
                launch {
                    viewModel.userEmail.collect { email ->
                        binding.tvUserEmail.text = email.ifEmpty { getString(R.string.user_email) }
                    }
                }
            }
        }
    }

    private fun showEditProfileDialog() {
        val currentName = viewModel.userName.value
        val currentEmail = viewModel.userEmail.value

        val nameInput = EditText(requireContext()).apply {
            hint = getString(R.string.user_name)
            setText(currentName)
            setSingleLine()
        }
        val emailInput = EditText(requireContext()).apply {
            hint = getString(R.string.user_email)
            setText(currentEmail)
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            setSingleLine()
        }

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            val pad = (16 * resources.displayMetrics.density).toInt()
            setPadding(pad, pad / 2, pad, 0)
            addView(nameInput)
            addView(emailInput)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit_profile)
            .setView(layout)
            .setPositiveButton(R.string.save) { _, _ ->
                val newName = nameInput.text.toString().trim()
                val newEmail = emailInput.text.toString().trim()
                viewModel.updateProfile(newName, newEmail)
                Snackbar.make(binding.root, R.string.profile_updated, Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showAboutDialog() {
        val version = try {
            requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName
        } catch (e: Exception) {
            "1.0.0"
        }
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.about_app)
            .setMessage(getString(R.string.about_app_message, version))
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    private fun openPlayStore() {
        val packageName = requireContext().packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: Exception) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }

    private fun shareApp() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)))
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.logout)
            .setMessage(R.string.logout_confirmation_message)
            .setPositiveButton(R.string.logout) { _, _ ->
                viewModel.clearProfile()
                Snackbar.make(binding.root, R.string.logged_out, Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
