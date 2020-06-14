package com.example.resdelivery.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resdelivery.util.SessionManagement
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

class RegisterViewModel(
    private val sessionManagement: SessionManagement,
    private val auth : FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _showProgress = MutableLiveData<Boolean>()
    private val _navigateToMap = MutableLiveData<Boolean>()
    private val _errorMessage = MutableLiveData<String>()

    val navigateToMap: LiveData<Boolean>
        get() = _navigateToMap

    val showProgress: LiveData<Boolean>
        get() = _showProgress

    fun doneNavigating() {
        _navigateToMap.value = null
    }

    fun doneShowingError() {
        _errorMessage.value = null
    }

    fun signUp(email: String, password: String, name: String) {
        _showProgress.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.uid?.let {
                        val map: HashMap<String, String> = HashMap()
                        map.put("id", it)
                        map.put("name", name)
                        map.put("email", email)
                        map.put("password", password)
                        firestore.collection("users/")
                            .document(it)
                            .set(map as Map<String, Any>)
                            .addOnCompleteListener { task ->
                                _showProgress.value = false
                                _navigateToMap.value = true
                                if (task.isSuccessful) {
                                    sessionManagement.createSession(true, it, name, email)
                                    Timber.d(sessionManagement.isLoggedIn().toString())
                                } else Timber.d(task.exception?.message.toString())
                            }
                    }
                } else {
                    _errorMessage.value = task.exception?.message.toString()
                    _showProgress.value = false
                }
            }
    }

    fun validateUser(name: String, email: String, password: String): String? {
        if (name.trim().isEmpty()) {
            return "Please Enter your name"
        } else if (email.trim().isEmpty()) {
            return "Please Enter your email"
        } else if (password.trim().isEmpty()) {
            return "Please Enter your password"
        }
        return "ok"
    }
}