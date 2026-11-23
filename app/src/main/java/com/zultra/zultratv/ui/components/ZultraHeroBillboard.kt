package com.zultra.zultratv.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.zultra.zultratv.data.model.MediaType
import com.zultra.zultratv.data.model.VodItem
import com.zultra.zultratv.ui.theme.Md3ExpressiveFontFamily
import com.zultra.zultratv.ui.theme.PrimeBackground
import com.zultra.zultratv.ui.theme.PrimeRatingYellow
import com.zultra.zultratv.ui.theme.PrimeTextMuted
import com.zultra.zultratv.ui.theme.PrimeTextWhite

@Composable
fun ZultraHeroBillboard(
    item: VodItem?,
    onPlayClick: () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(440.dp)
    ) {
        AnimatedContent(
            targetState = item?.backdropUrl ?: item?.posterUrl,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "zultra_hero_bg"
        ) { url ->
            if (url != null) {
                AsyncImage(
                    model = url,
                    contentDescription = item?.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF0F2B48), PrimeBackground),
                                radius = 1200f
                            )
                        )
                )
            }
        }

        // Liquid Glass Vignette gradients (fading seamlessly into deep canvas)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Transparent,
                            0.35f to PrimeBackground.copy(alpha = 0.30f),
                            0.70f to PrimeBackground.copy(alpha = 0.85f),
                            1.0f to PrimeBackground
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0.0f to PrimeBackground.copy(alpha = 0.98f),
                            0.45f to PrimeBackground.copy(alpha = 0.60f),
                            0.85f to Color.Transparent
                        )
                    )
                )
        )

        // Details Overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 36.dp, bottom = 24.dp)
                .fillMaxWidth(0.68f)
        ) {
            val isMusic = item?.mediaType == MediaType.LOCAL_MUSIC

            // Brand Tag
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isMusic) "ZULTRA AUDIO" else "ZULTRA CINEMA",
                        color = Color.Black,
                        fontFamily = Md3ExpressiveFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isMusic) "Lossless Stream" else "Ultra HD 4K",
                    color = accentColor,
                    fontFamily = Md3ExpressiveFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = item?.title ?: "Welcome to ZultraTV",
                style = MaterialTheme.typography.displayLarge,
                color = PrimeTextWhite
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Badges row: Rating / Year / UHD / HDR
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item?.rating ?: "9.2 Score",
                    color = PrimeRatingYellow,
                    fontFamily = Md3ExpressiveFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = item?.year ?: "2024",
                    color = PrimeTextMuted,
                    fontFamily = Md3ExpressiveFontFamily,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(14.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x33FFFFFF))
                        .border(0.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "4K",
                        color = PrimeTextWhite,
                        fontFamily = Md3ExpressiveFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x33FFFFFF))
                        .border(0.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "HDR",
                        color = PrimeTextWhite,
                        fontFamily = Md3ExpressiveFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (item?.genre != null) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = item.genre,
                        color = PrimeTextMuted,
                        fontFamily = Md3ExpressiveFontFamily,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(
                text = item?.description
                    ?: "Browse your IPTV channels, movies, TV shows, and local media with automated cover matching and zero buffering.",
                style = MaterialTheme.typography.bodyLarge,
                color = PrimeTextMuted,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onPlayClick,
                    shape = ButtonDefaults.shape(RoundedCornerShape(24.dp)),
                    colors = ButtonDefaults.colors(
                        containerColor = accentColor,
                        contentColor = Color.Black,
                        focusedContainerColor = Color.White,
                        focusedContentColor = Color.Black
                    )
                ) {
                    Text(
                        text = if (isMusic) "♫ Play Track" else "▶ Watch Now",
                        fontFamily = Md3ExpressiveFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Button(
                    onClick = onPlayClick,
                    shape = ButtonDefaults.shape(RoundedCornerShape(24.dp)),
                    colors = ButtonDefaults.colors(
                        containerColor = Color(0x33FFFFFF),
                        contentColor = PrimeTextWhite,
                        focusedContainerColor = PrimeTextWhite,
                        focusedContentColor = Color.Black
                    ),
                    modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
                ) {
                    Text(
                        text = "+ Watchlist",
                        fontFamily = Md3ExpressiveFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
