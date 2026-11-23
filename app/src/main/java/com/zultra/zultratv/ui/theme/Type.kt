package com.zultra.zultratv.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.tv.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.zultra.zultratv.R

// Material 3 Expressive Variable / Display Font Family
val Md3ExpressiveFontFamily = FontFamily(
    Font(R.font.roboto_flex, FontWeight.Normal),
    Font(R.font.roboto_flex, FontWeight.Medium),
    Font(R.font.roboto_flex, FontWeight.SemiBold),
    Font(R.font.roboto_flex, FontWeight.Bold),
    Font(R.font.roboto_flex, FontWeight.ExtraBold),
    Font(R.font.roboto_flex, FontWeight.Black)
)

val ZultraTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Md3ExpressiveFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 44.sp,
        lineHeight = 50.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = Md3ExpressiveFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        lineHeight = 38.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Md3ExpressiveFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Md3ExpressiveFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Md3ExpressiveFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Md3ExpressiveFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Md3ExpressiveFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Md3ExpressiveFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    )
)
