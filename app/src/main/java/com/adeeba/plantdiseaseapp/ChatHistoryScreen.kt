package com.adeeba.plantdiseaseapp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.adeeba.plantdiseaseapp.entity.*
import kotlinx.coroutines.launch

@Composable
fun ChatHistoryScreen(
    onOpenChat: (Long) -> Unit,
    onNewChat: (Long) -> Unit
) {

    val context = LocalContext.current
    val dao = DatabaseProvider.getDatabase(context).chatDao()
    val scope = rememberCoroutineScope()

    var sessions by remember { mutableStateOf(listOf<ChatSession>()) }

    var showDialog by remember { mutableStateOf(false) }
    var selectedId by remember { mutableStateOf<Long?>(null) }
    var newTitle by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        sessions = dao.getAllSessions()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🌙 THEME TOGGLE
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text("Chats", style = MaterialTheme.typography.headlineMedium)

            IconButton(onClick = {
                ThemeManager.toggle()
            }) {
                Icon(
                    if (ThemeManager.isDarkMode.value)
                        Icons.Default.LightMode
                    else Icons.Default.DarkMode,
                    contentDescription = null
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                scope.launch {
                    val id = dao.insertSession(ChatSession())
                    onNewChat(id)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("+ New Chat")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {

            items(sessions) { session ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .combinedClickable(
                            onClick = { onOpenChat(session.id) },
                            onLongClick = {
                                selectedId = session.id
                                newTitle = session.title
                                showDialog = true
                            }
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {

                    Text(
                        text = if (session.title.isEmpty()) "New Chat" else session.title,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    // 🔥 RENAME / DELETE
    if (showDialog && selectedId != null) {

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Manage Chat") },

            text = {
                Column {

                    TextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        placeholder = { Text("Rename") }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                dao.deleteMessages(selectedId!!)
                                dao.deleteSession(selectedId!!)
                                sessions = dao.getAllSessions()
                            }
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete Chat")
                    }
                }
            },

            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        dao.updateSessionTitle(selectedId!!, newTitle)
                        sessions = dao.getAllSessions()
                    }
                    showDialog = false
                }) {
                    Text("Save")
                }
            },

            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}