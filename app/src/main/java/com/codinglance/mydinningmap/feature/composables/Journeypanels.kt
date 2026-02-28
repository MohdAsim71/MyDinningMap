package com.codinglance.mydinningmap.feature.composables


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codinglance.mydinningmap.DistanceUtils
import com.codinglance.mydinningmap.feature.Journey
import com.codinglance.mydinningmap.feature.JourneyStop
import com.codinglance.mydinningmap.feature.MapStyle
import com.codinglance.mydinningmap.feature.StopType

// ─────────────────────────────────────────────────────────────────
//  TOP APP BAR
// ─────────────────────────────────────────────────────────────────

@Composable
fun JourneyTopBar(
    journey: Journey?,
    onMenuClick: () -> Unit,
    onFitClick: () -> Unit,
    onMapStyleClick: () -> Unit,
    mapStyle: MapStyle,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // ── Left: journey info card ────────────────────────────────
        Row(
            modifier = Modifier
                .shadow(8.dp, RoundedCornerShape(16.dp), ambientColor = Color.Black.copy(0.08f))
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable(onClick = onMenuClick)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(journey?.coverEmoji ?: "🗺️", fontSize = 20.sp)
            Column {
                Text(
                    journey?.name ?: "Select Journey",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1C1E)
                )
                if (journey != null) {
                    Text(
                        "${journey.stopCount} restaurants",
                        fontSize = 11.sp,
                        color = Color(0xFF8E8E93)
                    )
                }
            }
            // Chevron
            Text("›", fontSize = 18.sp, color = Color(0xFFC7C7CC))
        }

        // ── Right: action buttons ──────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Map style toggle
//            MapActionButton(
//                label = when (mapStyle) {
//                    MapStyle.STANDARD -> "🗺"
//                    MapStyle.TERRAIN -> "🏔"
//                    MapStyle.SATELLITE -> "🛰"
//                },
//                onClick = onMapStyleClick
//            )
            // Fit to journey
            MapActionButton(label = "⛶", onClick = onFitClick)
        }
    }
}

@Composable
private fun MapActionButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .shadow(6.dp, CircleShape, ambientColor = Color.Black.copy(0.08f))
            .clip(CircleShape)
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(label, fontSize = 16.sp)
    }
}

// ─────────────────────────────────────────────────────────────────
//  JOURNEY STATS BAR (bottom, above sheet)
// ─────────────────────────────────────────────────────────────────

@Composable
fun JourneyStatsBar(
    journey: Journey,
    journeyStop: JourneyStop?,
    distance: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            emoji = "\uD83C\uDF7D\uFE0F",
            value = "${journey.stopCount}",
            label = "Restaurants"
        )

        StatDivider()

        StatItem(
            emoji = "\uD83D\uDCCD",
            value = (journeyStop?.title + " ● $distance"),
            label = "Nearest Restaurant"
        )
    }
}

@Composable
private fun StatItem(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 18.sp)
        Spacer(Modifier.height(2.dp))
        Text(
            value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1C1C1E)
        )
        Text(
            label,
            fontSize = 10.sp,
            color = Color(0xFF8E8E93),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(50.dp)
            .background(Color(0xFFF2F2F7))
    )
}

// ─────────────────────────────────────────────────────────────────
//  JOURNEY LIST DRAWER (animated slide-in panel)
// ─────────────────────────────────────────────────────────────────

@Composable
fun JourneyListPanel(
    journeys: List<Journey>,
    activeJourney: Journey?,
    onJourneySelected: (Journey) -> Unit,
    onDismiss: () -> Unit
) {
    // Scrim
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable(onClick = onDismiss)
    )

    // Panel
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.82f)
                .shadow(24.dp)
                .background(Color.White)
                .padding(top = 56.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "My Journeys",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1C1E)
                    )
                    Text(
                        "${journeys.size} trips recorded",
                        fontSize = 13.sp,
                        color = Color(0xFF8E8E93)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF2F2F7))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✕", fontSize = 13.sp, color = Color(0xFF8E8E93))
                }
            }

            HorizontalDivider(color = Color(0xFFF2F2F7))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(journeys, key = { it.id }) { journey ->
                    JourneyListItem(
                        journey = journey,
                        isActive = journey.id == activeJourney?.id,
                        onClick = { onJourneySelected(journey) }
                    )
                }
            }
        }
    }
}

@Composable
private fun JourneyListItem(
    journey: Journey,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isActive) Color(0xFFF0F7FF) else Color.White
    val accentColor = Color(0xFF007AFF)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Emoji icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (isActive) accentColor.copy(alpha = 0.1f) else Color(0xFFF2F2F7)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(journey.coverEmoji, fontSize = 22.sp)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                journey.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1C1C1E)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "${journey.stopCount} stops  •  ${DistanceUtils.formatDistance(journey.totalDistanceKm)}  •  ${journey.date}",
                fontSize = 12.sp,
                color = Color(0xFF8E8E93)
            )
        }

        if (isActive) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
        }
    }
}

@Preview
@Composable
private fun PreviewJourneyStatsBar() {
    val journey = Journey(
        id = 1,
        name = "Gurgaon City",
        description = "A full day exploring the best of Mumbai",
        date = "Dec 15, 2024",
        coverEmoji = "🌆",
        totalDistanceKm = 34.6f,
        stops = listOf(
            JourneyStop(
                id = 1,
                title = "Haldiram",
                address = "Sahara Mall Shopping Complex M.G.Raod,Gurugram",
                notes = "Started early at 7am. Light traffic.",
                latitude = 28.4794338,
                longitude = 77.0864217,
                stopType = StopType.START,
                distanceFromPrev = 0f,
                durationAtStop = 5,
                date = "",
                paid_amount = "",
                total_amount = "",
                discount_amount = "",
                image = "",
                restaurant_code = "",
                is_chain = false,
                is_prime = true
            ),
            JourneyStop(
                id = 2,
                title = "YouMee",
                address = "Unit No GR-02, Ground Floor, Worldmark Gurgaon, Village Maidawas, Sector 65, Gurugram, Haryana – 122001",
                notes = "Iconic colonial architecture. Very crowded in the morning but worth it. Watched boats leave for Elephanta.",
                latitude = 28.3980361,
                longitude = 77.0726711,
                stopType = StopType.PHOTO,
                distanceFromPrev = 12.3f,
                durationAtStop = 45,
                date = "",
                paid_amount = "",
                total_amount = "",
                discount_amount = "",
                image = "",
                restaurant_code = "",
                is_chain = false,
                is_prime = true
            )
        )
    )

    val ne = JourneyStop(
        id = 2,
        title = "YouMee",
        address = "Unit No GR-02, Ground Floor, Worldmark Gurgaon, Village Maidawas, Sector 65, Gurugram, Haryana – 122001",
        notes = "Iconic colonial architecture. Very crowded in the morning but worth it. Watched boats leave for Elephanta.",
        latitude = 28.3980361,
        longitude = 77.0726711,
        stopType = StopType.PHOTO,
        distanceFromPrev = 12.3f,
        durationAtStop = 45,
        date = "",
        paid_amount = "",
        total_amount = "",
        discount_amount = "",
        image = "",
        restaurant_code = "",
        is_chain = false,
        is_prime = true
    )

    JourneyStatsBar(journey, ne, "12 kms")

//    JourneyTopBar(
//        journey, {  },
//        onFitClick = {},
//        onMapStyleClick = {},
//        mapStyle = MapStyle.STANDARD,
//        modifier = Modifier
//    )
}