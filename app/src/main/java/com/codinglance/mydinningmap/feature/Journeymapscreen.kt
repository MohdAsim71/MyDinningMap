package com.journeymap.ui.screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.Glide
import com.codinglance.mydinningmap.DistanceUtils.formatDistance
import com.codinglance.mydinningmap.PreferenceManager
import com.codinglance.mydinningmap.feature.Journey
import com.codinglance.mydinningmap.feature.JourneyStop
import com.codinglance.mydinningmap.feature.MapStyle
import com.codinglance.mydinningmap.feature.composables.JourneyListPanel
import com.codinglance.mydinningmap.feature.composables.JourneyMarker
import com.codinglance.mydinningmap.feature.composables.JourneyStatsBar
import com.codinglance.mydinningmap.feature.composables.JourneyTopBar
import com.codinglance.mydinningmap.feature.composables.StopDetailSheet
import com.codinglance.mydinningmap.feature.composables.uniqueStops
import com.codinglance.mydinningmap.feature.composables.visitCountMap
import com.codinglance.mydinningmap.feature.viewmodel.JourneyMapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun JourneyMapScreen(viewModel: JourneyMapViewModel = viewModel()) {
    val journey = viewModel.activeJourney
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.5937, 78.9629), 5f)
    }

    // ✅ Deduplicated stops & visit count across ALL journeys
    val uniqueStops = remember(viewModel.journeys) {
        viewModel.journeys.uniqueStops()
    }
    val visitCountMap = remember(viewModel.journeys) {
        viewModel.journeys.visitCountMap()
    }

    LaunchedEffect(journey?.id) {
        if (journey != null) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngBounds(buildBounds(journey), 250), durationMs = 1200
            )
        }
    }
    LaunchedEffect(viewModel.cameraTargetStop?.id) {
        val stop = viewModel.cameraTargetStop ?: return@LaunchedEffect
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(LatLng(stop.latitude, stop.longitude), 16f),
            durationMs = 800
        )
        viewModel.clearCameraTarget()
    }
    LaunchedEffect(viewModel.fitBoundsRequest) {
        val bounds = viewModel.fitBoundsRequest ?: return@LaunchedEffect
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngBounds(bounds, 250),
            durationMs = 1000
        )
        viewModel.clearFitBoundsRequest()
    }

    val bitmapMap = remember { mutableStateMapOf<String, ImageBitmap?>() }
    val context = LocalContext.current

    // ✅ Load bitmaps for unique stops only — no duplicate Glide requests
    LaunchedEffect(uniqueStops) {
        uniqueStops.forEach { stop ->
            if (stop.image.isNotBlank() && !bitmapMap.containsKey(stop.image)) {
                launch {
                    bitmapMap[stop.image] = loadBitmapFromUrl(context, stop.image)
                }
            }
        }
    }

    // State to track if we have permission
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // The launcher that actually pops up the system dialog
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    // Trigger the check when the screen starts
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
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
                isMyLocationEnabled = hasLocationPermission,
                mapType = when (viewModel.mapStyle) {
                    MapStyle.STANDARD -> MapType.NORMAL
                    MapStyle.TERRAIN -> MapType.TERRAIN
                    MapStyle.SATELLITE -> MapType.NORMAL
                }
            )
        ) {
            // ✅ Only show stops from active journey, but deduplicated
            val stopsToShow = remember(journey, uniqueStops) {
                if (journey == null) uniqueStops
                else uniqueStops.filter { unique ->
                    journey.stops.any { it.restaurant_code == unique.restaurant_code }
                }
            }

            journey?.stops?.forEachIndexed { index, stop ->
                val bitmap = bitmapMap[stop.image]
                val visitCount = visitCountMap[stop.restaurant_code] ?: 1

                key(stop.restaurant_code, bitmap != null) {
                    MarkerComposable(
                        state = MarkerState(position = LatLng(stop.latitude, stop.longitude)),
                        title = stop.title,
                        onClick = { viewModel.onStopMarkerTapped(stop); true }
                    ) {
                        JourneyMarker(
                            stop = stop,
                            stopNumber = index + 1,
                            isSelected = viewModel.selectedStop?.restaurant_code == stop.restaurant_code,
                            imageBitmap = bitmap,
                            visitCount = visitCount
                        )
                    }
                }
            }
        }

        if (!hasLocationPermission) {
            Text(
                text = "Enable Location in Settings",
                modifier = Modifier
                    .padding(bottom = 120.dp)
                    .shadow(
                        8.dp,
                        RoundedCornerShape(16.dp),
                        ambientColor = Color.Black.copy(0.08f)
                    )
                    .border(1.dp, color = Color.LightGray, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .clickable {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                    .padding(10.dp)
                    .align(Alignment.BottomCenter),
                color = Color.Black
            )
        }

        JourneyTopBar(
            viewModel,
            journey = journey,
            onMenuClick = { viewModel.toggleJourneyList() },
            onFitClick = { viewModel.fitToJourney() },
            onMapStyleClick = { viewModel.cycleMapStyle() },
            mapStyle = viewModel.mapStyle,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )

        val userLatLng = PreferenceManager(LocalContext.current).getLastLocation()

        AnimatedVisibility(
            visible = journey != null && !viewModel.isSheetVisible,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            if (journey != null) {
                val nearestRestaurant = viewModel.getNearestRestaurant(userLatLng, journey.stops)

                val lat = LatLng(
                    nearestRestaurant?.latitude ?: -1.0,
                    nearestRestaurant?.longitude ?: -1.0
                )

                val distance = userLatLng?.let {
                    viewModel.getDistanceInMeters(
                        lat,
                        userLatLng
                    )
                }

                val formatted = formatDistance((distance ?: 0f) / 1000)

                JourneyStatsBar(
                    journey = journey,
                    nearestRestaurant,
                    formatted,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        }

        AnimatedVisibility(
            visible = viewModel.isJourneyListOpen,
            enter = slideInHorizontally { -it } + fadeIn(tween(200)),
            exit = slideOutHorizontally { -it } + fadeOut(tween(200))
        ) {
            JourneyListPanel(
                viewModel,
                journeys = viewModel.journeys,
                activeJourney = viewModel.activeJourney,
                onJourneySelected = { viewModel.selectJourney(it) },
                onDismiss = { viewModel.toggleJourneyList() }
            )
        }

        val selected = viewModel.selectedStop
        val activeJ = viewModel.activeJourney

        val selectedLatLng = LatLng(
            viewModel.selectedStop?.latitude ?: -1.0,
            viewModel.selectedStop?.longitude ?: -1.0
        )

        val distance = userLatLng?.let {
            viewModel.getDistanceInMeters(
                selectedLatLng,
                userLatLng
            )
        }

        if (viewModel.isSheetVisible && selected != null && activeJ != null) {
            StopDetailSheet(
                stop = selected,
                stopNumber = activeJ.stops.indexOf(selected) + 1,
                totalStops = activeJ.stopCount,
                journey = activeJ,
                distance = distance,
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

//@Composable
//private fun JourneyPolyline(journey: Journey) {
//    val points = journey.stops.map { LatLng(it.latitude, it.longitude) }
//
//    // White halo underneath — improves visibility on satellite/terrain tiles
//    // ✅ Color.White.copy() is already androidx.compose.ui.graphics.Color
//    Polyline(
//        points = points,
//        color = Color.White.copy(alpha = 0.55f),
//        width = 10f,
//        zIndex = 0f,
//        geodesic = true
//    )
//
//    // Main blue route line on top
//    // ✅ StopColors.PathLine is already androidx.compose.ui.graphics.Color
//    Polyline(
//        points = points,
//        color = StopColors.PathLine,
//        width = 6f,
//        zIndex = 1f,
//        geodesic = true,
//        jointType = JointType.ROUND,
//        startCap = RoundCap(),
//        endCap = RoundCap()
//    )
//}

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

fun Journey.uniqueStops(): List<JourneyStop> =
    stops.distinctBy { it.restaurant_code }

fun Journey.visitCountMap(): Map<String, Int> =
    stops
        .groupBy { it.restaurant_code }
        .mapValues { it.value.size }