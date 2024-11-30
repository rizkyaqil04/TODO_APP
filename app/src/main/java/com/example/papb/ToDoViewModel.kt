package com.example.papb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.UUID

class ToDoViewModel : ViewModel() {
    private val repository = ToDoRepository()

    private val _toDoList = MutableStateFlow<List<ToDoItem>>(emptyList())
    val toDoList: StateFlow<List<ToDoItem>> = _toDoList

    init {
        // Subscribe to real-time updates
        viewModelScope.launch {
            repository.getToDoItemsFlow()
                .catch { e ->
                    // Handle error
                }
                .collect { items ->
                    _toDoList.value = items
                }
        }
    }

    fun addToDoItem(title: String, description: String) {
        val newItem = ToDoItem(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            done = false
        )
        viewModelScope.launch {
            repository.addToDoItem(newItem)
        }
    }

    fun updateToDoItem(item: ToDoItem) {
        viewModelScope.launch {
            repository.updateToDoItem(item)
        }
    }

    fun deleteToDoItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteToDoItem(itemId)
        }
    }
}
