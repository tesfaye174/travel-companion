package com.travelcompanion.ui.tips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        loadTips()
    }

    private fun setupRecyclerView() {
        tipsAdapter = TipsAdapter { _ ->
            // Handle tip click - expand or navigate to detail
        }
        binding.rvTips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tipsAdapter
        }
    }

    private fun loadTips() {
        val tips = listOf(
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
        tipsAdapter.submitList(tips)
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
