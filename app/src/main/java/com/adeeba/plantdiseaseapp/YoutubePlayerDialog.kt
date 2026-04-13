package com.adeeba.plantdiseaseapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import androidx.compose.ui.window.Dialog

@Composable
fun YouTubePlayerDialog(
    videoId: String,
    onDismiss: () -> Unit
) {

    Dialog(onDismissRequest = { onDismiss() }) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.8f)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.Black)
            ) {

                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->

                        val view = YouTubePlayerView(context)

                        view.addYouTubePlayerListener(
                            object : AbstractYouTubePlayerListener() {
                                override fun onReady(player: YouTubePlayer) {
                                    player.loadVideo(videoId, 0f)
                                }
                            }
                        )

                        view
                    }
                )

                IconButton(
                    onClick = { onDismiss() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Close, null, tint = Color.White)
                }
            }
        }
    }
}