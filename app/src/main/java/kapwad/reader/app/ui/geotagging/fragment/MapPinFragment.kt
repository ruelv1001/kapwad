package kapwad.reader.app.ui.geotagging.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.maps.android.SphericalUtil
import kapwad.reader.app.R
import kapwad.reader.app.databinding.FragmentGeoMapBinding
import kapwad.reader.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapPinFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentGeoMapBinding? = null
    private val binding get() = _binding!!


    private lateinit var mMap: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private val polygonPoints = mutableListOf<LatLng>()
    private var currentPolygon: Polygon? = null

    private val markersList = mutableListOf<Marker>()
    private val RADIUS_IN_METERS = 20.0 // Define your radius
    private var theArea = ""

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                // Permissions granted
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGeoMapBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )


        addFusedLocationCallback()
    }

    private fun setClickListeners() = binding.run {
        areaTextView.doOnTextChanged { text, start, before, count ->
            if (areaTextView.text.equals("Not enough points to compute area.")) {
                finishTextView.isInvisible = true
            } else {
                finishTextView.isVisible = true
            }
        }

        mapTransparentCoverView.setOnSingleClickListener {
            Toast.makeText(
                requireActivity(),
                "Click the start button below",
                Toast.LENGTH_SHORT
            ).show()
        }




        getLocationTextView.setOnClickListener {
            if (checkInternetConnection()) {
                addFusedLocationCallback()
                getCurrentLocation()
                setViewDisplay()
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Internet is required to load map tiles",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        getLocationImageView.setOnClickListener {
            pauseImageView.isVisible = true
            getLocationImageView.isGone = true
            computeArea()
            stopLocationUpdates()
            setStopwatchTimer(TIMER_PAUSE)
        }

        finishTextView.setOnClickListener {
            showFinishConfirmationDialog()
        }

        pauseImageView.setOnClickListener {
            pauseImageView.isGone = true
            getLocationImageView.isVisible = true
            setStopwatchTimer(TIMER_START)
            if (checkInternetConnection()) {
                addFusedLocationCallback()
                getCurrentLocation()
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Internet is required to load map tiles",
                    Toast.LENGTH_LONG
                ).show()
            }
            computeArea()
        }

        resetImageView.setOnClickListener {
            Toast.makeText(
                requireActivity(),
                "Reset Map",
                Toast.LENGTH_SHORT
            ).show()
            setViewDisplayRESET()
            resetMap()
        }

        changeMapTypeImageView.setOnSingleClickListener {
            if (mapWhiteCoverView.isVisible) {
                mapWhiteCoverView.isGone = true
                areaTextView.isGone = true
                subAreaTextView.isGone = true
            } else {
                mapWhiteCoverView.isVisible = true
                areaTextView.isVisible = true
                subAreaTextView.isVisible = true
            }

        }
    }

    private fun setViewDisplay() = binding.run {
        getLocationTextView.isInvisible = true
        mapTransparentCoverView.isGone = true

        ongoingGroupLinearLayout.isVisible = true
        mapWhiteCoverView.isVisible = true
        resetImageView.isVisible = true
        areaTextView.isVisible = true
        subAreaTextView.isVisible = true
        chronometer.isVisible = true
        greenLineView.isVisible = true

        setStopwatchTimer(TIMER_START)
    }

    private fun setViewDisplayRESET() = binding.run {
        getLocationTextView.isVisible = true
        mapTransparentCoverView.isVisible = true
        finishTextView.isInvisible = true
        getLocationImageView.isVisible = true
        pauseImageView.isGone = true

        ongoingGroupLinearLayout.isGone = true
        mapWhiteCoverView.isGone = true
        resetImageView.isVisible = true
        areaTextView.isGone = true
        subAreaTextView.isGone = true
        chronometer.isGone = true
        greenLineView.isGone = true

        setStopwatchTimer(TIMER_STOP)
    }

    var pauseAt: Long = 0

    private fun setStopwatchTimer(value: String) = binding.run {
        when (value) {
            TIMER_START -> {
                chronometer.base = SystemClock.elapsedRealtime() - pauseAt
                chronometer.start()
            }

            TIMER_STOP -> {
                chronometer.base = SystemClock.elapsedRealtime()
            }

            else -> {
                //PAUSE
                pauseAt = SystemClock.elapsedRealtime() - chronometer.base
                chronometer.stop()
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener { latLng ->
            addPointToMap(latLng)
        }

        mMap.setOnMarkerClickListener { marker ->
            showDeleteMarkerDialog(marker)
            true
        }
    }

    private fun addPointToMap(latLng: LatLng) {
        polygonPoints.add(latLng)
        val marker = mMap.addMarker(
            MarkerOptions().position(latLng).title("Point ${polygonPoints.size}")
        )
        marker?.let { markersList.add(it) }
        drawPolygon()
    }

    //for testing
    private fun addTemporaryPoints() {
        val samplePoints = listOf(
            LatLng(14.738154, 121.044767),
            LatLng(14.738163, 121.045754),
            LatLng(14.738460, 121.045751),
            LatLng(14.738470, 121.044245),
            LatLng(14.738170, 121.044249)
        )

        polygonPoints.addAll(samplePoints)

        for (point in samplePoints) {
            val marker = mMap.addMarker(MarkerOptions().position(point).title("Sample Point"))
            markersList.add(marker!!)
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(samplePoints[0], 15f))

        drawPolygon()
    }

    private fun addFusedLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location: Location = locationResult.lastLocation!!
                if (location != null) {
                    updatePolygonWithLocation(location)
                }
            }
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (requireContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            requireContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {

            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(3000)
                .setMaxUpdateDelayMillis(3000)
                .build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun updatePolygonWithLocation(location: Location) {
        val currentPoint = LatLng(location.latitude, location.longitude)

        if (polygonPoints.isNotEmpty()) {
            val lastPoint = polygonPoints.last()

            if (currentPoint.latitude == lastPoint.latitude && currentPoint.longitude == lastPoint.longitude) {
                return // Avoid adding duplicate points on same location
            }

            /**
             *Adds an invisible radius to prevent repeated addition of markings or points.
             *  Avoids placing points or markers too close to one another.
             *
             *  For example, the minimum distance of points between each other should be atleast 20 meters
             */
            val lastLocation = Location("").apply {
                latitude = lastPoint.latitude
                longitude = lastPoint.longitude
            }
            val distance = location.distanceTo(lastLocation)
            if (distance < RADIUS_IN_METERS) { //20 METERS THRESHOLD within the radius,
                // Current location is so don't add the marker
                return
            }
        }

        polygonPoints.add(currentPoint)
        val marker = mMap.addMarker(
            MarkerOptions().position(currentPoint).title("Point ${polygonPoints.size}")
        )
        marker?.let {
            markersList.add(it)
        }

//        THIS MUST BE CALLED AFTER ADDING ANOTHER POIINT TO ENSURE CAMERA WILL BE CENTERED IMMEDIATELY AFTER ADDING POINT
        // Ensure bounds can be created
        val builder = LatLngBounds.Builder()
        for (latLng in polygonPoints) {
            builder.include(latLng)
        }
        val bounds = builder.build()

        val padding = 100 // Adjust padding as needed (in pixels)
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))



        drawPolygon()

    }

    private fun drawPolygon() {
        // Remove previous polygon if it exists
        currentPolygon?.remove()

        // Draw a new polygon from the updated points list
        if (polygonPoints.size > 2) {
            currentPolygon = mMap.addPolygon(
                PolygonOptions()
                    .addAll(polygonPoints)
                    .strokeColor(Color.RED)
                    .fillColor(Color.argb(50, 255, 0, 0))
            )
            calculateArea()
        }
    }

    private fun calculateArea() {
        val area = SphericalUtil.computeArea(polygonPoints)
        binding.areaTextView.text = String.format("%.2f m²", area)
    }

    private fun computeArea() {

        if (polygonPoints.size < 3) {
            binding.areaTextView.text = "Not enough points to compute area."
            binding.finishTextView.isInvisible = true
            return
        }

        val area = SphericalUtil.computeArea(polygonPoints)
        binding.finishTextView.isVisible = true
        binding.areaTextView.text =area.toString()
//        binding.areaTextView.text =
//            "${"%.2f".format(area)} m² \nArea: ${"%.2f".format(area / 10000)}"
        findNavController().navigate(MapPinFragmentDirections.actionGeoSubmit(area.toString()))
    }

    private fun resetMap() {
        currentPolygon?.remove()
        currentPolygon = null
        polygonPoints.clear()

        for (marker in markersList) {
            marker.remove()
        }

        markersList.clear()
    }

    private fun showDeleteMarkerDialog(marker: Marker) {
        AlertDialog.Builder(requireActivity())
            .setTitle("Delete Marker")
            .setMessage("Do you want to delete this marker?")
            .setPositiveButton("Delete") { _, _ ->
                deleteMarker(marker)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showFinishConfirmationDialog() {
        AlertDialog.Builder(requireActivity())
            .setMessage("Are you sure you are finished?")
            .setPositiveButton("Confirm") { _, _ ->
//                Toast.makeText(this,"FINISHED GO TO PREVIEW DIALOG",Toast.LENGTH_SHORT).show()
                computeArea()
                stopLocationUpdates()
                setStopwatchTimer(TIMER_STOP)

// intent = GeotagingSuccessActivity.getIntent(this@ComputeAreaActivity)
                //    startActivity(intent)


            }
            .setNegativeButton("Go back", null)
            .show()
    }

    private fun deleteMarker(marker: Marker) {
        val index = markersList.indexOf(marker)
        if (index != -1) {
            marker.remove()
            markersList.removeAt(index)
            polygonPoints.removeAt(index)
            drawPolygon()
        }

        Toast.makeText(requireActivity(), "Marker deleted", Toast.LENGTH_SHORT).show()
    }

    private fun checkInternetConnection(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onDestroy() = binding.run {
        chronometer.stop()
        super.onDestroy()
    }

    companion object {

        private const val TIMER_START = "start"
        private const val TIMER_STOP = "stop"
        private const val TIMER_PAUSE = "pause"

    }
}