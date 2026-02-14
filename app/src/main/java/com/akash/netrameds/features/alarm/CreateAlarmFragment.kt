package com.akash.netrameds.features.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.akash.netrameds.databinding.FragmentCreateAlarmBinding

class CreateAlarmFragment : Fragment() {

    private var _binding: FragmentCreateAlarmBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Listen for results from ScanFragment
        setFragmentResultListener("requestKey") { requestKey, bundle ->
            val medicineName = bundle.getString("medicineName")
            if (!medicineName.isNullOrEmpty()) {
                // Navigate to SelectMedicineTypeFragment with the captured name
                val action = CreateAlarmFragmentDirections.actionCreateAlarmFragmentToSelectMedicineTypeFragment(medicineName)
                findNavController().navigate(action)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // --- CAPTURE MEDICINE BUTTON ---
        binding.captureMedicineCard.setOnClickListener {
            // Navigate to ScanFragment with isForResult = true
            val action = CreateAlarmFragmentDirections.actionCreateAlarmFragmentToScanMedicineFragment(isForResult = true)
            findNavController().navigate(action)
        }

        // --- FILL MANUALLY BUTTON ---
        binding.fillManuallyCard.setOnClickListener {
            val action = CreateAlarmFragmentDirections.actionCreateAlarmFragmentToAddMedicineNameFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}