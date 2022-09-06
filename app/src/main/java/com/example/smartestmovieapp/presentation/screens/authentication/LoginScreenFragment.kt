package com.example.smartestmovieapp.presentation.screens.authentication

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.data.repository.movieDetails.MovieDetailsRepositoryImpl
import com.example.smartestmovieapp.data.repository.trailer.TrailerRepositoryImpl
import com.example.smartestmovieapp.data.repository.upcoming.UpcomingRepositoryImpl
import com.example.smartestmovieapp.databinding.FragmentLoginScreenBinding
import com.example.smartestmovieapp.databinding.FragmentLoginScreenBinding.inflate
import com.example.smartestmovieapp.presentation.screens.VM.MovieDetailsVM
import com.example.smartestmovieapp.presentation.screens.VM.TrailerVM
import com.example.smartestmovieapp.presentation.screens.VM.UpcomingVM
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

open class LoginScreenFragment : AuthFragment() {

    private lateinit var binding: FragmentLoginScreenBinding

    @Inject
    lateinit var upcomingRepositoryImpl: UpcomingRepositoryImpl

    @Inject
    lateinit var movieDetailsRepositoryImpl: MovieDetailsRepositoryImpl

    @Inject
    lateinit var trailerRepositoryImpl: TrailerRepositoryImpl

    @Inject
    lateinit var upcomingVM: UpcomingVM

    @Inject
    lateinit var movieDetailsVM: MovieDetailsVM

    @Inject
    lateinit var trailerVM: TrailerVM

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //edit text
        binding.username.onFocusChangeListener = focusListener
        binding.password.onFocusChangeListener = focusListener

        binding.buttonSignIn.setOnClickListener { checkInput() }
        binding.googleSignIn.setOnClickListener { signInWithGoogle() }
        binding.noAccount.setOnClickListener { goToRegisterPage() }

        binding.password.setOnKeyListener(KeyDonePressed(binding.password))

    }


    private fun goToRegisterPage() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }


    private fun inputFieldsValid() =
        !binding.username.text.isNullOrEmpty() &&
                !binding.password.text.isNullOrEmpty()


    private fun checkInput() {
        if (!inputFieldsValid()) {
            Toast.makeText(requireContext(), INVALID_FIELDS, Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.username.text).matches()) {
            Toast.makeText(requireContext(), INVALID_EMAIL, Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.password.text.length < MIN_LENGTH_PASSWORD_REQUIRED) {
            Toast.makeText(requireContext(), INVALID_PASSWORD_LENGTH, Toast.LENGTH_SHORT).show()
            return
        }

        loginUser()
    }


    private fun loginUser() {
        firebaseAuth.signInWithEmailAndPassword(
            binding.username.text.toString(),
            binding.password.text.toString()
        )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    goToHomePage()
                } else {
                    Toast.makeText(requireContext(), LOGIN_FAILED, Toast.LENGTH_SHORT).show()
                }
            }
    }

}
