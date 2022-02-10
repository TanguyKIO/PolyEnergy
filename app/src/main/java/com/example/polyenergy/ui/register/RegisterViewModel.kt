package com.example.polyenergy.ui.register

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.polyenergy.R
import com.example.polyenergy.data.LoginApi
import com.example.polyenergy.domain.LoginParam
import com.example.polyenergy.domain.LoginResponse
import com.example.polyenergy.ui.login.LoginFormState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _registerForm = MutableLiveData<LoginFormState>()
    val registerForm: LiveData<LoginFormState> = _registerForm

    private val _registerResult = MutableLiveData<LoginResponse>()
    val registerResult: LiveData<LoginResponse> = _registerResult

    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun register(username: String, password: String, context: Context) {
        coroutineScope.launch {
            try {
                LoginApi.setRetrofit(context)
                val getToken = LoginApi.retrofitService.postRegisterAsync(LoginParam(username, password))
                try {
                    var loginResponse = getToken.await()
                    _registerResult.value = loginResponse
                } catch (e: Exception) {
                    _registerResult.value = LoginResponse(success = "Echec de connexion")
                }

            } catch (e: Exception) {
                _registerResult.value = LoginResponse(success = "Echec de connexion")
            }
        }
    }

    fun registerDataChanged(username: String, password: String, verifyPassword: String) {

        if (!isUserNameValid(username)) {
            _registerForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _registerForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else if (!arePasswordTheSame(password, verifyPassword)) {
            _registerForm.value = LoginFormState(passwordError = R.string.not_same_password)
        } else {
            _registerForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun arePasswordTheSame(password: String, verifyPassword: String): Boolean {
        return password.equals(verifyPassword, false)
    }
}
