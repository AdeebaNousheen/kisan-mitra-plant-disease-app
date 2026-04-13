package com.adeeba.plantdiseaseapp

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import com.adeeba.plantdiseaseapp.entity.DetectionEntity

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val context = LocaleHelper.setLocale(newBase, "te")
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            val context = this
            val scope = rememberCoroutineScope()

            var language by remember { mutableStateOf(getLanguage(context)) }
            var selectedScreen by remember { mutableStateOf("language") }
            var selectedChatId by remember { mutableStateOf<Long?>(null) }

            var savedCrops by remember { mutableStateOf(listOf<String>()) }
            var selectedCrop by remember { mutableStateOf("Tomato") }

            // 🔥 HISTORY SELECTED ITEM
            var selectedDetection by remember { mutableStateOf<DetectionEntity?>(null) }

            LaunchedEffect(Unit) {
                savedCrops = CropStorage.getCrops(context)
            }

            val drawerState = rememberDrawerState(DrawerValue.Closed)

            // RESULT STATE
            var disease by remember { mutableStateOf("") }
            var confidence by remember { mutableStateOf(0.0) }
            var description by remember { mutableStateOf("") }
            var treatment by remember { mutableStateOf("") }
            var prevention by remember { mutableStateOf("") }

            AppTheme {

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {

                        ModalDrawerSheet {

                            // ✅ HISTORY BUTTON
                            TextButton(
                                onClick = {
                                    selectedScreen = "history"
                                    scope.launch { drawerState.close() }
                                }
                            ) {
                                Text("📊 Scan History")
                            }

                            Divider()

                            ChatHistoryScreen(
                                onOpenChat = {
                                    selectedChatId = it
                                    selectedScreen = "assistant"
                                    scope.launch { drawerState.close() }
                                },
                                onNewChat = {
                                    selectedChatId = it
                                    selectedScreen = "assistant"
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }
                    }
                ) {

                    Scaffold(

                        // 🔝 TOP BAR
                        topBar = {
                            if (selectedScreen == "assistant") {
                                TopAppBar(
                                    title = { Text("AI Assistant") },
                                    navigationIcon = {
                                        Row {

                                            IconButton(onClick = {
                                                selectedScreen = "crops"
                                            }) {
                                                Icon(Icons.Default.ArrowBack, null)
                                            }

                                            IconButton(onClick = {
                                                scope.launch { drawerState.open() }
                                            }) {
                                                Icon(Icons.Default.Menu, null)
                                            }
                                        }
                                    }
                                )
                            }
                        },

                        // 🔽 BOTTOM NAV
                        bottomBar = {
                            if (selectedScreen != "assistant") {
                                NavigationBar {

                                    NavigationBarItem(
                                        selected = selectedScreen == "crops",
                                        onClick = { selectedScreen = "crops" },
                                        icon = { Icon(Icons.Default.Spa, null) },
                                        label = { Text("Crops") }
                                    )

                                    NavigationBarItem(
                                        selected = selectedScreen == "videos",
                                        onClick = { selectedScreen = "videos" },
                                        icon = { Icon(Icons.Default.PlayCircle, null) },
                                        label = { Text("Videos") }
                                    )

                                    NavigationBarItem(
                                        selected = selectedScreen == "community",
                                        onClick = { selectedScreen = "community" },
                                        icon = { Icon(Icons.Default.Group, null) },
                                        label = { Text("Community") }
                                    )

                                    NavigationBarItem(
                                        selected = selectedScreen == "profile",
                                        onClick = { selectedScreen = "profile" },
                                        icon = { Icon(Icons.Default.Person, null) },
                                        label = { Text("Profile") }
                                    )
                                }
                            }
                        }

                    ) { padding ->

                        Surface(modifier = Modifier.padding(padding)) {

                            when (selectedScreen) {

                                "language" -> LanguageScreen(
                                    context = context,
                                    onContinue = {
                                        language = getLanguage(context)
                                        selectedScreen = "crops"
                                    }
                                )

                                "crops" -> CropsScreen(
                                    selectedCrops = savedCrops,
                                    onScanClick = { crop ->
                                        selectedCrop = crop
                                        selectedScreen = "scan"
                                    },
                                    onAddCropClick = { selectedScreen = "selectCrops" },
                                    onAssistantClick = { selectedScreen = "assistant" },
                                    onToolClick = { selectedScreen = it },
                                    language = language
                                )

                                "selectCrops" -> SelectCropsScreen { crops ->
                                    CropStorage.saveCrops(context, crops)
                                    savedCrops = CropStorage.getCrops(context)
                                    selectedScreen = "crops"
                                }

                                "assistant" -> AssistantScreen(
                                    language = language,
                                    chatId = selectedChatId ?: 0,
                                    onBack = { selectedScreen = "crops" },
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    }
                                )

                                // 📷 CAMERA
                                "scan" -> CameraScreen(
                                    language = language,
                                    selectedCrop = selectedCrop,
                                    onResult = { d, conf, desc, treat, prev ->
                                        disease = d
                                        confidence = conf
                                        description = desc
                                        treatment = treat
                                        prevention = prev
                                        selectedScreen = "result"
                                    },
                                    onBack = { selectedScreen = "crops" }
                                )

                                // 📊 RESULT FROM CAMERA
                                "result" -> DiseaseResultScreen(
                                    imagePath = "",
                                    disease = disease,
                                    confidence = confidence,
                                    description = description,
                                    treatment = treatment,
                                    prevention = prevention,
                                    onScanAgain = { selectedScreen = "scan" },
                                    onBack = { selectedScreen = "crops" },
                                    language = language
                                )

                                // 📜 HISTORY LIST
                                "history" -> DetectionHistoryScreen(
                                    onItemClick = { item ->
                                        selectedDetection = item
                                        selectedScreen = "history_result"
                                    },
                                    onBack = { selectedScreen = "crops" }
                                )

                                // 🔥 FIXED (IMPORTANT)
                                "history_result" -> {
                                    selectedDetection?.let {
                                        DiseaseResultScreen(
                                            imagePath = "",
                                            disease = it.name,
                                            confidence = it.confidence,
                                            description = it.description,
                                            treatment = it.treatment,
                                            prevention = it.prevention,
                                            onScanAgain = { selectedScreen = "scan" },
                                            onBack = { selectedScreen = "history" },
                                            language = language
                                        )
                                    } ?: run {
                                        // ✅ PREVENT BLANK SCREEN
                                        selectedScreen = "history"
                                    }
                                }

                                "videos" -> VideoScreen()
                                "community" -> CommunityScreen()

                                // 🔥 PROFILE CLICK → RESULT (FIXED)
                                "profile" -> ProfileScreen { name, conf, desc, treat, prev ->

                                    disease = name
                                    confidence = conf
                                    description = desc
                                    treatment = treat
                                    prevention = prev

                                    selectedScreen = "result"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}