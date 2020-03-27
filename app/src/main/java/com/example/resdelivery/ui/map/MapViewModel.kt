package com.example.resdelivery.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resdelivery.models.Branch
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

class MapViewModel(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    // b/c i only want to trigger the onChange of branch list method once as there are values that could be null
    private val _oneDone = MutableLiveData<Boolean>()
    private val _branches = MutableLiveData<List<Branch>>()

    val oneDone: LiveData<Boolean>
        get() = _oneDone

    val branches: LiveData<List<Branch>>
        get() = _branches

    init {
        getBranches()
    }

    private fun getBranches() {
        _oneDone.value = true
        firestore.collection("branches/")
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val items = mutableListOf<Branch>()
                    for (document in task.result!!.documents) run {
                        val branch = document.toObject(Branch::class.java)
                        branch?.let {
                            items.add(it)
                            Timber.d("Branch ${branch.location.toString()} , ${branch.title}")
                        }
                        _branches.value = items
                    }
                } else {
                    Timber.e(task.exception?.message)
                }
            }
    }

    fun getDone(): Boolean? {
        return _oneDone.value
    }

    fun done() {
        _oneDone.value = false
    }
}