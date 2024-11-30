package com.example.papb.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.papb.AuthState
import com.example.papb.AuthViewModel
import com.example.papb.ToDoItem
import com.example.papb.ToDoViewModel
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.IntOffset
import androidx.compose.animation.core.animateFloatAsState

@Composable
fun ToDoListPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel, toDoViewModel: ToDoViewModel = viewModel()) {
    val authState = authViewModel.authState.observeAsState()

    val toDoList by toDoViewModel.toDoList.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var currentItem by remember { mutableStateOf<ToDoItem?>(null) }

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "To-Do List",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add To-Do")
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = rememberLazyListState()
        ) {
            items(
                items = toDoList.sortedWith(
                    compareBy<ToDoItem> { it.done }  // Urutkan berdasarkan status done terlebih dahulu
                        .thenByDescending { it.timestamp }  // Kemudian urutkan berdasarkan timestamp
                )
            ) { item ->
                ToDoItemCard(
                    item = item,
                    onEditClick = {
                        currentItem = item
                        showEditDialog = true
                    },
                    onDeleteClick = {
                        currentItem = item
                        showDeleteDialog = true
                    },
                    onToggleDone = { done ->
                        val updatedItem = item.copy(
                            done = done,
                            timestamp = item.timestamp
                        )
                        toDoViewModel.updateToDoItem(updatedItem)
                    }
                )
            }
        }
    }

    if (showAddDialog) {
        ToDoDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, description ->
                toDoViewModel.addToDoItem(title, description)
                showAddDialog = false
            }
        )
    }

    if (showEditDialog && currentItem != null) {
        ToDoDialog(
            initialTitle = currentItem!!.title,
            initialDescription = currentItem!!.description,
            onDismiss = { showEditDialog = false },
            onConfirm = { title, description ->
                val updatedItem = currentItem!!.copy(
                    title = title,
                    description = description,
                    timestamp = currentItem!!.timestamp
                )
                toDoViewModel.updateToDoItem(updatedItem)
                showEditDialog = false
            }
        )
    }

    if (showDeleteDialog && currentItem != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Delete Confirmation") },
            text = { Text(text = "Are you sure you want to delete this item?") },
            confirmButton = {
                Button(
                    onClick = {
                        toDoViewModel.deleteToDoItem(currentItem!!.id)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ToDoItemCard(
    item: ToDoItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleDone: (Boolean) -> Unit
) {
    var offsetX by remember(item.id, item.title, item.description, item.done) { mutableStateOf(0f) }
    val buttonWidth = 160.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 8.dp)
    ) {
        if (offsetX < 0) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    ),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.toInt(), 0) }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        val newOffset = offsetX + delta
                        offsetX = newOffset.coerceIn(-buttonWidth.value * 2, 0f)
                    },
                    onDragStopped = {
                        offsetX = if (offsetX < -buttonWidth.value / 2) {
                            -buttonWidth.value * 2
                        } else {
                            0f
                        }
                    }
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Checkbox(
                    checked = item.done,
                    onCheckedChange = { isChecked ->
                        onToggleDone(isChecked)
                        offsetX = 0f
                    }
                )
            }
        }
    }
}

@Composable
fun ToDoDialog(
    initialTitle: String = "",
    initialDescription: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "To-Do Item") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(title, description) }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
