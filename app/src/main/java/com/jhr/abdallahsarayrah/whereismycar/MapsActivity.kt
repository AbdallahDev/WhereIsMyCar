package com.jhr.abdallahsarayrah.whereismycar

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)

        textView_carno.text = carInfo.carno.toString()

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                val url = "http://192.168.0.29/WhereIsMyCar/update_location.php?" +
                        "lat=${location?.latitude}&lon=${location?.longitude}&" +
                        "carno=${carInfo.carno}"
                val request = Volley.newRequestQueue(this@MapsActivity)
                val stringRequest = StringRequest(Request.Method.GET, url,
                        com.android.volley.Response.Listener { },
                        com.android.volley.Response.ErrorListener { })
                request.add(stringRequest)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)

        button2.setOnClickListener {
            val url = "http://192.168.0.29/WhereIsMyCar/find_location.php?" +
                    "carno=${editText_carno.text}"
            val request = Volley.newRequestQueue(this@MapsActivity)
            val objectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                    com.android.volley.Response.Listener { response ->
                        val location = LatLng(response.getDouble("lat")
                                , response.getDouble("lon"))
                        mMap!!.addMarker(MarkerOptions().position(location))
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(location))
                    },
                    com.android.volley.Response.ErrorListener { })
            request.add(objectRequest)
        }
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

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap?.isMyLocationEnabled = true
        }

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

}
