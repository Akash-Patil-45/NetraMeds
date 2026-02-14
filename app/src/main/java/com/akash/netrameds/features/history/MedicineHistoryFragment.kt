package com.akash.netrameds.features.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.akash.netrameds.data.AppDatabase
import com.akash.netrameds.databinding.FragmentMedicineHistoryBinding
import kotlinx.coroutines.launch

class MedicineHistoryFragment : Fragment() {

    private var _binding: FragmentMedicineHistoryBinding? = null
    private val binding get() = _binding!!

    private val historyAdapter = HistoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicineHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup Toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Setup RecyclerView
        binding.historyRecyclerview.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Load data from database
        loadHistory()
    }

    private fun loadHistory() {
        lifecycleScope.launch {
            val historyDao = AppDatabase.getDatabase(requireContext()).scanHistoryDao()
            val historyList = historyDao.getAllScanHistory()

            if (historyList.isEmpty()) {
                binding.noHistoryText.isVisible = true
                binding.historyRecyclerview.isVisible = false
            } else {
                binding.noHistoryText.isVisible = false
                binding.historyRecyclerview.isVisible = true
                historyAdapter.submitList(historyList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}