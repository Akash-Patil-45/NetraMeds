package com.akash.netrameds.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController // 1. Import findNavController
import com.akash.netrameds.R
import com.akash.netrameds.databinding.FragmentLoginBinding // 2. Import the generated binding class

class LoginFragment : Fragment() {

    // 3. Declare the binding variable correctly
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 4. Inflate the layout using DataBindingUtil
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        // 5. Return the root view from the binding object
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 6. Set up click listeners here, in the correct lifecycle method
        binding.signUpText.setOnClickListener {
            // Navigate back to the sign-up fragment using the action from nav_graph.xml
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        // TODO: Add click listener for the actual login button
        // binding.loginButton.setOnClickListener { /* Handle login logic */ }
    }
}
