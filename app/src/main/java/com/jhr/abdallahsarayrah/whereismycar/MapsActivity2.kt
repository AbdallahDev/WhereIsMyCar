package com.jhr.abdallahsarayrah.whereismycar

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.directions.route.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps2.*
import java.util.*

class MapsActivity2 : FragmentActivity(), OnMapReadyCallback, RoutingListener {

    private var mMap: GoogleMap? = null
    private var click: Int = 0
    private var markerMapReadey: Marker? = null
    private var start: LatLng? = null
    private var startLat: Double = 0.0
    private var startLng: Double = 0.0
    private var end: LatLng? = null
    private var endLat: Double = 0.0
    private var endLng: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps2)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        polylines = ArrayList()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(32.004012, 35.952590)
        markerMapReadey = mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f))

        val mMap1 = mMap
        if (mMap1 != null) mMap1.isMyLocationEnabled = true

        mMap?.setOnMapClickListener { latLng ->
            if (markerMapReadey != null) markerMapReadey?.remove()
            if (click < 2) {
                if (click == 0) {
                    startLat = latLng.latitude
                    startLng = latLng.longitude
                    start = LatLng(startLat, startLng)
                    mMap?.addMarker(MarkerOptions()
                            .position(start!!)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                } else {
                    endLat = latLng.latitude
                    endLng = latLng.longitude
                    end = LatLng(latLng.latitude, latLng.longitude)
                    mMap?.addMarker(MarkerOptions()
                            .position(end!!)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))

                    getRoute()
                }
                click += 1
            } else {
                click = 0
                mMap?.clear()
            }
        }
    }

    private fun getRoute() {
        val routing = Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(start, end)
                .build()
        routing.execute()
    }

    //those functions from the library RoutingListener
    override fun onRoutingCancelled() {

    }

    override fun onRoutingStart() {
    }

    override fun onRoutingFailure(p0: RouteException?) {
        if (p0 != null) {
            Toast.makeText(this, "Error: " + p0.message, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    private var polylines: List<Polyline>? = null
    @SuppressLint("ResourceType", "SetTextI18n")
    override fun onRoutingSuccess(route: ArrayList<Route>?, p1: Int) {
        if (polylines?.size!! > 0) {
            for (poly in polylines!!) {
                poly.remove()
            }
        }

        polylines = ArrayList()

        //add route(s) to the map.
        for (i in 0 until route?.size!!) {
            val polyOptions = PolylineOptions()
            polyOptions.color(Color.parseColor("#673AB7"))
            polyOptions.width((10 + i * 3).toFloat())
            polyOptions.addAll(route[i].points)
            val polyline = mMap?.addPolyline(polyOptions)
            if (polyline != null) {
                (polylines as ArrayList<Polyline>).add(polyline)
            }

//            if (route[i].distanceValue >= 1000) {
//                textView.text = "Distance: " + (route[i].distanceValue.toFloat() / 1000) + " km"
//            } else {
//                textView.text = "Distance: " + route[i].distanceValue + " m"
//            }
        }

        val url = "https://maps.googleapis.com/maps/api/directions/json?origin=$startLat,$startLng&destination=$endLat,$endLng&key=AIzaSyCmSfnFUed9ZHTdFYUPu57XXA-IQsTpkfo"
        val obj = Volley.newRequestQueue(this)
        val obR = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    val distance = response.getJSONArray("routes").getJSONObject(0)
                            .getJSONArray("legs").getJSONObject(0)
                            .getJSONObject("distance").getString("text").toString()

                    val duration = response.getJSONArray("routes").getJSONObject(0)
                            .getJSONArray("legs").getJSONObject(0)
                            .getJSONObject("duration").getString("text").toString()

                    textView.text = "Distance: $distance, Duration: $duration"
                }, Response.ErrorListener { })
        obj.add(obR)
    }
}
