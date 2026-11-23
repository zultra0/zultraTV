package com.zultra.zultratv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.Text
import com.zultra.zultratv.data.local.LocalMediaScanner
import com.zultra.zultratv.data.model.MediaCategory
import com.zultra.zultratv.data.model.MediaType
import com.zultra.zultratv.data.model.VodItem
import com.zultra.zultratv.data.repository.IptvRepository
import com.zultra.zultratv.player.TvPlayerScreen
import com.zultra.zultratv.ui.screens.HomeTab
import com.zultra.zultratv.ui.screens.VideoResolution
import com.zultra.zultratv.ui.screens.ZultraMainView
import com.zultra.zultratv.ui.screens.ZultraSettingsDialog
import com.zultra.zultratv.ui.theme.AppTheme
import com.zultra.zultratv.ui.theme.PrimeBackground
import com.zultra.zultratv.ui.theme.PrimeBrightCyan
import com.zultra.zultratv.ui.theme.PrimeElectricBlue
import com.zultra.zultratv.ui.theme.PrimeTextWhite
import com.zultra.zultratv.ui.theme.ZultraTVTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = IptvRepository(applicationContext)
        val localScanner = LocalMediaScanner(applicationContext)

        setContent {
            ZultraTVTheme {
                val scope = rememberCoroutineScope()
                var currentPortal by remember { mutableStateOf(repository.getSavedPortal()) }
                var showSettings by remember { mutableStateOf(false) }
                var selectedStream by remember { mutableStateOf<String?>(null) }
                var categories by remember { mutableStateOf<List<MediaCategory>>(emptyList()) }
                var selectedTab by remember { mutableStateOf(HomeTab.ALL) }
                var currentTheme by remember { mutableStateOf(AppTheme.PRIME_BLUE) }
                var currentResolution by remember { mutableStateOf(VideoResolution.AUTO) }
                var isLoading by remember { mutableStateOf(false) }

                val accentColor = when (currentTheme) {
                    AppTheme.PRIME_BLUE -> PrimeElectricBlue
                    AppTheme.NETFLIX_RED -> Color(0xFFE50914)
                    AppTheme.AMERALD_GREEN -> Color(0xFF10B981)
                    AppTheme.PURPLE_NEON -> Color(0xFFA855F7)
                }

                // Generic, unbranded showcase media
                val showcaseCategories = remember {
                    listOf(
                        MediaCategory(
                            title = "Live Channels",
                            items = listOf(
                                VodItem(
                                    id = "live_1",
                                    title = "Global News HD",
                                    posterUrl = "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?w=500",
                                    backdropUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=1280",
                                    streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
                                    rating = "99% Match",
                                    year = "LIVE",
                                    genre = "News & Info",
                                    description = "24/7 International news broadcasts and live world event coverage.",
                                    mediaType = MediaType.LIVE_TV
                                ),
                                VodItem(
                                    id = "live_2",
                                    title = "World Sports Network",
                                    posterUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=500",
                                    backdropUrl = "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=1280",
                                    streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
                                    rating = "96% Match",
                                    year = "LIVE",
                                    genre = "Sports",
                                    description = "Live coverage of championship events, highlights, and tournaments.",
                                    mediaType = MediaType.LIVE_TV
                                )
                            )
                        ),
                        MediaCategory(
                            title = "Featured Movies & Series",
                            items = listOf(
                                VodItem(
                                    id = "vod_1",
                                    title = "Interstellar Horizons",
                                    posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500",
                                    backdropUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1280",
                                    streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                                    rating = "98% Match",
                                    year = "2024",
                                    genre = "Sci-Fi",
                                    description = "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
                                    mediaType = MediaType.MOVIE
                                ),
                                VodItem(
                                    id = "vod_2",
                                    title = "Deep Space Odyssey",
                                    posterUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=500",
                                    backdropUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1280",
                                    streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                                    rating = "95% Match",
                                    year = "2023",
                                    genre = "Mystery",
                                    description = "Unraveling classified transmissions hidden inside deep space telemetry.",
                                    mediaType = MediaType.SERIES
                                )
                            )
                        )
                    )
                }

                fun refreshAllMedia() {
                    scope.launch {
                        isLoading = true
                        try {
                            val combinedList = mutableListOf<MediaCategory>()

                            // 1. Scan Local Storage Video & Query Movie Database
                            try {
                                val localVids = localScanner.scanLocalVideos()
                                if (localVids.items.isNotEmpty()) {
                                    combinedList.add(localVids)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            // 2. Scan Local Storage Music & Query Album Database
                            try {
                                val localSongs = localScanner.scanLocalMusic()
                                if (localSongs.items.isNotEmpty()) {
                                    combinedList.add(localSongs)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            // 3. Fetch remote IPTV portal (Stalker / Xtream / M3U)
                            if (currentPortal != null) {
                                try {
                                    val portalContent = repository.loadContent()
                                    combinedList.addAll(portalContent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            if (combinedList.isEmpty()) {
                                combinedList.addAll(showcaseCategories)
                            }

                            categories = combinedList
                        } finally {
                            isLoading = false
                        }
                    }
                }

                LaunchedEffect(currentPortal) {
                    refreshAllMedia()
                }

                when {
                    selectedStream != null -> {
                        TvPlayerScreen(
                            streamUrl = selectedStream!!,
                            onBack = { selectedStream = null }
                        )
                    }
                    else -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            ZultraMainView(
                                categories = categories,
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it },
                                onItemSelected = { item ->
                                    selectedStream = item.streamUrl
                                },
                                onOpenSettings = {
                                    showSettings = true
                                },
                                accentColor = accentColor
                            )

                            if (showSettings) {
                                ZultraSettingsDialog(
                                    currentConfig = currentPortal,
                                    currentTheme = currentTheme,
                                    currentResolution = currentResolution,
                                    onSaveSettings = { config, theme, res ->
                                        repository.savePortal(config)
                                        currentPortal = config
                                        currentTheme = theme
                                        currentResolution = res
                                        showSettings = false
                                    },
                                    onDismiss = {
                                        showSettings = false
                                    },
                                    accentColor = accentColor
                                )
                            }

                            if (isLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(PrimeBackground.copy(alpha = 0.7f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Syncing ZultraTV & Media Portals...",
                                        color = PrimeTextWhite
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
