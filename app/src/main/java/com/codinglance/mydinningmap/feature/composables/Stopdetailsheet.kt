package com.codinglance.mydinningmap.feature.composables


import android.R
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
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
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        tonalElevation = 0.dp
    ) {
        SheetContent(
            stop,
            stopNumber,
            totalStops,
            journey,
            onDismiss,
            onPrevStop,
            onNextStop,
            hasPrev,
            hasNext
        )
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SheetContent(
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
    val color = StopColors.forType(stop.stopType)

    Box(
        Modifier
            .background(Color.White)
            .clip(shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .border(
                width = 2.dp,
                color = color,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
    ) {
        GlideImage(
            model = "https://dt4l9bx31tioh.cloudfront.net//eazymedia//restaurant//695156//restaurant420240703085242.jpg",
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            contentScale = ContentScale.Crop
        )

        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(2f)
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Transparent,
                            0.8f to Color.Transparent,
                            1.0f to Color.White
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, bottom = 32.dp, top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f)
            )

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
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
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
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
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

@Preview
@Composable
private fun PreviewStopDetailSheet() {

    val dummyJourneyStops = listOf(

        JourneyStop(
            id = 1,
            title = "New Delhi Railway Station",
            address = "Ajmeri Gate, New Delhi, Delhi 110006",
            notes = "Journey starting point",
            latitude = 28.6430,
            longitude = 77.2197,
            timestamp = 1739491200000, // 15 Feb 2025 08:00 AM
            stopType = StopType.START,
            distanceFromPrev = 0f,
            durationAtStop = 20
        ),

        JourneyStop(
            id = 2,
            title = "Mathura Temple",
            address = "Mathura, Uttar Pradesh 281001",
            notes = "Krishna Janmabhoomi visit and darshan",
            latitude = 27.4924,
            longitude = 77.6737,
            timestamp = 1739491200000 + 2 * 60 * 60 * 1000, // +2 hrs
            stopType = StopType.VISIT,
            distanceFromPrev = 183f,
            durationAtStop = 60
        ),

        JourneyStop(
            id = 3,
            title = "Vrindavan Lunch Stop",
            address = "Banke Bihari Marg, Vrindavan, UP",
            notes = "Lunch and rest break",
            latitude = 27.5806,
            longitude = 77.7006,
            timestamp = 1739491200000 + 4 * 60 * 60 * 1000, // +4 hrs
            stopType = StopType.FOOD,
            distanceFromPrev = 15f,
            durationAtStop = 45
        ),

        JourneyStop(
            id = 4,
            title = "Agra Fort Visit",
            address = "Rakabganj, Agra, UP 282003",
            notes = "Sightseeing and photography",
            latitude = 27.1795,
            longitude = 78.0211,
            timestamp = 1739491200000 + 7 * 60 * 60 * 1000, // +7 hrs
            stopType = StopType.VISIT,
            distanceFromPrev = 70f,
            durationAtStop = 90
        ),

        JourneyStop(
            id = 5,
            title = "Highway Tea Break",
            address = "Yamuna Expressway, UP",
            notes = "Quick tea and rest stop",
            latitude = 27.5000,
            longitude = 77.9000,
            timestamp = 1739491200000 + 9 * 60 * 60 * 1000, // +9 hrs
            stopType = StopType.REST,
            distanceFromPrev = 60f,
            durationAtStop = 15
        ),

        JourneyStop(
            id = 6,
            title = "Jaipur Hotel Check-in",
            address = "MI Road, Jaipur, Rajasthan 302001",
            notes = "Overnight stay",
            latitude = 26.9124,
            longitude = 75.7873,
            timestamp = 1739491200000 + 14 * 60 * 60 * 1000, // +14 hrs
            stopType = StopType.END,
            distanceFromPrev = 355f,
            durationAtStop = 600
        ),

        JourneyStop(
            id = 7,
            title = "Jaipur City Palace",
            address = "City Palace, Jaipur, Rajasthan",
            notes = "Cultural exploration",
            latitude = 26.9258,
            longitude = 75.8237,
            timestamp = 1739577600000, // next day
            stopType = StopType.VISIT,
            distanceFromPrev = 5f,
            durationAtStop = 120
        ),

        JourneyStop(
            id = 8,
            title = "Journey End Point",
            address = "Hawa Mahal, Jaipur, Rajasthan",
            notes = "Final destination",
            latitude = 26.9239,
            longitude = 75.8267,
            timestamp = 1739588400000,
            stopType = StopType.END,
            distanceFromPrev = 2f,
            durationAtStop = 0
        )
    )

    val journeyData = Journey(
        id = 1,
        name = "Holi Road Trip",
        description = "A colorful road trip across cities celebrating Holi vibes.",
        coverEmoji = "🎨",
        date = "15 Mar 2026",
        totalDistanceKm = 620f,
        stops = dummyJourneyStops
    )

    val journeyStop = JourneyStop(
        id = 1,
        title = "New Delhi Railway Station",
        address = "Ajmeri Gate, New Delhi, Delhi 110006",
        notes = "Journey starting point",
        latitude = 28.6430,
        longitude = 77.2197,
        timestamp = 1739491200000, // 15 Feb 2025 08:00 AM
        stopType = StopType.START,
        distanceFromPrev = 0f,
        durationAtStop = 20
    )

    SheetContent(
        stop = journeyStop,
        stopNumber = 2,
        totalStops = 10,
        journey = journeyData,
        onDismiss = {},
        onPrevStop = { },
        onNextStop = { },
        hasPrev = false,
        hasNext = true
    )


}