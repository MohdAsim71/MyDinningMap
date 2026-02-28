package com.codinglance.mydinningmap.feature.composables


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.codinglance.mydinningmap.DateUtils
import com.codinglance.mydinningmap.DistanceUtils
import com.codinglance.mydinningmap.feature.Journey
import com.codinglance.mydinningmap.feature.JourneyStop
import com.codinglance.mydinningmap.feature.MapStyle
import com.codinglance.mydinningmap.feature.StopColors
import com.codinglance.mydinningmap.feature.viewmodel.JourneyMapViewModel
import com.codinglance.mydinningmap.touchScaleClickable
import com.journeymap.ui.screen.uniqueStops
import com.journeymap.ui.screen.visitCountMap

// ─────────────────────────────────────────────────────────────────
//  TOP APP BAR
// ─────────────────────────────────────────────────────────────────

@Composable
fun JourneyTopBar(
    viewModel: JourneyMapViewModel,
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
                        "${journey.stopCount} stops • ${journey.date}",
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
            MapActionButton(
                label = when (mapStyle) {
                    MapStyle.STANDARD -> "🗺"
                    MapStyle.TERRAIN -> "🏔"
                    MapStyle.SATELLITE -> "🛰"
                },
                onClick = onMapStyleClick
            )
            // Fit to journey
            MapActionButton(label = "⊡", onClick = onFitClick)
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
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        StatItem(
            emoji = "📍",
            value = "${journey.stopCount}",
            label = "Stops"
        )
        StatDivider()
        StatItem(
            emoji = "📏",
            value = DistanceUtils.formatDistance(journey.totalDistanceKm),
            label = "Distance"
        )
        StatDivider()
        StatItem(
            emoji = "⏱",
            value = DateUtils.formatDuration(journey.totalDurationMins),
            label = "Duration"
        )
        StatDivider()
        StatItem(
            emoji = "📅",
            value = journey.date,
            label = "Date"
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
            .height(36.dp)
            .background(Color(0xFFF2F2F7))
    )
}

@Composable
fun JourneyListPanel(
    viewModel: JourneyMapViewModel,
    journeys: List<Journey>,
    activeJourney: Journey?,
    onJourneySelected: (Journey) -> Unit,
    onDismiss: () -> Unit
) {
    // ── Only one journey can be expanded at a time ─────────────────
    var expandedJourneyId by remember { mutableStateOf<Int?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable(onClick = onDismiss)
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.82f)
                .shadow(24.dp)
                .background(Color.White)
                .padding(top = 56.dp)
        ) {
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
                        viewModel,
                        journey = journey,
                        isActive = journey.id == activeJourney?.id,
                        isExpanded = journey.id == expandedJourneyId,   // ✅ controlled from parent
                        onExpandToggle = {
                            // ✅ if already open → close it, else open this one
                            expandedJourneyId =
                                if (expandedJourneyId == journey.id) null else journey.id
                        },
                        onClick = { onJourneySelected(journey) }
                    )
                }
            }
        }
    }
}

@Composable
private fun JourneyListItem(
    viewModel: JourneyMapViewModel,
    journey: Journey,
    isActive: Boolean,            // ✅ controlled by parent
    isExpanded: Boolean,     // ✅ parent decides open/close logic
    onExpandToggle: () -> Unit,
    onClick: () -> Unit
) {
    val bgColor = if (isActive) Color(0xFFF0F7FF) else Color.White
    val accentColor = Color(0xFF007AFF)

    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "chevron_rotation"
    )


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
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

            // ── Chevron ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isExpanded) accentColor.copy(alpha = 0.1f) else Color(0xFFF2F2F7))
                    .clickable(onClick = onExpandToggle),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = if (isExpanded) accentColor else Color(0xFF8E8E93),
                    modifier = Modifier
                        .size(18.dp)
                        .graphicsLayer { rotationZ = rotationAngle }
                )
            }
        }

        // ── Expandable stop list ──────────────────────────────────
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = tween(300)) + fadeIn(tween(300)),
            exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(tween(300))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFAFC))
                    .padding(bottom = 8.dp)
            ) {

                journey.stops.forEachIndexed { index, stop ->
                    StopListItem(
                        stop = stop,
                        stopNumber = index + 1,
                        isLast = index == journey.stops.lastIndex,

                    )
                }


            }
        }

        HorizontalDivider(color = Color(0xFFF2F2F7))
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun StopListItem(
    stop: JourneyStop,
    stopNumber: Int,
    isLast: Boolean,
) {
    val stopColor = StopColors.forType(stop.stopType)
    val uriHandler = LocalUriHandler.current


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 16.dp, top = 6.dp, bottom = 6.dp)
            .touchScaleClickable(onClick = {
                if (stop.restaurant_code.isNotBlank()) {
                    uriHandler.openUri(stop.restaurant_code)  // ✅ opens in browser
                }
            }),

        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ── Card ──────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                .padding(8.dp)

            ,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            // ── Stop image + visit count badge ────────────────────
            Box(
                modifier = Modifier
                    .size(56.dp)
            ) {
                GlideImage(
                    model = stop.image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF2F2F7)),
                    contentScale = ContentScale.Crop
                )

            }

            // ── Info ──────────────────────────────────────────────
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // ── Title row ─────────────────────────────────────
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stop.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1C1C1E),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (stop.is_prime) {
                        Badge(
                            label = "Prime",
                            bgColor = Color(0xFFFFF3CD),
                            textColor = Color(0xFF856404),
                            icon = "👑"
                        )
                    }
                    if (stop.is_chain) {
                        Badge(
                            label = "Chain",
                            bgColor = Color(0xFFE8F4FD),
                            textColor = Color(0xFF0C63A4),
                            icon = "🔗"
                        )
                    }
                }

                // ── Price row ─────────────────────────────────────
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(text = "Total", fontSize = 10.sp, color = Color(0xFF8E8E93))
                        Text(
                            text = stop.total_amount,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1C1C1E)
                        )
                    }

                    Text("•", fontSize = 10.sp, color = Color(0xFFCCCCCC))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(text = "Save", fontSize = 10.sp, color = Color(0xFF8E8E93))
                        Text(
                            text = stop.discount_amount,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF34C759)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF34C759).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "visit",
                            tint = Color(0xFF34C759),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}
// ── Reusable badge ────────────────────────────────────────────────
@Composable
private fun Badge(
    label: String,
    bgColor: Color,
    textColor: Color,
    icon: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 5.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(icon, fontSize = 6.sp)
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

fun List<Journey>.uniqueStops(): List<JourneyStop> =
    flatMap { it.stops }.distinctBy { it.restaurant_code }

fun List<Journey>.visitCountMap(): Map<String, Int> =
    flatMap { it.stops }
        .groupBy { it.restaurant_code }
        .mapValues { it.value.size }