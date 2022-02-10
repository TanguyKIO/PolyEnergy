package com.example.polyenergy.ui.login

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.polyenergy.R
import com.example.polyenergy.data.LoginApi
import com.example.polyenergy.domain.LoginParam
import com.example.polyenergy.domain.LoginResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResponse>()
    val loginResult: LiveData<LoginResponse> = _loginResult

    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)



    fun login(username: String, password: String, context: Context) {
        coroutineScope.launch {
            try {
                LoginApi.setRetrofit(context)
                val getToken = LoginApi.retrofitService.postLoginAsync(LoginParam(username, password))
                try {
                    var loginResponse = getToken.await()
                    _loginResult.value = loginResponse
                } catch (e: Exception) {
                    _loginResult.value = LoginResponse(success = null)
                }

            } catch (e: Exception) {
                _loginResult.value = LoginResponse(success = null)
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {

        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
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
}