package com.zultra.zultratv.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Text
import com.zultra.zultratv.ui.screens.HomeTab
import com.zultra.zultratv.ui.theme.Md3ExpressiveFontFamily
import com.zultra.zultratv.ui.theme.PrimeTextMuted
import com.zultra.zultratv.ui.theme.PrimeTextWhite

@Composable
fun LiquidGlassSideNav(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
    onOpenSettings: () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val navWidth by animateDpAsState(
        targetValue = if (isExpanded) 220.dp else 78.dp,
        label = "nav_width"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(navWidth)
            .padding(12.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0x351E293B),
                        Color(0x180F172A),
                        Color(0x22131D33)
                    )
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.08f)
                    )
                ),
                RoundedCornerShape(24.dp)
            )
            .onFocusChanged {
                isExpanded = it.hasFocus
            }
            .padding(vertical = 20.dp, horizontal = 10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // App Brand
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
            ) {
                if (isExpanded) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Zultra",
                            color = PrimeTextWhite,
                            fontFamily = Md3ExpressiveFontFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "TV",
                            color = accentColor,
                            fontFamily = Md3ExpressiveFontFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(accentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Z",
                            color = Color.Black,
                            fontFamily = Md3ExpressiveFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Navigation items (Search, Home, Live TV, Movies & TV, My Media)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SideNavItem(
                    icon = "🔍",
                    title = "Search",
                    isSelected = selectedTab == HomeTab.SEARCH,
                    isExpanded = isExpanded,
                    accentColor = accentColor,
                    onClick = { onTabSelected(HomeTab.SEARCH) }
                )
                SideNavItem(
                    icon = "⌂",
                    title = "Home",
                    isSelected = selectedTab == HomeTab.ALL,
                    isExpanded = isExpanded,
                    accentColor = accentColor,
                    onClick = { onTabSelected(HomeTab.ALL) }
                )
                SideNavItem(
                    icon = "◉",
                    title = "Live TV",
                    isSelected = selectedTab == HomeTab.IPTV_LIVE,
                    isExpanded = isExpanded,
                    accentColor = accentColor,
                    onClick = { onTabSelected(HomeTab.IPTV_LIVE) }
                )
                SideNavItem(
                    icon = "▶",
                    title = "Movies & TV",
                    isSelected = selectedTab == HomeTab.MOVIES_SERIES,
                    isExpanded = isExpanded,
                    accentColor = accentColor,
                    onClick = { onTabSelected(HomeTab.MOVIES_SERIES) }
                )
                SideNavItem(
                    icon = "♫",
                    title = "My Media",
                    isSelected = selectedTab == HomeTab.LOCAL_MEDIA,
                    isExpanded = isExpanded,
                    accentColor = accentColor,
                    onClick = { onTabSelected(HomeTab.LOCAL_MEDIA) }
                )
            }

            // Bottom Settings
            SideNavItem(
                icon = "⚙",
                title = "Settings",
                isSelected = false,
                isExpanded = isExpanded,
                accentColor = accentColor,
                onClick = onOpenSettings
            )
        }
    }
}

@Composable
fun SideNavItem(
    icon: String,
    title: String,
    isSelected: Boolean,
    isExpanded: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused },
        shape = ButtonDefaults.shape(RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.colors(
            containerColor = if (isSelected) accentColor.copy(alpha = 0.25f) else Color.Transparent,
            focusedContainerColor = accentColor,
            contentColor = if (isSelected) accentColor else PrimeTextMuted,
            focusedContentColor = Color.Black
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.width(36.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isFocused) Color.Black else if (isSelected) accentColor else PrimeTextWhite
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontFamily = Md3ExpressiveFontFamily,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    color = if (isFocused) Color.Black else if (isSelected) PrimeTextWhite else PrimeTextMuted
                )
            }
        }
    }
}
