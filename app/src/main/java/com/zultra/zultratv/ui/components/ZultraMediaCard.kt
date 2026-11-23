package com.zultra.zultratv.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.zultra.zultratv.data.model.MediaType
import com.zultra.zultratv.data.model.VodItem
import com.zultra.zultratv.ui.theme.Md3ExpressiveFontFamily
import com.zultra.zultratv.ui.theme.PrimeRatingYellow
import com.zultra.zultratv.ui.theme.PrimeSurfaceCard
import com.zultra.zultratv.ui.theme.PrimeSurfaceElevated
import com.zultra.zultratv.ui.theme.PrimeTextMuted
import com.zultra.zultratv.ui.theme.PrimeTextWhite

@Composable
fun ZultraMediaCard(
    item: VodItem,
    onClick: () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.10f else 1.0f,
        label = "zultra_card_scale"
    )

    val isMusic = item.mediaType == MediaType.LOCAL_MUSIC
    val ratio = if (isMusic) 1f else 1.77f

    val cardShape = RoundedCornerShape(14.dp)

    Card(
        onClick = onClick,
        modifier = modifier
            .scale(scale)
            .aspectRatio(ratio)
            .onFocusChanged { isFocused = it.isFocused },
        shape = CardDefaults.shape(shape = cardShape),
        colors = CardDefaults.colors(containerColor = Color.Transparent),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(3.dp, accentColor),
                shape = cardShape
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(cardShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0x351E293B),
                            Color(0x200F172A)
                        )
                    )
                )
                .border(
                    1.dp,
                    if (isFocused) accentColor else Color.White.copy(alpha = 0.18f),
                    cardShape
                )
        ) {
            val imageUrl = item.backdropUrl ?: item.posterUrl
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF0F2644), Color(0xFF071426))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isMusic) "♫" else "▶",
                        fontFamily = Md3ExpressiveFontFamily,
                        fontSize = 28.sp,
                        color = PrimeTextWhite.copy(alpha = 0.8f)
                    )
                }
            }

            // Liquid Glass Frost Bottom Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xCC050A18),
                                Color(0xF0030712)
                            )
                        )
                    )
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = item.title,
                        color = PrimeTextWhite,
                        fontFamily = Md3ExpressiveFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    if (isMusic && !item.artist.isNullOrBlank()) {
                        Text(
                            text = item.artist,
                            color = PrimeTextMuted,
                            fontFamily = Md3ExpressiveFontFamily,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    } else if (item.rating != null) {
                        Text(
                            text = item.rating,
                            color = PrimeRatingYellow,
                            fontFamily = Md3ExpressiveFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Top Badge Pill: Shows clean functional tags (LIVE, 4K, VIDEO, AUDIO)
            val badge = when (item.mediaType) {
                MediaType.LIVE_TV -> "LIVE"
                MediaType.LOCAL_VIDEO -> "LOCAL"
                MediaType.LOCAL_MUSIC -> "AUDIO"
                MediaType.MOVIE -> "MOVIE"
                MediaType.SERIES -> "SERIES"
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (item.mediaType == MediaType.LIVE_TV) accentColor else Color(0x99030712))
                    .border(0.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badge,
                    color = if (item.mediaType == MediaType.LIVE_TV) Color.Black else accentColor,
                    fontFamily = Md3ExpressiveFontFamily,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
