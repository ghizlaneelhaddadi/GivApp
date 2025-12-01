package com.example.test1000.model

import android.provider.ContactsContract.CommonDataKinds.Phone

data class societies(
    val name: String = "",
    val description: String = "",
    val image: String = "",
    val category: String = "",
    val accumulator: Int = 0,
    val goalsociety: Int = 1000000,
    val address: String = "",
    val email: String = "",
    val phone: String = "",
    val facebook: String = "",
    val instagram: String = "",
    val twitter: String = "",
    val site: String = ""
)