package se.nmds.drowablemenu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.support.v4.app.ActivityCompat
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import kotlin.concurrent.thread

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    companion object {
        fun start(context: Context){
            context.startActivity(Intent(context, MapsActivity::class.java))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps_activity)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermision()
    }//onCreate




    val accesLocation = 123
    fun checkPermision(){
        if (Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), accesLocation)
                return
            }
        }
        getUserLocation()
    }//checkPermision

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            accesLocation->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getUserLocation()
                }else{
                    Toast.makeText(this, "Location acces is Deny", Toast.LENGTH_LONG).show()
                }
            }
        }
          super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    //  @SuppressLint("MissingPermission")
    fun getUserLocation(){
        Toast.makeText(this, "Location acces is acces", Toast.LENGTH_LONG).show()

        val myLocation= MyLocationListener()
        val locationManager= getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 3f, myLocation)
        val myThread = MyThread()
        myThread.start()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)

    }


    var myLocation:Location?= null
    inner class MyLocationListener: LocationListener {
        constructor(){
            myLocation= Location("Me")
            myLocation!!.latitude = 0.0
            myLocation!!.longitude = 0.0
        }
        override fun onLocationChanged(location: Location?) {
            myLocation = location
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }

    }
    inner class MyThread:Thread{
        constructor():super(){

        }

        override fun run() {
          //  super.run()
            while (true){
                try {

                    //   val malmo = LatLng(55.58, 13.02)

                runOnUiThread{
                    mMap.clear()
                    val malmo = LatLng(myLocation!!.latitude, myLocation!!.longitude)
                    mMap.addMarker(MarkerOptions()
                        .position(malmo)
                        .title("Malmö")
                        .snippet("Malmö Norra gränges")
                        //  .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_menu_camera))
                    )
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(malmo, 10f))

                }
                    Thread.sleep(10000)
                }catch (ex:Exception){}

            }
        }

    }
}
