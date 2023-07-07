package com.example.resellapp

import android.net.Uri

data class Item(
    var id: String? = null,
    var name: String? = null,
    var price: Float? = null,
    var description: String? = null,
    var imageUrl: String? = null,
    var userId: String? = null,
    var bought: Boolean? = false,
    var imageUrlList: List<String>? = emptyList()


    )