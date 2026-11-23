package com.zultra.zultratv.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.zultra.zultratv.data.model.MediaCategory
import com.zultra.zultratv.data.model.MediaType
import com.zultra.zultratv.data.model.VodItem
import com.zultra.zultratv.ui.components.LiquidGlassSideNav
import com.zultra.zultratv.ui.components.ZultraHeroBillboard
import com.zultra.zultratv.ui.components.ZultraMediaCard
import com.zultra.zultratv.ui.theme.Md3ExpressiveFontFamily
import com.zultra.zultratv.ui.theme.PrimeBackground
import com.zultra.zultratv.ui.theme.PrimeTextMuted
import com.zultra.zultratv.ui.theme.PrimeTextWhite

enum class HomeTab {
    SEARCH,
    ALL,
    IPTV_LIVE,
    MOVIES_SERIES,
    LOCAL_MEDIA
}

@Composable
fun ZultraMainView(
    categories: List<MediaCategory>,
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
    onItemSelected: (VodItem) -> Unit,
    onOpenSettings: () -> Unit,
    accentColor: Color
) {
    var highlightedItem by remember(categories) {
        mutableStateOf(categories.firstOrNull()?.items?.firstOrNull())
    }

    // State for viewing full category grid
    var expandedCategory by remember { mutableStateOf<MediaCategory?>(null) }

    // Search query state
    var searchQuery by remember { mutableStateOf("") }

    // Intercept Back button when inside category full view to return to main browse
    BackHandler(enabled = expandedCategory != null || (selectedTab == HomeTab.SEARCH && searchQuery.isNotEmpty())) {
        if (expandedCategory != null) {
            expandedCategory = null
        } else {
            searchQuery = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0A1428),
                        PrimeBackground
                    ),
                    radius = 1600f
                )
            )
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // 1. COLLAPSIBLE / MINIMIZABLE LIQUID GLASS SIDEBAR
            LiquidGlassSideNav(
                selectedTab = selectedTab,
                onTabSelected = {
                    expandedCategory = null
                    onTabSelected(it)
                },
                onOpenSettings = onOpenSettings,
                accentColor = accentColor
            )

            // 2. MAIN CONTENT AREA
            if (selectedTab == HomeTab.SEARCH) {
                // DEDICATED SEARCH VIEW
                val allItems = remember(categories) {
                    categories.flatMap { it.items }.distinctBy { it.id }
                }
                val searchResults = remember(searchQuery, allItems) {
                    if (searchQuery.isBlank()) emptyList()
                    else allItems.filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        (it.genre?.contains(searchQuery, ignoreCase = true) == true) ||
                        (it.artist?.contains(searchQuery, ignoreCase = true) == true)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(start = 28.dp, end = 28.dp, top = 24.dp)
                ) {
                    Text(
                        text = "Search Media & Channels",
                        color = PrimeTextWhite,
                        style = MaterialTheme.typography.displayMedium
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Liquid Glass Search Input Field
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x351E293B))
                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            textStyle = TextStyle(
                                color = PrimeTextWhite,
                                fontSize = 16.sp,
                                fontFamily = Md3ExpressiveFontFamily
                            ),
                            cursorBrush = SolidColor(accentColor),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Type title, channel, artist, or genre...",
                                        color = PrimeTextMuted,
                                        fontFamily = Md3ExpressiveFontFamily,
                                        fontSize = 15.sp
                                    )
                                }
                                innerTextField()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (searchQuery.isBlank()) {
                        Text(
                            text = "Search across all your IPTV live channels, VOD movies, local videos, and songs.",
                            color = PrimeTextMuted,
                            fontFamily = Md3ExpressiveFontFamily,
                            fontSize = 14.sp
                        )
                    } else if (searchResults.isEmpty()) {
                        Text(
                            text = "No results found for \"$searchQuery\"",
                            color = PrimeTextMuted,
                            fontFamily = Md3ExpressiveFontFamily,
                            fontSize = 14.sp
                        )
                    } else {
                        Text(
                            text = "Found ${searchResults.size} results:",
                            color = accentColor,
                            fontFamily = Md3ExpressiveFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 200.dp),
                            contentPadding = PaddingValues(bottom = 48.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(searchResults) { item ->
                                ZultraMediaCard(
                                    item = item,
                                    onClick = { onItemSelected(item) },
                                    accentColor = accentColor,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            } else if (expandedCategory != null) {
                // FULL CATEGORY GRID VIEW
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(start = 28.dp, end = 28.dp, top = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { expandedCategory = null },
                            shape = ButtonDefaults.shape(RoundedCornerShape(16.dp)),
                            colors = ButtonDefaults.colors(
                                containerColor = Color(0x33FFFFFF),
                                focusedContainerColor = accentColor,
                                contentColor = PrimeTextWhite,
                                focusedContentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "← Back",
                                fontFamily = Md3ExpressiveFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(18.dp))

                        Text(
                            text = expandedCategory!!.title,
                            color = PrimeTextWhite,
                            style = MaterialTheme.typography.displayMedium
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "(${expandedCategory!!.items.size} titles)",
                            color = PrimeTextMuted,
                            fontFamily = Md3ExpressiveFontFamily,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 200.dp),
                        contentPadding = PaddingValues(bottom = 48.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(expandedCategory!!.items) { item ->
                            ZultraMediaCard(
                                item = item,
                                onClick = { onItemSelected(item) },
                                accentColor = accentColor,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            } else {
                // NORMAL DASHBOARD BROWSE VIEW
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp)
                ) {
                    if (selectedTab == HomeTab.ALL) {
                        item {
                            ZultraHeroBillboard(
                                item = highlightedItem,
                                onPlayClick = {
                                    highlightedItem?.let(onItemSelected)
                                },
                                accentColor = accentColor
                            )
                        }
                    } else {
                        item {
                            val tabTitle = when (selectedTab) {
                                HomeTab.IPTV_LIVE -> "Live Channels"
                                HomeTab.MOVIES_SERIES -> "Movies, Shows & On-Demand"
                                HomeTab.LOCAL_MEDIA -> "My Local Media & Music"
                                else -> "Featured Content"
                            }

                            Text(
                                text = tabTitle,
                                color = PrimeTextWhite,
                                style = MaterialTheme.typography.displayMedium,
                                modifier = Modifier.padding(start = 28.dp, top = 20.dp, bottom = 10.dp)
                            )
                        }
                    }

                    // Filtered Content Rails
                    val filteredCategories = categories.filter { cat ->
                        when (selectedTab) {
                            HomeTab.ALL -> true
                            HomeTab.IPTV_LIVE -> cat.items.any { it.mediaType == MediaType.LIVE_TV }
                            HomeTab.MOVIES_SERIES -> cat.items.any { it.mediaType == MediaType.MOVIE || it.mediaType == MediaType.SERIES }
                            HomeTab.LOCAL_MEDIA -> cat.items.any { it.mediaType == MediaType.LOCAL_VIDEO || it.mediaType == MediaType.LOCAL_MUSIC }
                            HomeTab.SEARCH -> false
                        }
                    }

                    items(filteredCategories) { category ->
                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { expandedCategory = category },
                            shape = ButtonDefaults.shape(RoundedCornerShape(8.dp)),
                            colors = ButtonDefaults.colors(
                                containerColor = Color.Transparent,
                                focusedContainerColor = Color(0x33FFFFFF),
                                contentColor = PrimeTextWhite,
                                focusedContentColor = accentColor
                            ),
                            modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = category.title,
                                    color = PrimeTextWhite,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "›",
                                    color = accentColor,
                                    fontFamily = Md3ExpressiveFontFamily,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "See All (${category.items.size})",
                                    color = PrimeTextMuted,
                                    fontFamily = Md3ExpressiveFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 28.dp)
                        ) {
                            items(category.items) { item ->
                                val isMusic = item.mediaType == MediaType.LOCAL_MUSIC
                                ZultraMediaCard(
                                    item = item,
                                    onClick = {
                                        highlightedItem = item
                                        onItemSelected(item)
                                    },
                                    accentColor = accentColor,
                                    modifier = Modifier
                                        .width(if (isMusic) 160.dp else 220.dp)
                                        .padding(end = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
