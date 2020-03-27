package com.example.resdelivery.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resdelivery.util.SessionManagement
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel(
    private val sessionManagement: SessionManagement,
    private val auth : FirebaseAuth
) : ViewModel() {

    private val _showProgress = MutableLiveData<Boolean>()
    private val _navigateToMap = MutableLiveData<Boolean>()
    private val _errorMessage = MutableLiveData<String>()

    val errorMessage: LiveData<String>
        get() = _errorMessage

    val navigateToMap: LiveData<Boolean>
        get() = _navigateToMap

    val showProgress: LiveData<Boolean>
        get() = _showProgress

    fun doneNavigating() {
        _navigateToMap.value = null
    }

    fun doneShowingError(){
        _errorMessage.value = null
    }

    fun loginUser(email: String, password: String) {
        _showProgress.value = true
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _showProgress.value = false
                _navigateToMap.value = true
                //sessionManagement.setLoggedIn()
                sessionManagement.createSession(true, auth.uid!!,"",email)
            } else {
                _errorMessage.value = task.exception?.message.toString()
                _showProgress.value = false
            }
        }
    }

    fun validateEmail(email: String): String {
        if (email.trim().isEmpty() || email.length == 0) {
            return "Please enter an email"
        }
        return "ok"
    }

    fun validatePassword(password: String): String {
        if (password.trim().isEmpty() || password.length == 0) {
            return "Please enter a password"
        }
        if (password.length < 6) {
            return "Minimum password must be 7 chars"
        }
        return "ok"
    }
}