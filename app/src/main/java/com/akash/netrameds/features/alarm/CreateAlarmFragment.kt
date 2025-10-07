package com.akash.netrameds.features.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.akash.netrameds.databinding.FragmentCreateAlarmBinding

class CreateAlarmFragment : Fragment() {

    private var _binding: FragmentCreateAlarmBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- CLICK LISTENERS ARE ADDED HERE ---

        // Set up the back button on the toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Set up the "Capture with Camera" card (with a placeholder action)
        binding.captureMedicineCard.setOnClickListener {
            Toast.makeText(requireContext(), "Camera feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        // Set up the "Fill Manually" card to navigate to the next page
        binding.fillManuallyCard.setOnClickListener {
            // 1. Create the navigation action using the auto-generated Directions class.
            //    This class is created by the Safe Args plugin based on your nav_graph.xml.
            val action = CreateAlarmFragmentDirections.actionCreateAlarmFragmentToAddMedicineNameFragment()

            // 2. Tell the NavController to execute that action.
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
