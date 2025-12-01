package com.example.test1000

import kotlinx.coroutines.*
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.test1000.data.AppDatabase
import com.example.test1000.data.SocietyLocation
import com.example.test1000.databinding.ActivityMapsBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding

    private lateinit var mMap: GoogleMap
    private var address: String? = null
    private lateinit var database: DatabaseReference
    private var savedLocations: List<SocietyLocation> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        address = intent.getStringExtra("address")

        database = FirebaseDatabase.getInstance().reference
        findViewById<LinearLayout>(R.id.home).setOnClickListener { launchhome() }
        findViewById<LinearLayout>(R.id.profil).setOnClickListener { launchprofile() }
        findViewById<LinearLayout>(R.id.setting).setOnClickListener { launchsettings() }
        findViewById<LinearLayout>(R.id.Evenements).setOnClickListener { launchevent() }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (!checkLocationPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Companion.LOCATION_PERMISSION_REQUEST_CODE
            )
        }

    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Companion.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lifecycleScope.launch {
                    loadSavedLocations()
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadMapData()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (address != null) {
            showAddressOnMap(address!!)
        } else {
            displayLocationsOnMap(savedLocations)
        }
    }

    private fun loadMapData() {
        lifecycleScope.launch {
            fetchAndDisplayMapData()
        }
    }

    private suspend fun fetchAndDisplayMapData() {
        withContext(Dispatchers.IO) {
            val locations = fetchSocietyLocations()
            withContext(Dispatchers.Main) {
                displayLocationsOnMap(locations)
            }
        }
    }

    private suspend fun fetchSocietyLocations(): List<SocietyLocation> {
        val societyLocations = mutableListOf<SocietyLocation>()
        val societiesRef = database.child("societies")
        val snapshot = suspendCoroutine<DataSnapshot> { continuation ->
            societiesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    continuation.resume(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }
        for (categorySnapshot in snapshot.children) {
            for (societySnapshot in categorySnapshot.children) {
                val name = societySnapshot.child("name").getValue(String::class.java) ?: ""
                val address = societySnapshot.child("contact").child("address").getValue(String::class.java)
                if (!address.isNullOrEmpty()) {
                    getLatLngFromAddress(this@MapsActivity, address)?.let { latLng ->
                        societyLocations.add(SocietyLocation(name = name, latitude = latLng.latitude, longitude = latLng.longitude))
                    }
                }
            }
        }
        return societyLocations
    }

    private fun displayLocationsOnMap(locations: List<SocietyLocation>) {
        for (location in locations) {
            val latLng = LatLng(location.latitude ?: 0.0, location.longitude ?: 0.0)
            mMap.addMarker(MarkerOptions().position(latLng).title(location.name ?: ""))
        }
    }

    private fun getLatLngFromAddress(context: MapsActivity, address: String): LatLng? {
        val geocoder = Geocoder(context)
        try {
            val addresses: MutableList<Address>? = geocoder.getFromLocationName(address, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val location = addresses[0]
                    return LatLng(location.latitude, location.longitude)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun loadSavedLocations() {
        lifecycleScope.launch {
            savedLocations = retrieveSavedLocations()
        }
    }

    private suspend fun retrieveSavedLocations(): List<SocietyLocation> {
        return withContext(Dispatchers.IO) {
            val locations: List<SocietyLocation?>? = AppDatabase.getDatabase(applicationContext).societyLocationDao().getAllLocations()
            locations?.filterNotNull() ?: emptyList()
        }
    }

    private fun launchsettings() {
        startActivity(Intent(this, ParametreActivity::class.java))
    }

    private fun launchprofile() {
        startActivity(Intent(this, ProfilActivity::class.java))
        finish()
    }

    private fun launchevent() {
        startActivity(Intent(this, EventActivity::class.java))
    }
    private fun showAddressOnMap(address: String) {
        val latLng = getLatLngFromAddress(this, address)
        if (latLng != null) {
            mMap.addMarker(MarkerOptions().position(latLng).title(address))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }

    private fun launchhome() {
        startActivity(Intent(this, HomeActivity::class.java))
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
