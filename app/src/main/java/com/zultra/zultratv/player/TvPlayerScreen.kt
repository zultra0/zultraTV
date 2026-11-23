package com.zultra.zultratv.player

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Text
import com.zultra.zultratv.ui.theme.Md3ExpressiveFontFamily

@OptIn(UnstableApi::class)
@Composable
fun TvPlayerScreen(
    streamUrl: String,
    onBack: () -> Unit
) {
    BackHandler(enabled = true) {
        onBack()
    }

    val context = LocalContext.current
    var playbackError by remember { mutableStateOf<String?>(null) }

    val exoPlayer = remember(streamUrl) {
        // Robust HTTP Data Source for IPTV streams with custom User-Agent and timeouts
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (QtEmbedded; U; Linux; C) AppleWebKit/533.3 (KHTML, like Gecko) MAG250 stbapp ver: 2 rev: 250 Safari/533.3")
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(20000)
            .setAllowCrossProtocolRedirects(true)

        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build().apply {
                val mediaItem = MediaItem.fromUri(streamUrl)
                setMediaItem(mediaItem)
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        playbackError = error.localizedMessage ?: "Unable to play stream (${error.errorCodeName})"
                    }

                    override fun onPlaybackStateChanged(state: Int) {
                        if (state == Player.STATE_READY) {
                            playbackError = null
                        }
                    }
                })
                prepare()
                playWhenReady = true
            }
    }

    DisposableEffect(streamUrl) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    useController = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Error Dialog overlay if stream fails to load
        if (playbackError != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "Playback Error",
                        color = Color(0xFFEF4444),
                        fontSize = 22.sp,
                        fontFamily = Md3ExpressiveFontFamily
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = playbackError ?: "",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = Md3ExpressiveFontFamily
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onBack,
                        shape = ButtonDefaults.shape(RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.colors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(text = "← Return to Browse")
                    }
                }
            }
        }
    }
}
