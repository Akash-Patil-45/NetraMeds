package com.akash.netrameds.features.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.akash.netrameds.R
import com.akash.netrameds.databinding.FragmentSelectMedicineTypeBinding

class SelectMedicineTypeFragment : Fragment() {

    private var _binding: FragmentSelectMedicineTypeBinding? = null
    private val binding get() = _binding!!

    // Retrieve the medicineName passed from the previous fragment
    private val args: SelectMedicineTypeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectMedicineTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Previous button takes the user back
        binding.previousButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // Toolbar back button also navigates up
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.nextButton.setOnClickListener {
            val selectedTypeId = binding.medicineTypeRadioGroup.checkedRadioButtonId
            if (selectedTypeId == -1) {
                Toast.makeText(requireContext(), "Please select a medicine type", Toast.LENGTH_SHORT).show()
            } else {
                val medicineType = when (selectedTypeId) {
                    R.id.radio_button_capsule -> "Capsule"
                    R.id.radio_button_syrup -> "Syrup"
                    else -> "Tablet" // Default to Tablet
                }

                // Retrieve medicineName from arguments
                val medicineName = args.medicineName

                // Use the generated Directions class to navigate and pass the data
                val action = SelectMedicineTypeFragmentDirections
                    .actionSelectMedicineTypeFragmentToAddDosageFragment(medicineName, medicineType)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
