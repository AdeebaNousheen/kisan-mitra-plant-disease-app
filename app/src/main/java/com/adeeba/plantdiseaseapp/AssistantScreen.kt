package com.adeeba.plantdiseaseapp

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.adeeba.plantdiseaseapp.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import com.adeeba.plantdiseaseapp.ChatDao

@Composable
fun AssistantScreen(
    language: String,
    chatId: Long,
    onBack: () -> Unit,
    onMenuClick: () -> Unit
) {

    val context = LocalContext.current
    val dao = DatabaseProvider.getDatabase(context).chatDao()

    var inputText by remember { mutableStateOf("") }
    val chatList = remember { mutableStateListOf<ChatMessage>() }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    // 🌍 LANGUAGE MAP
    val locale = when (language.lowercase()) {
        "hindi" -> Locale("hi", "IN")
        "telugu" -> Locale("te", "IN")
        else -> Locale.ENGLISH
    }

    // 🔊 TEXT TO SPEECH (MULTI LANGUAGE)
    val tts = remember {
        TextToSpeech(context) {}
    }

    LaunchedEffect(language) {
        tts.language = locale
    }

    // 🎤 SPEECH INPUT (MULTI LANGUAGE)
    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val result =
                it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!result.isNullOrEmpty()) inputText = result[0]
        }
    }

    val listState = rememberLazyListState()

    // 📥 LOAD CHAT
    LaunchedEffect(chatId) {
        val messages = dao.getMessages(chatId)
        chatList.clear()
        chatList.addAll(messages)
    }

    // 🔽 AUTO SCROLL
    LaunchedEffect(chatList.size) {
        if (chatList.isNotEmpty()) {
            listState.animateScrollToItem(chatList.size - 1)
        }
    }

    Column(Modifier.fillMaxSize()) {

        // 💬 CHAT LIST
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(chatList) { msg ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        if (msg.sender == "user") Arrangement.End else Arrangement.Start
                ) {

                    if (msg.sender == "bot") {
                        Text("🤖")
                        Spacer(Modifier.width(6.dp))
                    }

                    Column {

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (msg.sender == "user")
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = msg.message,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // 🔊 SPEAK (MULTI LANGUAGE)
                        if (msg.sender == "bot") {
                            Row {

                                IconButton(onClick = {
                                    tts.language = locale
                                    tts.speak(
                                        msg.message.take(300),
                                        TextToSpeech.QUEUE_FLUSH,
                                        null,
                                        null
                                    )
                                }) {
                                    Icon(Icons.Default.VolumeUp, null)
                                }

                                IconButton(onClick = {
                                    tts.stop()
                                }) {
                                    Icon(Icons.Default.Stop, null)
                                }
                            }
                        }
                    }

                    if (msg.sender == "user") {
                        Spacer(Modifier.width(6.dp))
                        Text("👤")
                    }
                }
            }

            if (isLoading) {
                item {
                    Text("Typing...")
                }
            }
        }

        Divider()

        // ✉️ INPUT
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(30.dp),
                placeholder = { Text("Ask something...") }
            )

            IconButton(onClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    locale
                )

                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )

                speechLauncher.launch(intent)
            }) {
                Icon(Icons.Default.Mic, null)
            }

            Button(onClick = {

                if (inputText.isNotBlank()) {

                    val userMsg = ChatMessage(
                        chatId = chatId,
                        sender = "user",
                        message = inputText
                    )

                    chatList.add(userMsg)

                    val isFirstMessage =
                        chatList.count { it.sender == "user" } == 1

                    scope.launch(Dispatchers.IO) {

                        dao.insertMessage(userMsg)

                        if (isFirstMessage) {
                            dao.updateSessionTitle(
                                chatId,
                                inputText.take(25)
                            )
                        }
                    }

                    isLoading = true

                    // 🌍 STRONG MULTILINGUAL PROMPT
                    val finalPrompt = """
You are a helpful agriculture AI assistant.

IMPORTANT:
- Reply ONLY in the SAME language as the user.
- Do NOT translate to English.
- Keep response natural and simple.

User message:
$inputText
""".trimIndent()

                    sendChatMessage(context, finalPrompt, language) { response ->

                        val botMsg = ChatMessage(
                            chatId = chatId,
                            sender = "bot",
                            message = response
                        )

                        chatList.add(botMsg)

                        scope.launch(Dispatchers.IO) {
                            dao.insertMessage(botMsg)
                        }

                        isLoading = false
                    }

                    inputText = ""
                }

            }) {
                Text("Send")
            }
        }
    }
}