package com.sample.myweather.ui.map

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sample.myweather.R
import com.sample.myweather.ui.home.HomeActivity.Companion.mLocationPermissionGranted
import com.sample.myweather.utils.log
import kotlinx.android.synthetic.main.fragment_maps.*
import java.util.*


class MapsFragment : Fragment() {

    companion object {
        fun newInstance(): MapsFragment {
            return MapsFragment()
        }
    }

    private lateinit var mMap: GoogleMap
    private var geocoder: Geocoder? = null
    private var mAddress: Address? = null
    private var pickedCity: String = ""

    val selectedCity = MutableLiveData<String>()
    val dismissFragment = MutableLiveData<Boolean>()

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var mLastKnownLocation: Location? = null

    private var mDefaultLocation = LatLng(18.1124, 79.0193)
    private val DEFAULT_ZOOM = 8


    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        mAddress = getAddress()

        var title = "${mDefaultLocation.latitude} : ${mDefaultLocation.longitude}"
        if(!mAddress?.locality.isNullOrBlank() || !mAddress?.adminArea.isNullOrBlank()){
            title = if(mAddress!!.locality!=null) mAddress!!.locality else mAddress!!.adminArea
        }
        pickedCity = "$title@${mDefaultLocation.latitude},${mDefaultLocation.latitude}"

        mMap.addMarker(MarkerOptions().position(mDefaultLocation).title(title))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mDefaultLocation))

        btn_done.text = "Select: $title"

        getDeviceLocation()

        mMap.setOnMapClickListener { latLng ->
            mDefaultLocation = LatLng(latLng.latitude, latLng.longitude)

            mAddress = getAddress()

            var title = "${mDefaultLocation.latitude} : ${mDefaultLocation.longitude}"
            if(!mAddress?.locality.isNullOrBlank() || !mAddress?.adminArea.isNullOrBlank()){
                title = if(mAddress!!.locality!=null) mAddress!!.locality else mAddress!!.adminArea
            }
            pickedCity = "$title@${mDefaultLocation.latitude},${mDefaultLocation.latitude}"

            mMap.clear();
            mMap.addMarker(MarkerOptions().position(mDefaultLocation).title(title))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM.toFloat()))

            btn_done.text = "Select: $title"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_maps, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let { cntx ->

            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(callback)

            geocoder = Geocoder(cntx, Locale.getDefault())


            btn_done.setOnClickListener {
                selectedCity.postValue(pickedCity)
            }
            btn_close.setOnClickListener {
                dismissFragment.postValue(true)
            }
        }
    }

    private fun getDeviceLocation() {
        try {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            if (mLocationPermissionGranted) {
                val locationResult = mFusedLocationProviderClient!!.lastLocation
                locationResult.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.result
                        mLastKnownLocation?.let {
                            mDefaultLocation = LatLng(it.latitude, it.longitude)

                            mAddress = getAddress()

                            var title = "${mDefaultLocation.latitude} : ${mDefaultLocation.longitude}"
                            if(!mAddress?.locality.isNullOrBlank() || !mAddress?.adminArea.isNullOrBlank()){
                                title = if(mAddress!!.locality!=null) mAddress!!.locality else mAddress!!.adminArea
                            }
                            pickedCity = "$title@${mDefaultLocation.latitude},${mDefaultLocation.latitude}"

                            mMap.clear();
                            mMap.addMarker(MarkerOptions().position(mDefaultLocation).title(title))
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM.toFloat()))

                            btn_done.text = "Select: $title"
                        }
                    } else {
                        Log.d("chk", "Current location is null. Using defaults.")
                        Log.e("chk", "Exception: %s", task.exception)
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message!!)
        }
    }

    private fun getAddress() : Address? {
        return try {
            val addresses = geocoder?.getFromLocation(mDefaultLocation.latitude, mDefaultLocation.latitude, 1)
            if (addresses?.size!! > 0) {
                log(addresses[0].toString())
                addresses[0]
            } else {
                null
            }
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }
}