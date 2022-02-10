package com.example.polyenergy.ui.carmap

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.polyenergy.*
import com.example.polyenergy.databinding.FragmentMapBinding
import com.example.polyenergy.domain.ChargeInfo
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*


class CarMapFragment : Fragment(), OnMyLocationButtonClickListener {

    private val viewModel: CarMapViewModel by viewModels()

    private lateinit var map: GoogleMap

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var cameraInitialized: Boolean = false
    private var isSearchResult: Boolean = false

    private val bicycleIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(requireContext(), R.color.blue)
        BitmapHelper.vectorToBitmap(requireContext(), R.drawable.ic_charge_icon, color)
    }

    private lateinit var locationCallback: LocationCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cameraInitialized = false
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            addMarkers(googleMap)
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                googleMap.isMyLocationEnabled = true
                googleMap.setOnMyLocationButtonClickListener(this)
            }
            map = googleMap
            setCameraOnFavPlace()
        }



        requestLocationPermission()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    viewModel.loadCharges(location.latitude, location.longitude)
                    if(!cameraInitialized) {
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(location.latitude, location.longitude),
                                15F
                            )
                        )
                        cameraInitialized = true
                    }
                }
            }
        }

        requestNewLocationData()

        binding.loginButtonMap.setOnClickListener {
            val cookie = requireContext().getSharedPreferences(
                this.getString(R.string.app_name),
                Context.MODE_PRIVATE
            ).getString(USER_COOKIE, null)
            if (cookie != null) {
                Navigation.findNavController(binding.root).navigate(R.id.action_global_fav)
            } else {
                Navigation.findNavController(binding.root).navigate(R.id.action_global_login)
            }
        }

        setAutoCompletePlaces()

        return binding.root
    }

    private fun setCameraOnFavPlace() {
        val latitude = arguments?.getDouble(LATITUDE)
        val longitude = arguments?.getDouble(LONGITUDE)
        if (latitude != null && longitude != null) {
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(latitude, longitude),
                    15F
                )
            )
            cameraInitialized = true
        }
    }

    private fun requestLocationPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    locate()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    locate()
                }
                else -> {
                    // No location access granted.
                }
            }
        }
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun setAutoCompletePlaces() {
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyA6Sbv6Es-0CEEYZ_frHy9t_vTxGOLWWTY")
        }
        val fragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        fragment?.let {
            it.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
            it.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    viewModel.loadCharges(place.latLng.latitude, place.latLng.longitude)
                    isSearchResult = true
                }

                override fun onError(status: Status) {
                    Toast.makeText(
                        requireContext(),
                        "Erreur lors de la recherche",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }


    override fun onMyLocationButtonClick(): Boolean {
        requestNewLocationData()
        return false
    }

    private fun requestNewLocationData() {

        val locationRequest = LocationRequest.create().apply {
            interval = 5
            fastestInterval = 0
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            numUpdates = 1
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun locate() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            map.setOnMyLocationButtonClickListener(this)
        }
    }

    private fun addMarkers(googleMap: GoogleMap) {

        viewModel.result.observe(viewLifecycleOwner) { chargesResponse ->
            if (chargesResponse.isSuccessful()) {
                var count = 0
                val charges = chargesResponse.getAddresses()
                charges.forEach { charge ->
                    val marker = googleMap.addMarker(
                        MarkerOptions()
                            .title(charge.addressInfo.title)
                            .position(
                                LatLng(
                                    charge.addressInfo.latitude,
                                    charge.addressInfo.longitude
                                )
                            )
                            .icon(bicycleIcon)
                    )
                    marker?.tag = charge
                    if (count == 0 && isSearchResult) {
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(charge.addressInfo.latitude, charge.addressInfo.longitude),
                                15F
                            )
                        )
                        cameraInitialized = true
                    }
                    count++
                }
            }
        }


        googleMap.setInfoWindowAdapter(object : InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View? {
                return null
            }


            override fun getInfoContents(marker: Marker): View? {
                val charge = marker.tag as? ChargeInfo ?: return null

                val view = layoutInflater.inflate(
                    R.layout.marker_layout, null
                )
                view?.findViewById<TextView>(
                    R.id.text_view_title
                )?.text = charge.addressInfo.title
                view?.findViewById<TextView>(
                    R.id.text_view_address
                )?.text = charge.addressInfo.addressLine1
                return view
            }
        })

        googleMap.setOnInfoWindowClickListener {
            val cookie = requireContext().getSharedPreferences(
                this.getString(R.string.app_name),
                Context.MODE_PRIVATE
            ).getString(USER_COOKIE, null)
            if (cookie != null) {
                viewModel.setFavorite(it.tag as ChargeInfo, cookie)
            }
            // CHANGER ETAT BOUTON
        }
    }
}