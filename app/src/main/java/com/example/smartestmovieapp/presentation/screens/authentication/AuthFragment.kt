package com.example.smartestmovieapp.presentation.screens.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.smartestmovieapp.R
import com.example.smartestmovieapp.presentation.screens.home.HomeScreenActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.android.support.AndroidSupportInjection

const val INVALID_FIELDS = "All input fields must be completed"
const val PASSWORD_FIELDS_NO_MATCH = "Password fields do not match"
const val INVALID_EMAIL = "Invalid email address format"
const val INVALID_PASSWORD_LENGTH = "Password should contain at least 6 characters"
const val REGISTER_FAIL = "Something went wrong... Couldn't register user"
const val LOGIN_FAILED = "Login failed"
const val MIN_LENGTH_PASSWORD_REQUIRED = 6

open class AuthFragment : Fragment() {

    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestProfile()
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
    }


    fun signInWithGoogle() {
        val googleSignInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(googleSignInIntent)
    }

    //launcher for Google sign in
    val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            try {
                val accountTask = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
                val account = accountTask.result
                firebaseAuthWithGoogleAcount(account)

            } catch (e: RuntimeExecutionException) {
                Toast.makeText(
                    context,
                    "Something went wrong on Google sign in...",
                    Toast.LENGTH_SHORT
                ).show()
                Log.v("TEST", "RESULT CODE: " + activityResult.resultCode.toString())
            }
        }

    //firebase - sign in with Google
    private fun firebaseAuthWithGoogleAcount(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credentials)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    goToHomePage()
                } else {
                    Toast.makeText(requireContext(), LOGIN_FAILED, Toast.LENGTH_SHORT).show()
                    val exception = task.exception
                    Log.v("TEST", "FAILED LOGIN: $exception")
                }
            }
    }

    // HomePage redirect
    fun goToHomePage() {
        val redirectToHomePageIntent = Intent(requireActivity(), HomeScreenActivity::class.java)
        startActivity(redirectToHomePageIntent)
        requireActivity().finish()
    }


    // edit text - focus change listener
    val focusListener = object : View.OnFocusChangeListener {
        override fun onFocusChange(view: View?, hasFocus: Boolean) {
            if (hasFocus) {
                view?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.focused_edit_text)
            } else {
                val edittext = view as EditText
                if (edittext.text.isNullOrEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.invalid_edit_text)
                } else {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.valid_edit_text)
                }
            }
        }

    }

    inner class KeyDonePressed(val editText: EditText) : View.OnKeyListener {
        override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
            p2?.apply {
                if (this.action == KeyEvent.ACTION_DOWN && p1 == KeyEvent.KEYCODE_ENTER) {
                    editText.clearFocus()
                    hideKeyboard()
                    return true
                }
            }
            return false
        }

        private fun hideKeyboard() {
            val imm =
                activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view!!.windowToken, 0)
        }
    }


}