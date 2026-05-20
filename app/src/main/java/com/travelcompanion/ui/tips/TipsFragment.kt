package com.travelcompanion.ui.tips

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.travelcompanion.R
import com.travelcompanion.databinding.FragmentTipsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TipsFragment : Fragment() {

    private var _binding: FragmentTipsBinding? = null
    private val binding get() = _binding!!

    private lateinit var tipsAdapter: TipsAdapter
    private var allTips: List<TravelTip> = emptyList()
    private var currentCategory: TipCategory? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTipsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        setupRecyclerView()
        loadTips()
        setupSearch()
        setupCategoryChips()
    }

    private fun setupRecyclerView() {
        tipsAdapter = TipsAdapter { tip ->
            showTipDetailDialog(tip)
        }
        binding.rvTips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tipsAdapter
        }
    }

    private fun loadTips() {
        allTips = listOf(
            TravelTip(
                id = 1,
                title = getString(R.string.tip_packing_light),
                description = getString(R.string.tip_packing_light_desc),
                category = TipCategory.PACKING,
                iconRes = R.drawable.ic_trip
            ),
            TravelTip(
                id = 2,
                title = getString(R.string.tip_safety_docs),
                description = getString(R.string.tip_safety_docs_desc),
                category = TipCategory.SAFETY,
                iconRes = R.drawable.ic_privacy
            ),
            TravelTip(
                id = 3,
                title = getString(R.string.tip_budget_early),
                description = getString(R.string.tip_budget_early_desc),
                category = TipCategory.BUDGET,
                iconRes = R.drawable.ic_stats
            ),
            TravelTip(
                id = 4,
                title = getString(R.string.tip_culture_local),
                description = getString(R.string.tip_culture_local_desc),
                category = TipCategory.CULTURE,
                iconRes = R.drawable.ic_info
            ),
            TravelTip(
                id = 5,
                title = getString(R.string.tip_transport_apps),
                description = getString(R.string.tip_transport_apps_desc),
                category = TipCategory.TRANSPORT,
                iconRes = R.drawable.ic_map
            )
        )
        applyFilters()
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                applyFilters()
            }
        })
    }

    private fun setupCategoryChips() {
        val chipMap = mapOf(
            binding.chipPacking to TipCategory.PACKING,
            binding.chipSafety to TipCategory.SAFETY,
            binding.chipBudget to TipCategory.BUDGET,
            binding.chipCulture to TipCategory.CULTURE,
            binding.chipTransport to TipCategory.TRANSPORT
        )
        val allChips = listOf(binding.chipAll) + chipMap.keys

        binding.chipAll.setOnClickListener {
            currentCategory = null
            allChips.forEach { it.isChecked = false }
            binding.chipAll.isChecked = true
            applyFilters()
        }

        chipMap.forEach { (chip, category) ->
            chip.setOnClickListener {
                currentCategory = if (currentCategory == category) {
                    // Deselect: revert to "All"
                    allChips.forEach { it.isChecked = false }
                    binding.chipAll.isChecked = true
                    null
                } else {
                    allChips.forEach { it.isChecked = false }
                    chip.isChecked = true
                    category
                }
                applyFilters()
            }
        }

        // Initial state
        binding.chipAll.isChecked = true
    }

    private fun applyFilters() {
        val query = binding.etSearch.text?.toString()?.trim()?.lowercase() ?: ""
        val filtered = allTips.filter { tip ->
            val matchesCategory = currentCategory == null || tip.category == currentCategory
            val matchesSearch = query.isEmpty() ||
                tip.title.lowercase().contains(query) ||
                tip.description.lowercase().contains(query)
            matchesCategory && matchesSearch
        }
        tipsAdapter.submitList(filtered)
    }

    private fun showTipDetailDialog(tip: TravelTip) {
        AlertDialog.Builder(requireContext())
            .setTitle(tip.title)
            .setMessage(tip.description)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class TravelTip(
    val id: Int,
    val title: String,
    val description: String,
    val category: TipCategory,
    val iconRes: Int
)

enum class TipCategory {
    PACKING, SAFETY, BUDGET, CULTURE, TRANSPORT
}
