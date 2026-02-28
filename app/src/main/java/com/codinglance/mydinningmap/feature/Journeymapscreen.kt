package com.journeymap.ui.screen

import android.content.Context
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.Glide
import com.codinglance.mydinningmap.feature.Journey
import com.codinglance.mydinningmap.feature.MapStyle
import com.codinglance.mydinningmap.feature.StopColors
import com.codinglance.mydinningmap.feature.composables.JourneyListPanel
import com.codinglance.mydinningmap.feature.composables.JourneyMarker
import com.codinglance.mydinningmap.feature.composables.JourneyStatsBar
import com.codinglance.mydinningmap.feature.composables.JourneyTopBar
import com.codinglance.mydinningmap.feature.composables.StopDetailSheet
import com.codinglance.mydinningmap.feature.viewmodel.JourneyMapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun JourneyMapScreen(viewModel: JourneyMapViewModel = viewModel()) {
    val journey = viewModel.activeJourney
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.5937, 78.9629), 5f)
    }

    LaunchedEffect(journey?.id) {
        if (journey != null) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngBounds(buildBounds(journey), 120), durationMs = 1200
            )
        }
    }
    LaunchedEffect(viewModel.cameraTargetStop?.id) {
        val stop = viewModel.cameraTargetStop ?: return@LaunchedEffect
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(LatLng(stop.latitude, stop.longitude), 16f), durationMs = 800
        )
        viewModel.clearCameraTarget()
    }
    LaunchedEffect(viewModel.fitBoundsRequest) {
        val bounds = viewModel.fitBoundsRequest ?: return@LaunchedEffect
        cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 120), durationMs = 1000)
        viewModel.clearFitBoundsRequest()
    }

    // ✅ Load all bitmaps here — LaunchedEffect works fine at screen level
    val bitmapMap = remember { mutableStateMapOf<String, ImageBitmap?>() }
    val context = LocalContext.current

    LaunchedEffect(journey?.stops) {
        journey?.stops?.forEach { stop ->
            if (stop.image.isNotBlank() && !bitmapMap.containsKey(stop.image)) {
                launch {
                    Log.d("MapScreen", "Loading: ${stop.image}")
                    bitmapMap[stop.image] = loadBitmapFromUrl(context, stop.image)
                    Log.d("MapScreen", "Loaded: ${stop.image} → ${bitmapMap[stop.image]}")
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = false,
                compassEnabled = true
            ),
            properties = MapProperties(
                mapType = when (viewModel.mapStyle) {
                    MapStyle.STANDARD  -> MapType.NORMAL
                    MapStyle.TERRAIN   -> MapType.TERRAIN
                    MapStyle.SATELLITE -> MapType.HYBRID
                }
            )
        ) {
            journey?.stops?.forEachIndexed { index, stop ->
                val bitmap = bitmapMap[stop.image]  // ✅ observe state directly

                // ✅ key() forces MarkerComposable to recompose when bitmap changes
                key(stop.image, bitmap != null) {
                    MarkerComposable(
                        state = MarkerState(position = LatLng(stop.latitude, stop.longitude)),
                        title = stop.title,
                        onClick = { viewModel.onStopMarkerTapped(stop); true }
                    ) {
                        JourneyMarker(
                            stop = stop,
                            stopNumber = index + 1,
                            isSelected = false,
                            imageBitmap = bitmap
                        )
                    }
                }
            }
        }

        JourneyTopBar(
            journey = journey,
            onMenuClick = { viewModel.toggleJourneyList() },
            onFitClick = { viewModel.fitToJourney() },
            onMapStyleClick = { viewModel.cycleMapStyle() },
            mapStyle = viewModel.mapStyle,
            modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding()
        )

        AnimatedVisibility(
            visible = journey != null && !viewModel.isSheetVisible,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            if (journey != null) {
                JourneyStatsBar(journey = journey, modifier = Modifier.navigationBarsPadding())
            }
        }

        AnimatedVisibility(
            visible = viewModel.isJourneyListOpen,
            enter = slideInHorizontally { -it } + fadeIn(tween(200)),
            exit = slideOutHorizontally { -it } + fadeOut(tween(200))
        ) {
            JourneyListPanel(
                journeys = viewModel.journeys,
                activeJourney = viewModel.activeJourney,
                onJourneySelected = { viewModel.selectJourney(it) },
                onDismiss = { viewModel.toggleJourneyList() }
            )
        }

        val selected = viewModel.selectedStop
        val activeJ = viewModel.activeJourney
        if (viewModel.isSheetVisible && selected != null && activeJ != null) {
            StopDetailSheet(
                stop = selected,
                stopNumber = activeJ.stops.indexOf(selected) + 1,
                totalStops = activeJ.stopCount,
                journey = activeJ,
                onDismiss = { viewModel.dismissSheet() },
                onPrevStop = { viewModel.goToPrevStop() },
                onNextStop = { viewModel.goToNextStop() },
                hasPrev = viewModel.hasPrevStop(),
                hasNext = viewModel.hasNextStop()
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────
//  JOURNEY POLYLINE  –  FIXED
//
//  ROOT CAUSE of the original error:
//  maps-compose `Polyline(color = ...)` expects androidx.compose.ui.graphics.Color.
//  The old code did an unnecessary Android int round-trip:
//
//    android.graphics.Color.WHITE.toArgb()      → Int
//    .let { androidx.compose.ui.graphics.Color(it) } → Compose Color ✓
//    .let { android.graphics.Color.argb(...) }  → back to Int       ✗  TYPE MISMATCH
//    .let { Color.White.copy(alpha = 0.5f) }    → Compose Color ✓ (but too late)
//
//  The middle `.let` converted back to an Int (android.graphics.Color.argb returns Int),
//  so the `color` parameter received an Int instead of Compose Color → compile error.
//
//  FIX: Pass Compose Color directly. No android.graphics.Color needed at all.
// ─────────────────────────────────────────────────────────────────

@Composable
private fun JourneyPolyline(journey: Journey) {
    val points = journey.stops.map { LatLng(it.latitude, it.longitude) }

    // White halo underneath — improves visibility on satellite/terrain tiles
    // ✅ Color.White.copy() is already androidx.compose.ui.graphics.Color
    Polyline(
        points = points,
        color = Color.White.copy(alpha = 0.55f),
        width = 10f,
        zIndex = 0f,
        geodesic = true
    )

    // Main blue route line on top
    // ✅ StopColors.PathLine is already androidx.compose.ui.graphics.Color
    Polyline(
        points = points,
        color = StopColors.PathLine,
        width = 6f,
        zIndex = 1f,
        geodesic = true,
        jointType = JointType.ROUND,
        startCap = RoundCap(),
        endCap = RoundCap()
    )
}

private fun buildBounds(journey: Journey): LatLngBounds {
    val builder = LatLngBounds.builder()
    journey.stops.forEach { builder.include(LatLng(it.latitude, it.longitude)) }
    return builder.build()
}

suspend fun loadBitmapFromUrl(context: Context, url: String): ImageBitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val bitmap = Glide.with(context.applicationContext)
                .asBitmap()
                .load(url)
                .submit()
                .get()
            bitmap.asImageBitmap()
        } catch (e: Exception) {
            Log.e("BitmapLoader", "❌ Failed: ${e.message}")
            null
        }
    }
}