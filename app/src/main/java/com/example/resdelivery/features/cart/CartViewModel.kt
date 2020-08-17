package com.example.resdelivery.features.cart

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resdelivery.models.Meal
import com.example.resdelivery.util.SessionManagement
import com.example.resdelivery.util.SessionManagement.Companion.KEY_USER_ID
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber


enum class CartStatus { LOADING, SUCCESS, ERROR }

class CartViewModel @ViewModelInject constructor(
    private val sessionManagement: SessionManagement,
    private val database: FirebaseFirestore
) : ViewModel() {

    private val _status = MutableLiveData<CartStatus>()
    private val _cartItems = MutableLiveData<List<Meal>>()
    private val _showEmptyCart = MutableLiveData<Boolean>()

    val showEmptyCart: LiveData<Boolean>
        get() = _showEmptyCart

    val status: LiveData<CartStatus>
        get() = _status

    val cartItems: LiveData<List<Meal>>
        get() = _cartItems

    init {
        getCartItems()
    }

    private fun getCartItems() {
        _status.value = CartStatus.LOADING
        sessionManagement.getValue(SessionManagement.KEY_USER_ID)?.let {
            database
                .collection("users/")
                .document(it)
                .collection("cart/")
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _status.value = CartStatus.SUCCESS
                        if (task.result!!.documents.isEmpty()) {
                            _showEmptyCart.value = true
                        } else {
                            _showEmptyCart.value = false
                            val items: MutableList<Meal> = mutableListOf()
                            for (document in task.result!!.documents) run {
                                val meal: Meal? = document.toObject(Meal::class.java)
                                meal?.let { it1 ->
                                    items.add(it1)
                                }
                            }
                            _status.value = CartStatus.SUCCESS
                            if (items.isEmpty()) {
                                _showEmptyCart.value = true
                            } else {
                                _cartItems.value = items
                            }
                        }
                    } else {
                        _status.value = CartStatus.ERROR
                        Timber.e(task.exception)
                    }
                }
        }
    }

    fun removeFromCart(meal: Meal) {
        sessionManagement.getValue(KEY_USER_ID)?.let {
            database.collection("users/").document(it).collection("cart/")
                .document(meal.id).delete().addOnCompleteListener { task ->
                    Timber.d("Deleted from firebase")
                }
        }
    }
}
