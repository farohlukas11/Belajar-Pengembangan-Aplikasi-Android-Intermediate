package com.dicoding.storyappsub1.maps

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.storyappsub1.R
import com.dicoding.storyappsub1.ViewModelFactory
import com.dicoding.storyappsub1.databinding.ActivityMapsBinding
import com.dicoding.storyappsub1.model.ListStoryItem
import com.dicoding.storyappsub1.model.Result
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val mapsViewModel: MapsViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle()

        mapsViewModel.mediator.observe(this) {}
        mapsViewModel.user.observe(this) { tokenUser ->
            if (tokenUser != null) {
                mapsViewModel.getStoriesMap(tokenUser)
                    .observe(this) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> showLoading(true)
                                is Result.Success -> {
                                    showLoading(false)
                                    val mapsData = result.data

                                    if (!mapsData.error) {
                                        showLoading(false)
                                        showToast(mapsData.message)

                                        setupMaps(mapsData.listStory)
                                    }
                                }
                                is Result.Error -> {
                                    showLoading(false)
                                    showToast(getString(R.string.error_maps))
                                }
                            }
                        } else {
                            showToast(getString(R.string.error_offline))
                        }
                    }
            }
        }
    }

    private fun setupMaps(list: List<ListStoryItem>?) {
        list?.let {
            for (i in it) {
                val location = LatLng(i.lat, i.lon)
                mMap.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title(i.name)
                        .snippet(i.description)

                )
            }
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    companion object {
        const val TAG = "Maps_Activity"
    }
}