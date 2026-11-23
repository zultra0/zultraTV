package com.zultra.zultratv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Text
import com.zultra.zultratv.data.repository.PortalConfig
import com.zultra.zultratv.data.repository.PortalType
import com.zultra.zultratv.ui.theme.AppTheme
import com.zultra.zultratv.ui.theme.Md3ExpressiveFontFamily
import com.zultra.zultratv.ui.theme.PrimeBackground
import com.zultra.zultratv.ui.theme.PrimeTextMuted
import com.zultra.zultratv.ui.theme.PrimeTextWhite

enum class VideoResolution(val label: String) {
    AUTO("Auto (Best Available)"),
    UHD_4K("4K Ultra HD (2160p)"),
    FHD_1080P("Full HD (1080p)"),
    HD_720P("HD (720p)")
}

@Composable
fun ZultraSettingsDialog(
    currentConfig: PortalConfig?,
    currentTheme: AppTheme,
    currentResolution: VideoResolution,
    onSaveSettings: (PortalConfig, AppTheme, VideoResolution) -> Unit,
    onDismiss: () -> Unit,
    accentColor: Color
) {
    var selectedTheme by remember { mutableStateOf(currentTheme) }
    var selectedRes by remember { mutableStateOf(currentResolution) }
    var selectedType by remember { mutableStateOf(currentConfig?.type ?: PortalType.STALKER_MAC) }
    var serverUrl by remember { mutableStateOf(currentConfig?.serverUrl ?: "") }
    var macAddress by remember { mutableStateOf(currentConfig?.macAddress ?: "00:1A:79:") }
    var username by remember { mutableStateOf(currentConfig?.username ?: "") }
    var password by remember { mutableStateOf(currentConfig?.password ?: "") }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.80f)),
        contentAlignment = Alignment.Center
    ) {
        // Liquid Glass Container
        Column(
            modifier = Modifier
                .width(640.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xEE0B132B),
                            Color(0xFA070C1B)
                        )
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.40f),
                            Color.White.copy(alpha = 0.10f)
                        )
                    ),
                    RoundedCornerShape(24.dp)
                )
                .padding(28.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "ZultraTV Preferences & Portals",
                fontSize = 24.sp,
                fontFamily = Md3ExpressiveFontFamily,
                fontWeight = FontWeight.Black,
                color = PrimeTextWhite
            )
            Spacer(modifier = Modifier.height(18.dp))

            // SECTION 1: THEME ACCENT
            Text(
                text = "Theme Accent",
                color = accentColor,
                fontFamily = Md3ExpressiveFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AppTheme.values().forEach { theme ->
                    val isSel = selectedTheme == theme
                    Button(
                        onClick = { selectedTheme = theme },
                        shape = ButtonDefaults.shape(RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.colors(
                            containerColor = if (isSel) accentColor else Color(0x331E293B),
                            focusedContainerColor = if (isSel) Color.White else accentColor,
                            contentColor = if (isSel) Color.Black else PrimeTextWhite,
                            focusedContentColor = Color.Black
                        ),
                        modifier = Modifier.border(
                            1.dp,
                            if (isSel) accentColor else Color.White.copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
                        )
                    ) {
                        Text(
                            text = theme.displayName,
                            fontSize = 11.sp,
                            fontFamily = Md3ExpressiveFontFamily,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 2: STREAMING RESOLUTION
            Text(
                text = "Streaming Quality / Resolution",
                color = accentColor,
                fontFamily = Md3ExpressiveFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VideoResolution.values().forEach { res ->
                    val isSel = selectedRes == res
                    Button(
                        onClick = { selectedRes = res },
                        shape = ButtonDefaults.shape(RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.colors(
                            containerColor = if (isSel) accentColor.copy(alpha = 0.25f) else Color(0x22FFFFFF),
                            focusedContainerColor = accentColor,
                            contentColor = if (isSel) accentColor else PrimeTextWhite,
                            focusedContentColor = Color.Black
                        ),
                        modifier = Modifier.border(
                            1.dp,
                            if (isSel) accentColor else Color.White.copy(alpha = 0.12f),
                            RoundedCornerShape(12.dp)
                        )
                    ) {
                        Text(
                            text = res.label,
                            fontSize = 11.sp,
                            fontFamily = Md3ExpressiveFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // SECTION 3: IPTV PROTOCOL SWITCHER (TV Remote D-Pad friendly pill buttons)
            Text(
                text = "IPTV Protocol / Connection Type",
                color = accentColor,
                fontFamily = Md3ExpressiveFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Stalker Pill Button
                val isStalker = selectedType == PortalType.STALKER_MAC
                Button(
                    onClick = { selectedType = PortalType.STALKER_MAC },
                    shape = ButtonDefaults.shape(RoundedCornerShape(14.dp)),
                    colors = ButtonDefaults.colors(
                        containerColor = if (isStalker) accentColor else Color(0x331E293B),
                        focusedContainerColor = if (isStalker) Color.White else accentColor,
                        contentColor = if (isStalker) Color.Black else PrimeTextWhite,
                        focusedContentColor = Color.Black
                    ),
                    modifier = Modifier.border(
                        1.dp,
                        if (isStalker) accentColor else Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(14.dp)
                    )
                ) {
                    Text(
                        text = if (isStalker) "✓ Stalker / MAC" else "Stalker / MAC",
                        fontSize = 12.sp,
                        fontFamily = Md3ExpressiveFontFamily,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Xtream Codes Pill Button
                val isXtream = selectedType == PortalType.XTREAM_CODES
                Button(
                    onClick = { selectedType = PortalType.XTREAM_CODES },
                    shape = ButtonDefaults.shape(RoundedCornerShape(14.dp)),
                    colors = ButtonDefaults.colors(
                        containerColor = if (isXtream) accentColor else Color(0x331E293B),
                        focusedContainerColor = if (isXtream) Color.White else accentColor,
                        contentColor = if (isXtream) Color.Black else PrimeTextWhite,
                        focusedContentColor = Color.Black
                    ),
                    modifier = Modifier.border(
                        1.dp,
                        if (isXtream) accentColor else Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(14.dp)
                    )
                ) {
                    Text(
                        text = if (isXtream) "✓ Xtream Codes" else "Xtream Codes",
                        fontSize = 12.sp,
                        fontFamily = Md3ExpressiveFontFamily,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // M3U Link Pill Button
                val isM3u = selectedType == PortalType.M3U_URL
                Button(
                    onClick = { selectedType = PortalType.M3U_URL },
                    shape = ButtonDefaults.shape(RoundedCornerShape(14.dp)),
                    colors = ButtonDefaults.colors(
                        containerColor = if (isM3u) accentColor else Color(0x331E293B),
                        focusedContainerColor = if (isM3u) Color.White else accentColor,
                        contentColor = if (isM3u) Color.Black else PrimeTextWhite,
                        focusedContentColor = Color.Black
                    ),
                    modifier = Modifier.border(
                        1.dp,
                        if (isM3u) accentColor else Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(14.dp)
                    )
                ) {
                    Text(
                        text = if (isM3u) "✓ M3U Playlist" else "M3U Playlist",
                        fontSize = 12.sp,
                        fontFamily = Md3ExpressiveFontFamily,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DYNAMIC INPUT FIELDS BASED ON SELECTED PROTOCOL
            GlassInputField(
                label = when (selectedType) {
                    PortalType.STALKER_MAC -> "Stalker Portal URL (e.g. http://portal.example.com/c/)"
                    PortalType.XTREAM_CODES -> "Server URL (e.g. http://server.com:8080)"
                    PortalType.M3U_URL -> "M3U Playlist URL (e.g. http://server.com/playlist.m3u8)"
                },
                value = serverUrl,
                onValueChange = { serverUrl = it },
                accentColor = accentColor
            )

            if (selectedType == PortalType.STALKER_MAC) {
                Spacer(modifier = Modifier.height(12.dp))
                GlassInputField(
                    label = "YokaTV Box MAC Address (e.g. 00:1A:79:XX:XX:XX)",
                    value = macAddress,
                    onValueChange = { macAddress = it },
                    accentColor = accentColor
                )
            } else if (selectedType == PortalType.XTREAM_CODES) {
                Spacer(modifier = Modifier.height(12.dp))
                GlassInputField(
                    label = "Username",
                    value = username,
                    onValueChange = { username = it },
                    accentColor = accentColor
                )
                Spacer(modifier = Modifier.height(12.dp))
                GlassInputField(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    accentColor = accentColor
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // ACTION BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onDismiss,
                    shape = ButtonDefaults.shape(RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.colors(
                        containerColor = Color(0x33FFFFFF),
                        focusedContainerColor = PrimeTextWhite,
                        contentColor = PrimeTextWhite,
                        focusedContentColor = Color.Black
                    ),
                    modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                ) {
                    Text(text = "Cancel", fontFamily = Md3ExpressiveFontFamily, fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        val config = PortalConfig(
                            type = selectedType,
                            serverUrl = serverUrl.trim(),
                            macAddress = macAddress.trim(),
                            username = username.trim(),
                            password = password.trim()
                        )
                        onSaveSettings(config, selectedTheme, selectedRes)
                    },
                    shape = ButtonDefaults.shape(RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.colors(
                        containerColor = accentColor,
                        focusedContainerColor = Color.White,
                        contentColor = Color.Black,
                        focusedContentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Save & Apply",
                        fontFamily = Md3ExpressiveFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GlassInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    accentColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = PrimeTextMuted, fontSize = 11.sp, fontFamily = Md3ExpressiveFontFamily)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x351E293B))
                .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 9.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(color = PrimeTextWhite, fontSize = 13.sp, fontFamily = Md3ExpressiveFontFamily),
                cursorBrush = SolidColor(accentColor),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
