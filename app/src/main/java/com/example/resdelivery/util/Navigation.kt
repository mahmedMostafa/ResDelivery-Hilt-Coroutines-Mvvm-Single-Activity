package com.example.resdelivery.util

import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigator

//this is just helpful because if u click on an item twice (trying to navigate to another fragment) so fast the app will crash
fun NavController.navigateSafe(
    @IdRes resId: Int,
    navDirections: NavDirections? = null,
    navExtras: Navigator.Extras? = null
) {
//    val action = currentDestination?.getAction(resId) ?: graph.getAction(resId)
    if (currentDestination?.id == resId && navDirections != null) {
        navExtras?.let {
            navigate(navDirections, it)
        } ?: navigate(navDirections)
    }
}