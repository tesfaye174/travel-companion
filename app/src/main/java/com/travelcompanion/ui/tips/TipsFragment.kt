package com.travelcompanion.ui.tips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
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
    private var currentSearchQuery: String = ""

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
        setupRecyclerView()
        setupSearch()
        setupCategoryFilter()
        setupShuffleButton()
        loadTips()
    }

    private fun setupRecyclerView() {
        tipsAdapter = TipsAdapter { tip ->
            // Show tip details in a dialog
            showTipDetailsDialog(tip)
        }
        binding.rvTips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tipsAdapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            currentSearchQuery = text?.toString().orEmpty()
            filterTips()
        }
        // Chiudi la tastiera quando l'utente preme invio
        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == android.view.KeyEvent.KEYCODE_ENTER && event.action == android.view.KeyEvent.ACTION_DOWN)) {
                v.clearFocus()
                val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                true
            } else {
                false
            }
        }
    }

    private fun setupCategoryFilter() {
        binding.chipGroupCategories.setOnCheckedStateChangeListener { group, checkedIds ->
            currentCategory = when {
                checkedIds.contains(R.id.chip_packing) -> TipCategory.PACKING
                checkedIds.contains(R.id.chip_safety) -> TipCategory.SAFETY
                checkedIds.contains(R.id.chip_budget) -> TipCategory.BUDGET
                checkedIds.contains(R.id.chip_culture) -> TipCategory.CULTURE
                checkedIds.contains(R.id.chip_transport) -> TipCategory.TRANSPORT
                else -> null // "All" selezionato
            }
            // Se nessuna chip è selezionata, seleziona "All"
            if (checkedIds.isEmpty() || currentCategory == null) {
                group.check(R.id.chip_all)
                currentCategory = null
            }
            filterTips()
        }
    }

    private fun setupShuffleButton() {
        binding.fabShuffleTips.setOnClickListener {
            shuffleTips()
        }
    }

    private var lastShuffled: List<TravelTip>? = null
    private fun shuffleTips() {
        var shuffled: List<TravelTip>
        do {
            shuffled = allTips.shuffled()
        } while (shuffled == lastShuffled && allTips.size > 1)
        allTips = shuffled
        lastShuffled = shuffled
        filterTips()
    }

    private fun filterTips() {
        var filtered = allTips
        // Filtro per categoria
        if (currentCategory != null) {
            filtered = filtered.filter { it.category == currentCategory }
        }
        // Filtro per ricerca
        if (currentSearchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.title.contains(currentSearchQuery, ignoreCase = true) ||
                it.description.contains(currentSearchQuery, ignoreCase = true)
            }
        }
        tipsAdapter.submitList(filtered)
        updateEmptyState(filtered.isEmpty())
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvTips.visibility = if (isEmpty) View.GONE else View.VISIBLE
        // Migliora la visibilità del messaggio vuoto
        if (isEmpty) {
            binding.etSearch.clearFocus()
        }
    }

    private fun showTipDetailsDialog(tip: TravelTip) {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle(tip.title)
            .setMessage(tip.description)
            .setIcon(tip.iconRes)
            .setPositiveButton(R.string.ok, null)
            .show()
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
        tipsAdapter.submitList(allTips)
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
