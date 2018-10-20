package se.nmds.drowablemenu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MapsActivity : AppCompatActivity(), LocationListener {

    private val accesLocation = 123

    private lateinit var map: GoogleMap
    private lateinit var marker: Marker
    private lateinit var locationManager: LocationManager
    private lateinit var provider: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps_activity)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync { googleMap ->
            map = googleMap
            checkPermission()
        }
    }

    private fun handleNewLocation(location: Location) {
        val latLng = LatLng(
            location.latitude, location.longitude
        )

        if (!::marker.isInitialized) {
            marker = map.addMarker(MarkerOptions().position(latLng))
        }

        marker.position = latLng
        marker.title = "Malmö"
        marker.snippet = "Malmö Norra gränges"
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        Toast.makeText(this, "Location acces is acces", Toast.LENGTH_LONG).show()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        provider = locationManager.getBestProvider(Criteria(), false)!!

        // Fetch last known position.
        val lastKnownLocation: Location? = locationManager.getLastKnownLocation(provider)
        lastKnownLocation?.let {
            handleNewLocation(it)
        }

        // Register for location updates.
        locationManager.requestLocationUpdates(provider, 3, 3f, this)
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        if (::locationManager.isInitialized) {
            locationManager.requestLocationUpdates(provider, 400, 1f, this)
        }
    }

    override fun onPause() {
        super.onPause()
        if (::locationManager.isInitialized) {
            locationManager.removeUpdates(this)
        }
    }

    /* Permission */

    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), accesLocation)
                return
            }
        }
        getUserLocation()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            accesLocation -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation()
                } else {
                    Toast.makeText(this, "Location acces is Deny", Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /* LocationListener */

    override fun onLocationChanged(location: Location?) {
        location?.let {
            handleNewLocation(it)
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) { /* Not used */
    }

    override fun onProviderEnabled(provider: String?) { /* Not used */
    }

    override fun onProviderDisabled(provider: String?) { /* Not used */
    }

    /* Start Method */

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, MapsActivity::class.java))
        }
    }
}
