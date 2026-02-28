package com.codinglance.mydinningmap

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.codinglance.mydinningmap.ui.theme.MyDinningMapTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.journeymap.ui.screen.JourneyMapScreen
import androidx.core.content.edit
import com.google.android.gms.location.FusedLocationProviderClient

class MainActivity : ComponentActivity() {

    var fusedLocationClient: FusedLocationProviderClient? = null
    var currentUserLocation by mutableStateOf<LatLng?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fetchCurrentLocation()

        setContent {
            MyDinningMapTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    @SuppressLint("MissingPermission") // Ensure you check permissions before calling this
    fun fetchCurrentLocation() {
        fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
            location?.let {
                currentUserLocation = LatLng(it.latitude, it.longitude)
                PreferenceManager(this).saveLocation(LatLng(it.latitude, it.longitude))
//                saveLocation(LatLng(it.latitude, it.longitude))
            }
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    JourneyMapScreen()

}

