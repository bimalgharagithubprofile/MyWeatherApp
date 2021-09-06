package com.sample.myweather.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sample.myweather.R
import com.sample.myweather.ui.cities.CitiesFragment
import com.sample.myweather.ui.help.HelpActivity
import com.sample.myweather.ui.map.MapsFragment
import com.sample.myweather.ui.weather.WeatherFragment
import com.sample.myweather.utils.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_scrolling.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class HomeActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()

    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel

    private lateinit var mActionBar: ActionBar

    companion object {
        val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        var mLocationPermissionGranted = false

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)

        setContentView(R.layout.activity_home)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.let { mActionBar = it }

        loadCities()

        initClickListener()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }


    private fun loadCities(bookmark: Boolean = false) {
        log("Is Bookmark => $bookmark")

        val fragment = CitiesFragment.newInstance(bookmark)

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_cities, fragment)
        ft.commit()

        fragment.selectedCity.observe(this, Observer {
            log("Load weather for city=$it")

            loadWeatherView(it)
        })
        fragment.progressStatus.observe(this, Observer {
            updateProgressStatus(it)
        })
    }

    private fun initClickListener() {
        sec_home.setOnClickListener {
            log("Home Clicked")
            loadCities()
        }
        sec_book_mark.setOnClickListener {
            log("Bookmark Clicked")
            loadCities(true)
        }
        sec_help.setOnClickListener {
            log("Help Clicked")
            startActivity(Intent(this@HomeActivity, HelpActivity::class.java))
        }


        fabAddCity.setOnClickListener { view ->
            log("opening map")
            if (mLocationPermissionGranted) {
                loadMapView()
            } else {
                getLocationPermission()
            }
        }
    }

    private fun loadMapView() {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            log("Location Disabled")
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        } else {
            log("Location Enabled")

            app_bar.setExpanded(true)
            if (this::mActionBar.isInitialized) {
                mActionBar.hide()
            }
            layout_fragment.visibility = View.VISIBLE
            fabAddCity.visibility = View.GONE

            val fragment = MapsFragment.newInstance()

            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            ft.replace(R.id.layout_fragment, fragment)
            ft.commit()

            fragment.selectedCity.observe(this, Observer {
                try {
                    log("saving city=$it")

                    vProgressBar.show()

                    if (this::mActionBar.isInitialized) {
                        mActionBar.show()
                    }
                    layout_fragment.visibility = View.GONE
                    fabAddCity.visibility = View.VISIBLE

                    if (it.isNotEmpty()) {
                        val response = viewModel.addCity(it)
                        if (response)
                            toast("City Added")
                        else
                            toast("Failed to add !")
                    } else {
                        toast("Invalid city name !")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    vProgressBar.hide()
                    loadCities(false)
                }
            })
            fragment.dismissFragment.observe(this, Observer {
                log("dismissing MapView")
                if (this::mActionBar.isInitialized) {
                    mActionBar.show()
                }
                layout_fragment.visibility = View.GONE
                fabAddCity.visibility = View.VISIBLE

                supportFragmentManager.beginTransaction().remove(fragment).commit()
            })
        }
    }

    private fun loadWeatherView(selectedCity: String) {
        app_bar.setExpanded(true)
        if(this::mActionBar.isInitialized) { mActionBar.hide() }
        layout_fragment.visibility = View.VISIBLE
        fabAddCity.visibility = View.GONE

        val fragment = WeatherFragment.newInstance(selectedCity)

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.layout_fragment, fragment)
        ft.commit()


        fragment.dismissFragment.observe(this, Observer {
            log("dismissing WeatherView")
            if (this::mActionBar.isInitialized) { mActionBar.show() }
            layout_fragment.visibility = View.GONE
            fabAddCity.visibility = View.VISIBLE

            supportFragmentManager.beginTransaction().remove(fragment).commit()
        })
        fragment.progressStatus.observe(this, Observer {
            updateProgressStatus(it)
        })
    }

    private fun getLocationPermission() {
        mLocationPermissionGranted = false
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationPermissionGranted = true
            loadMapView()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                }
            }
        }
    }

    private fun updateProgressStatus(progressStatus: ProgressStatus?) {
        when (progressStatus) {
            ProgressStatus.LOADING -> vProgressBar.show()
            ProgressStatus.COMPLETED -> vProgressBar.hide()
        }
    }

}