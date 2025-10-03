package com.akash.netrameds.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.akash.netrameds.R
import com.akash.netrameds.databinding.FragmentSignUpBinding // Import the generated binding class

class SignUpFragment : Fragment() {

    // 1. Declare the binding variable correctly
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 2. Inflate the layout using DataBindingUtil and assign it to the binding variable
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)

        // 3. Return the root view from the binding object
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 4. Set up click listeners here, after the view has been created
        // This is the safest place to access views to avoid crashes.
        binding.tvAlreadyHaveAccount.setOnClickListener {
            // Navigate to the login fragment using the action defined in your nav_graph.xml
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        // TODO: Set up other click listeners here (e.g., for the sign-up button)
        // binding.signUpButton.setOnClickListener { ... }
    }
}
