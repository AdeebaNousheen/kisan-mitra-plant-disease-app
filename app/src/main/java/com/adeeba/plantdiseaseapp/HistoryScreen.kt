package com.adeeba.plantdiseaseapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.adeeba.plantdiseaseapp.entity.ChatSession
import com.adeeba.plantdiseaseapp.DatabaseProvider
import kotlinx.coroutines.launch

@Composable
fun DetectionChatHistoryScreen(
    onOpenChat: (Long) -> Unit,
    onNewChat: (Long) -> Unit,
    onBack: () -> Unit
) {

    val context = LocalContext.current
    val dao = DatabaseProvider.getDatabase(context).chatDao()
    val scope = rememberCoroutineScope()

    var sessions by remember { mutableStateOf(listOf<ChatSession>()) }

    // 🔄 LOAD SESSIONS
    LaunchedEffect(Unit) {
        sessions = dao.getAllSessions()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔙 BACK
        TextButton(onClick = onBack) {
            Text("← Back")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Chats",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ➕ NEW CHAT
        Button(
            onClick = {
                scope.launch {
                    val id = dao.insertSession(
                        ChatSession(title = "New Chat")
                    )
                    onNewChat(id)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+ New Chat")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (sessions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No chats yet")
            }
        } else {

            LazyColumn {

                items(sessions) { session ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onOpenChat(session.id) }
                    ) {

                        Column(modifier = Modifier.padding(16.dp)) {

                            Text(
                                text = session.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}