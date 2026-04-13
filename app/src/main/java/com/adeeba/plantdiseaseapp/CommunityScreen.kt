package com.adeeba.plantdiseaseapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Post(
    val user: String,
    val message: String,
    var likes: Int = 0
)

@Composable
fun CommunityScreen() {

    var postText by remember { mutableStateOf("") }

    val posts = remember {
        mutableStateListOf(
            Post("Ravi 🌾", "My tomato leaves are turning yellow. What should I do?"),
            Post("Sita 🌱", "Use neem oil spray, it works well 👍"),
            Post("Arjun 🚜", "Check for fungal infection also"),
            Post("Farmer Raj", "Best fertilizer for wheat crop?"),
            Post("Kiran 🌿", "Try organic compost, very effective!")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔥 TITLE
        Text(
            text = "Farmer Community 💬",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ✍️ POST INPUT BOX
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            TextField(
                value = postText,
                onValueChange = { postText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask your question...") },
                shape = RoundedCornerShape(12.dp)
            )

            IconButton(
                onClick = {
                    if (postText.isNotBlank()) {
                        posts.add(0, Post("You 👤", postText))
                        postText = ""
                    }
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 📜 POSTS LIST
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(posts) { index, post ->
                PostCard(
                    post = post,
                    onLike = {
                        posts[index] = posts[index].copy(
                            likes = posts[index].likes + 1
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    onLike: () -> Unit
) {

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {

        Column(modifier = Modifier.padding(14.dp)) {

            // 👤 USER
            Text(
                text = post.user,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 💬 MESSAGE
            Text(text = post.message)

            Spacer(modifier = Modifier.height(10.dp))

            // 👍 LIKE BUTTON
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = onLike) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = Color.Red
                    )
                }

                Text(text = "${post.likes} likes")
            }
        }
    }
}