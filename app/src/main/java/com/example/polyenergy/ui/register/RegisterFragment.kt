package com.example.polyenergy.ui.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.polyenergy.R
import com.example.polyenergy.SessionManager
import com.example.polyenergy.databinding.FragmentCreateBinding
import com.example.polyenergy.ui.login.RegisterViewModelFactory

class RegisterFragment : Fragment() {
    private lateinit var loginViewModel: RegisterViewModel
    private var _binding: FragmentCreateBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel = ViewModelProvider(this, RegisterViewModelFactory())
            .get(RegisterViewModel::class.java)

        val usernameEditText = binding.email
        val passwordEditText = binding.password
        val verifyPasswordEditText = binding.verif
        val loginButton = binding.login
        val loadingProgressBar = binding.loading

        val session = SessionManager(requireContext())

        loginViewModel.registerForm.observe(viewLifecycleOwner,
            Observer { registerFormState ->
                if (registerFormState == null) {
                    return@Observer
                }
                loginButton.isEnabled = registerFormState.isDataValid
                registerFormState.usernameError?.let {
                    usernameEditText.error = getString(it)
                }
                registerFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
            })

        loginViewModel.registerResult.observe(viewLifecycleOwner,
            Observer { registerResult ->
                registerResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                registerResult.token?.let {
                    session.saveAuthToken(it)
                    Navigation.findNavController(view).navigate(R.id.action_global_home)
                }?:run {
                    showLoginFailed(registerResult.success)
                }
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                loginViewModel.registerDataChanged(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString(),
                    verifyPasswordEditText.text.toString()
                )
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        verifyPasswordEditText.addTextChangedListener(afterTextChangedListener)
        verifyPasswordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.register(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString(),
                    requireContext()
                )
            }
            false
        }

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            loginViewModel.register(
                usernameEditText.text.toString(),
                passwordEditText.text.toString(),
                requireContext()
            )
        }

        binding.existingAccount.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.login)
        }
    }

    private fun showLoginFailed(message: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
