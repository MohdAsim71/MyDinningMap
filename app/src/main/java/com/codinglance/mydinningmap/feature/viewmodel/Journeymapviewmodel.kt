package com.codinglance.mydinningmap.feature.viewmodel


import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codinglance.mydinningmap.feature.Journey
import com.codinglance.mydinningmap.feature.JourneyRepository
import com.codinglance.mydinningmap.feature.JourneyStop
import com.codinglance.mydinningmap.feature.MapStyle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class JourneyMapViewModel : ViewModel() {

    // ── All journeys available ─────────────────────────────────────
    val journeys: List<Journey> = JourneyRepository.sampleJourneys

    // ── Active journey shown on map ────────────────────────────────
    var activeJourney by mutableStateOf<Journey?>(journeys.firstOrNull())
        private set

    // ── Selected stop (tap on marker) ─────────────────────────────
    var selectedStop by mutableStateOf<JourneyStop?>(null)
        private set

    // ── Bottom sheet visibility ────────────────────────────────────
    var isSheetVisible by mutableStateOf(false)
        private set

    // ── Journey list drawer visible ────────────────────────────────
    var isJourneyListOpen by mutableStateOf(false)
        private set

    // ── Camera animation trigger ──────────────────────────────────
    var cameraTargetStop by mutableStateOf<JourneyStop?>(null)
        private set

    var fitBoundsRequest by mutableStateOf<LatLngBounds?>(null)
        private set

    // ── Stats panel visibility ────────────────────────────────────
    var isStatsExpanded by mutableStateOf(false)
        private set

    // ── Map type ──────────────────────────────────────────────────
    var mapStyle by mutableStateOf(MapStyle.STANDARD)
        private set

    // ─────────────────────────────────────────────────────────────
    //  ACTIONS
    // ─────────────────────────────────────────────────────────────

    /** User taps a stop marker on the map */
    fun onStopMarkerTapped(stop: JourneyStop) {
        selectedStop = stop
        isSheetVisible = true
        cameraTargetStop = stop
    }

    /** Close the detail bottom sheet */
    fun dismissSheet() {
        isSheetVisible = false
        // Slight delay before clearing selection so exit animation plays
        viewModelScope.launch {
            delay(300)
            selectedStop = null
        }
    }

    /** User selects a journey from the list */
    fun selectJourney(journey: Journey) {
        activeJourney = journey
        selectedStop = null
        isSheetVisible = false
        isJourneyListOpen = false

        // Fit camera to show all stops
        viewModelScope.launch {
            delay(300) // let recomposition settle
            fitBoundsRequest = computeJourneyBounds(journey)
        }
    }

    /** Fit map camera to show all stops of the active journey */
    fun fitToJourney() {
        val journey = activeJourney ?: return
        fitBoundsRequest = computeJourneyBounds(journey)
    }

    fun clearFitBoundsRequest() {
        fitBoundsRequest = null
    }

    fun clearCameraTarget() {
        cameraTargetStop = null
    }

    fun toggleJourneyList() {
        isJourneyListOpen = !isJourneyListOpen
    }

    fun toggleStats() {
        isStatsExpanded = !isStatsExpanded
    }

    fun cycleMapStyle() {
        mapStyle = when (mapStyle) {
            MapStyle.STANDARD  -> MapStyle.TERRAIN
            MapStyle.TERRAIN   -> MapStyle.SATELLITE
            MapStyle.SATELLITE -> MapStyle.STANDARD
        }
    }

    /** Navigate to next stop in journey */
    fun goToNextStop() {
        val journey = activeJourney ?: return
        val current = selectedStop ?: return
        val idx = journey.stops.indexOf(current)
        val next = journey.stops.getOrNull(idx + 1) ?: return
        onStopMarkerTapped(next)
    }

    /** Navigate to previous stop in journey */
    fun goToPrevStop() {
        val journey = activeJourney ?: return
        val current = selectedStop ?: return
        val idx = journey.stops.indexOf(current)
        val prev = journey.stops.getOrNull(idx - 1) ?: return
        onStopMarkerTapped(prev)
    }

    fun hasNextStop(): Boolean {
        val journey = activeJourney ?: return false
        val current = selectedStop ?: return false
        return journey.stops.indexOf(current) < journey.stops.size - 1
    }

    fun hasPrevStop(): Boolean {
        val journey = activeJourney ?: return false
        val current = selectedStop ?: return false
        return journey.stops.indexOf(current) > 0
    }

    fun currentStopIndex(): Int {
        val journey = activeJourney ?: return 0
        val current = selectedStop ?: return 0
        return journey.stops.indexOf(current) + 1
    }

    // ─────────────────────────────────────────────────────────────
    //  HELPERS
    // ─────────────────────────────────────────────────────────────

    private fun computeJourneyBounds(journey: Journey): LatLngBounds {
        val builder = LatLngBounds.builder()
        journey.stops.forEach { stop ->
            builder.include(LatLng(stop.latitude, stop.longitude))
        }
        return builder.build()
    }

    // Extension to get unique stops across all journeys
    fun List<Journey>.uniqueStops(): List<JourneyStop> {
        return this
            .flatMap { it.stops }
            .distinctBy { it.restaurant_code }  // ✅ unique by restaurant_code
    }

    fun List<Journey>.visitCountMap(): Map<String, Int> {
        return this
            .flatMap { it.stops }
            .groupBy { it.restaurant_code }
            .mapValues { it.value.size }        // ✅ how many times visited
    }
}