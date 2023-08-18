package com.example.resellapp

data class User(
    var id: String? = null,
    var name: String? = null,
    var email: String? = null,
    var notificationId: String? = null,
    var items: List<Item>? = emptyList(),
    var boughtItems: List<Item>? = emptyList(),
    var likedItems: List<Item>? = emptyList()

    )