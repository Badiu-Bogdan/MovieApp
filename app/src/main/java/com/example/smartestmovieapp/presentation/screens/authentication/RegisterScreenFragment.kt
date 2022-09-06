package com.example.smartestmovieapp.presentation.screens.authentication

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.databinding.FragmentRegisterScreenBinding
import com.example.smartestmovieapp.databinding.FragmentRegisterScreenBinding.inflate


class RegisterScreenFragment : AuthFragment() {

    private lateinit var binding: FragmentRegisterScreenBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //edit text
        binding.username.onFocusChangeListener = focusListener
        binding.password.onFocusChangeListener = focusListener
        binding.confirmPassword.onFocusChangeListener = focusListener
        binding.email.onFocusChangeListener = focusListener

        binding.buttonSignUp.setOnClickListener { checkInput() }
        binding.googleSignIn.setOnClickListener { signInWithGoogle() }
        binding.accountAlreadyCreated.setOnClickListener { goToLoginPage() }

        binding.confirmPassword.setOnKeyListener(KeyDonePressed(binding.confirmPassword))
    }


    private fun goToLoginPage() {
        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }

    private fun inputFieldsValid() =
        !binding.email.text.isNullOrEmpty() &&
                !binding.username.text.isNullOrEmpty() &&
                !binding.password.text.isNullOrEmpty() &&
                !binding.confirmPassword.text.isNullOrEmpty()


    private fun checkInput() {
        if (!inputFieldsValid()) {
            Toast.makeText(requireContext(), INVALID_FIELDS, Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.text).matches()) {
            Toast.makeText(requireContext(), INVALID_EMAIL, Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.password.text.length < MIN_LENGTH_PASSWORD_REQUIRED) {
            Toast.makeText(requireContext(), INVALID_PASSWORD_LENGTH, Toast.LENGTH_SHORT).show()
            return
        }
        if (!binding.password.text.contentEquals(binding.confirmPassword.text)) {
            Toast.makeText(requireContext(), PASSWORD_FIELDS_NO_MATCH, Toast.LENGTH_SHORT).show()
            return
        }

        registerUser()
    }

    private fun registerUser() {
        firebaseAuth.createUserWithEmailAndPassword(
            binding.email.text.toString().trim(),
            binding.password.text.toString().trim()
        )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    goToHomePage()
                } else {
                    Toast.makeText(requireContext(), REGISTER_FAIL, Toast.LENGTH_SHORT).show()
                }
            }
    }

}