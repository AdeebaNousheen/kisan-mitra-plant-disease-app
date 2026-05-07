package com.adeeba.plantdiseaseapp

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.Timestamp
import java.util.*

//////////////////////////////////////////////////////////////
// 🤖 AI
//////////////////////////////////////////////////////////////

fun generateSmartAI(message: String): List<String> {
    val msg = message.lowercase()
    return when {
        "rain" in msg -> listOf("Avoid watering", "Ensure drainage", "Cover crops")
        "insects" in msg -> listOf("Use neem oil", "Soap water spray", "Remove infected leaves")
        else -> listOf("Check soil moisture", "Use compost", "Monitor plant health")
    }
}

//////////////////////////////////////////////////////////////
// 📱 MAIN SCREEN
//////////////////////////////////////////////////////////////

@Composable
fun CommunityScreen() {

    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val repo = CommunityRepository()

    var isTrending by remember { mutableStateOf(false) }
    var postText by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }

    val userName = sharedPref.getString("username", "Farmer 👨‍🌾")!!

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { imageUri = it }

    LaunchedEffect(isTrending) {
        repo.getPosts(isTrending) { posts = it }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {

        Text("Farmer Community 💬", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(10.dp))

        Row {
            Button({ isTrending = false }) { Text("Latest") }
            Spacer(Modifier.width(8.dp))
            Button({ isTrending = true }) { Text("Trending 🔥") }
        }

        Spacer(Modifier.height(10.dp))

        Row {

            TextField(
                value = postText,
                onValueChange = { postText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask your question...") }
            )

            IconButton({ launcher.launch("image/*") }) {
                Icon(Icons.Default.Image, null)
            }

            IconButton({

                val uid = auth.currentUser?.uid ?: ""
                val postRef = db.collection("community_posts").document()

                fun upload(imageUrl: String) {

                    val data = hashMapOf(
                        "userId" to uid,
                        "username" to userName,
                        "message" to postText,
                        "imageUrl" to imageUrl,
                        "likes" to 0,
                        "likedBy" to listOf<String>(),
                        "bestReplyId" to "",
                        "timestamp" to Timestamp.now()
                    )

                    postRef.set(data)

                    // 🤖 AI (1 reply only)
                    if (postText.contains("?")) {
                        generateSmartAI(postText).take(1).forEach {
                            postRef.collection("replies").add(
                                hashMapOf(
                                    "username" to "🌱 AgriBot",
                                    "userId" to "AI",
                                    "message" to it,
                                    "likes" to 0,
                                    "likedBy" to listOf<String>(),
                                    "type" to "ai"
                                )
                            )
                        }
                    }

                    postText = ""
                    imageUri = null
                }

                if (imageUri != null) {
                    val ref = storage.reference.child("posts/${UUID.randomUUID()}")
                    ref.putFile(imageUri!!)
                        .addOnSuccessListener {
                            ref.downloadUrl.addOnSuccessListener {
                                upload(it.toString())
                            }
                        }
                } else upload("")

            }) {
                Icon(Icons.Default.Send, null)
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(posts, key = { it.id }) {
                PostCard(it, db, userName)
            }
        }
    }
}

//////////////////////////////////////////////////////////////
// 🔥 POST CARD
//////////////////////////////////////////////////////////////

@Composable
fun PostCard(post: Post, db: FirebaseFirestore, currentUserName: String) {

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var liked by remember { mutableStateOf(post.likedBy.contains(uid)) }
    var showDelete by remember { mutableStateOf(false) }
    var showReply by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }
    var replies by remember { mutableStateOf<List<Reply>>(emptyList()) }

    // 🔥 LOAD REPLIES
    LaunchedEffect(post.id) {
        db.collection("community_posts")
            .document(post.id)
            .collection("replies")
            .addSnapshotListener { snap, _ ->
                replies = snap?.documents?.map { doc ->
                    Reply(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        username = doc.getString("username") ?: "",
                        message = doc.getString("message") ?: "",
                        likes = (doc.getLong("likes") ?: 0).toInt(),
                        likedBy = doc.get("likedBy") as? List<String> ?: emptyList(),
                        isBest = doc.id == post.bestReplyId,
                        type = doc.getString("type") ?: "user"
                    )
                } ?: emptyList()
            }
    }

    // 🔥 DELETE
    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            confirmButton = {
                Button({
                    db.collection("community_posts").document(post.id).delete()
                    showDelete = false
                }) { Text("Delete") }
            },
            dismissButton = {
                OutlinedButton({ showDelete = false }) { Text("Cancel") }
            },
            title = { Text("Delete Post") },
            text = { Text("Are you sure?") }
        )
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDEDED)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    if (post.userId == uid) showDelete = true
                }
            )
    ) {

        Column(Modifier.padding(14.dp)) {

            Text(post.username)
            Spacer(Modifier.height(6.dp))
            Text(post.message)

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {

                IconButton({
                    val ref = db.collection("community_posts").document(post.id)

                    if (liked) {
                        ref.update("likes", post.likes - 1,
                            "likedBy", FieldValue.arrayRemove(uid))
                    } else {
                        ref.update("likes", post.likes + 1,
                            "likedBy", FieldValue.arrayUnion(uid))
                    }

                    liked = !liked
                }) {
                    Icon(Icons.Default.Favorite, null,
                        tint = if (liked) Color.Red else Color.Gray)
                }

                Text("${post.likes}")

                Spacer(Modifier.width(12.dp))

                TextButton({ showReply = !showReply }) {
                    Text("Reply")
                }
            }

            if (showReply) {

                Spacer(Modifier.height(10.dp))
                Divider()
                Spacer(Modifier.height(8.dp))

                Row {
                    TextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Write reply...") }
                    )

                    IconButton({

                        // 🔥 RANDOM USERNAMES (FIX)
                        val demoNames = listOf(
                            "Ravi 🌾", "Sita 🌸", "Arjun 🚜", "Kiran 🌱", "Meena 🍀"
                        )

                        val randomName = demoNames.random()

                        val data = hashMapOf(
                            "username" to randomName,
                            "userId" to uid,
                            "message" to replyText,
                            "timestamp" to Timestamp.now(),
                            "likes" to 0,
                            "likedBy" to listOf<String>(),
                            "type" to "user"
                        )

                        db.collection("community_posts")
                            .document(post.id)
                            .collection("replies")
                            .add(data)

                        replyText = ""

                    }) {
                        Icon(Icons.Default.Send, null)
                    }
                }

                Spacer(Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    replies.sortedByDescending { it.isBest }
                        .forEach { reply ->

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {

                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFEDEDED) // ✅ MATCH POST COLOR
                                    ),
                                    modifier = Modifier.widthIn(max = 260.dp)
                                ) {

                                    Column(Modifier.padding(10.dp)) {

                                        Text(reply.username)

                                        if (reply.isBest) {
                                            Text("⭐ Best",
                                                color = Color(0xFFFF9800))
                                        }

                                        Spacer(Modifier.height(4.dp))

                                        Text(reply.message)
                                    }
                                }
                            }
                        }
                }
            }
        }
    }
}