package com.codinglance.mydinningmap.feature.composables


import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.codinglance.mydinningmap.DateUtils
import com.codinglance.mydinningmap.DistanceUtils
import com.codinglance.mydinningmap.feature.Journey
import com.codinglance.mydinningmap.feature.JourneyStop
import com.codinglance.mydinningmap.feature.StopColors
import com.codinglance.mydinningmap.feature.StopType


// ─────────────────────────────────────────────────────────────────
//  STOP DETAIL BOTTOM SHEET
// ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopDetailSheet(
    stop: JourneyStop,
    stopNumber: Int,
    totalStops: Int,
    journey: Journey,
    onDismiss: () -> Unit,
    onPrevStop: () -> Unit,
    onNextStop: () -> Unit,
    hasPrev: Boolean,
    hasNext: Boolean
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val color = StopColors.forType(stop.stopType)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = {
            // Custom drag handle with color accent
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD1D1D6))
                )
                Spacer(Modifier.height(12.dp))
                // Color accent line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(color.copy(alpha = 0.15f))
                )
            }
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ── Header ────────────────────────────────────────────
            SheetHeader(stop, stopNumber, totalStops, color)

            HorizontalDivider(color = Color(0xFFF2F2F7), thickness = 1.dp)

            // ── Info rows ─────────────────────────────────────────
            InfoSection(stop)

            // ── Notes section ─────────────────────────────────────
            if (stop.notes.isNotBlank()) {
                NotesSection(stop.notes)
            }

            HorizontalDivider(color = Color(0xFFF2F2F7), thickness = 1.dp)

            // ── Navigation between stops ──────────────────────────
            StopNavigation(
                stop, stopNumber, totalStops,
                onPrevStop, onNextStop,
                hasPrev, hasNext, color
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────

@Composable
private fun SheetHeader(
    stop: JourneyStop,
    stopNumber: Int,
    totalStops: Int,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Stop number badge
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f))
                .border(2.dp, color.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            when (stop.stopType) {
                StopType.START, StopType.END ->
                    Text(stop.stopType.emoji, fontSize = 20.sp)
                else ->
                    Text(
                        stopNumber.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = color
                    )
            }
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Journey breadcrumb
            Text(
                "Stop $stopNumber of $totalStops",
                fontSize = 11.sp,
                color = Color(0xFF8E8E93),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
            // Stop title
            Text(
                stop.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1C1E),
                lineHeight = 26.sp
            )
            // Stop type badge
            StopTypeBadge(stop.stopType)
        }
    }
}

// ─────────────────────────────────────────────────────────────────

@Composable
private fun InfoSection(stop: JourneyStop) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

        // Date & Time
        InfoRow(
            emoji = "🕐",
            label = "DATE & TIME",
            value = DateUtils.formatDateTime(stop.timestamp)
        )

        // Address
        InfoRow(
            emoji = "📍",
            label = "ADDRESS",
            value = stop.address
        )

        // Distance from previous
        if (stop.distanceFromPrev > 0f) {
            InfoRow(
                emoji = "📏",
                label = "DISTANCE FROM PREVIOUS STOP",
                value = DistanceUtils.formatDistance(stop.distanceFromPrev),
                valueColor = Color(0xFF007AFF)
            )
        }

        // Duration at stop
        if (stop.durationAtStop > 0) {
            InfoRow(
                emoji = "⏱️",
                label = "TIME SPENT HERE",
                value = DateUtils.formatDuration(stop.durationAtStop)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────

@Composable
private fun NotesSection(notes: String) {
    var isExpanded by remember { mutableStateOf(false) }
    val isLong = notes.length > 120

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("📝", fontSize = 15.sp)
            Text(
                "NOTES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF8E8E93),
                letterSpacing = 0.5.sp
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF9F9FB))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = notes,
                    fontSize = 14.sp,
                    color = Color(0xFF3C3C43),
                    lineHeight = 22.sp,
                    maxLines = if (isExpanded || !isLong) Int.MAX_VALUE else 3,
                    overflow = if (isExpanded || !isLong) TextOverflow.Visible else TextOverflow.Ellipsis
                )
                if (isLong) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = if (isExpanded) "Show less" else "Read more",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF007AFF),
                        modifier = Modifier.clickable { isExpanded = !isExpanded }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────

@Composable
private fun StopNavigation(
    stop: JourneyStop,
    stopNumber: Int,
    totalStops: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    hasPrev: Boolean,
    hasNext: Boolean,
    color: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFFF2F2F7))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(stopNumber.toFloat() / totalStops)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }

        // Prev / Next buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Prev
            OutlinedButton(
                onClick = onPrev,
                enabled = hasPrev,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = color,
                    disabledContentColor = Color(0xFFC7C7CC)
                ),
                border = BorderStroke(
                    1.5.dp,
                    if (hasPrev) color.copy(alpha = 0.4f) else Color(0xFFE5E5EA)
                )
            ) {
                Text("← Prev", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }

            // Next
            Button(
                onClick = onNext,
                enabled = hasNext,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = color,
                    disabledContainerColor = Color(0xFFE5E5EA),
                    disabledContentColor = Color(0xFFC7C7CC)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Text("Next →", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
}