package com.example.papb

data class ToDoItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val done: Boolean = false,
    val timestamp: Long = System.currentTimeMillis() // Tambahkan timestamp
)
