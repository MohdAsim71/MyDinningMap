package com.codinglance.mydinningmap.feature


import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────
//  JOURNEY STOP
//  Represents a single location the user visited
// ─────────────────────────────────────────────────────────────────

data class JourneyStop(
    val id: Int,
    val title: String,
    val address: String,
    val notes: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,          // epoch ms
    val stopType: StopType = StopType.VISIT,
    val distanceFromPrev: Float = 0f,  // km
    val durationAtStop: Int = 0,  // minutes
)

enum class StopType(val label: String, val emoji: String) {
    START("Journey Start", "🏠"),
    VISIT("Visit", "📍"),
    FOOD("Food Stop", "🍽️"),
    PHOTO("Photo Spot", "📸"),
    REST("Rest Stop", "☕"),
    END("Journey End", "🏁")
}

// ─────────────────────────────────────────────────────────────────
//  JOURNEY
//  A complete journey with all stops
// ─────────────────────────────────────────────────────────────────

data class Journey(
    val id: Int,
    val name: String,
    val description: String,
    val stops: List<JourneyStop>,
    val totalDistanceKm: Float,
    val date: String,           // formatted display date
    val coverEmoji: String = "🗺️"
) {
    val totalDurationMins: Int get() = stops.sumOf { it.durationAtStop }
    val stopCount: Int get() = stops.size
}

// ─────────────────────────────────────────────────────────────────
//  MAP UI STATE
// ─────────────────────────────────────────────────────────────────

data class MapUiState(
    val selectedJourney: Journey? = null,
    val selectedStop: JourneyStop? = null,
    val isBottomSheetVisible: Boolean = false,
    val isJourneyListVisible: Boolean = true,
    val mapStyle: MapStyle = MapStyle.STANDARD,
    val isAnimatingCamera: Boolean = false
)

enum class MapStyle { STANDARD, SATELLITE, TERRAIN }

// ─────────────────────────────────────────────────────────────────
//  STOP TYPE COLORS
// ─────────────────────────────────────────────────────────────────

object StopColors {
    val Start       = Color(0xFF34C759)
    val End         = Color(0xFFFF3B30)
    val Visit       = Color(0xFF007AFF)
    val Food        = Color(0xFFFF9500)
    val Photo       = Color(0xFFAF52DE)
    val Rest        = Color(0xFF5AC8FA)
    val PathLine    = Color(0xFF007AFF)
    val PathDashed  = Color(0xFF007AFF).copy(alpha = 0.4f)

    fun forType(type: StopType) = when (type) {
        StopType.START -> Start
        StopType.END   -> End
        StopType.VISIT -> Visit
        StopType.FOOD  -> Food
        StopType.PHOTO -> Photo
        StopType.REST  -> Rest
    }
}