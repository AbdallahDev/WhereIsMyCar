package com.jhr.abdallahsarayrah.whereismycar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.directions.route.Route

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps3.*

class MapsActivity3 : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps3)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(32.003994, 35.952502)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        val url = "https://maps.googleapis.com/maps/api/directions/json?origin=32.004426,%2035.952431&destination=32.003988,%2035.954323&key=AIzaSyCmSfnFUed9ZHTdFYUPu57XXA-IQsTpkfo"
        val obj = Volley.newRequestQueue(this)
        val obR = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    val distance = response.getJSONArray("routes").getJSONObject(0)
                            .getJSONArray("legs").getJSONObject(0)
                            .getJSONObject("distance").getString("text").toString()
                    val route: Route? = null

                    textView2.text = distance
                }, Response.ErrorListener { })
        obj.add(obR)
    }
}
