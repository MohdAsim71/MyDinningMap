package com.codinglance.mydinningmap.feature.composables


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codinglance.mydinningmap.feature.JourneyStop
import com.codinglance.mydinningmap.feature.StopColors
import com.codinglance.mydinningmap.feature.StopType


// ─────────────────────────────────────────────────────────────────
//  CUSTOM MAP MARKER
//  Used inside GoogleMap { MarkerComposable(...) }
// ─────────────────────────────────────────────────────────────────

@Composable
fun JourneyMarker(
    stop: JourneyStop,
    stopNumber: Int,
    isSelected: Boolean
) {
    val color = StopColors.forType(stop.stopType)
    val size = if (isSelected) 52.dp else 42.dp
    val borderWidth = if (isSelected) 3.dp else 2.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // ── Marker bubble ─────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(size)
                .shadow(
                    elevation = if (isSelected) 12.dp else 6.dp,
                    shape = CircleShape,
                    ambientColor = color.copy(alpha = 0.3f),
                    spotColor = color.copy(alpha = 0.5f)
                )
                .clip(CircleShape)
                .background(if (isSelected) color else Color.White)
                .border(borderWidth, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            when (stop.stopType) {
                StopType.START, StopType.END -> {
                    Text(
                        text = stop.stopType.emoji,
                        fontSize = if (isSelected) 20.sp else 16.sp
                    )
                }
                else -> {
                    Text(
                        text = stopNumber.toString(),
                        fontSize = if (isSelected) 16.sp else 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else color
                    )
                }
            }
        }

        // ── Marker tail (triangle pointer) ────────────────────────
        Box(
            modifier = Modifier
                .width(if (isSelected) 3.dp else 2.dp)
                .height(if (isSelected) 10.dp else 7.dp)
                .background(color)
        )
        // Dot at base
        Box(
            modifier = Modifier
                .size(if (isSelected) 6.dp else 4.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}

// ─────────────────────────────────────────────────────────────────
//  STOP TYPE BADGE (used in sheet)
// ─────────────────────────────────────────────────────────────────

@Composable
fun StopTypeBadge(stopType: StopType) {
    val color = StopColors.forType(stopType)
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(stopType.emoji, fontSize = 12.sp)
        Text(
            stopType.label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

// ─────────────────────────────────────────────────────────────────
//  INFO ROW (icon + label + value)
// ─────────────────────────────────────────────────────────────────

@Composable
fun InfoRow(
    emoji: String,
    label: String,
    value: String,
    valueColor: Color = Color(0xFF1C1C1E)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon circle
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFFF2F2F7)),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 15.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                fontSize = 11.sp,
                color = Color(0xFF8E8E93),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.3.sp
            )
            Text(
                value,
                fontSize = 14.sp,
                color = valueColor,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 20.sp
            )
        }
    }
}