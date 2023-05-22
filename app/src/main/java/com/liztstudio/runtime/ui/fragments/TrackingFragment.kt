package com.liztstudio.runtime.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.liztstudio.runtime.R
import com.liztstudio.runtime.databinding.FragmentTrackingBinding
import com.liztstudio.runtime.services.Polyline
import com.liztstudio.runtime.services.TrackingService
import com.liztstudio.runtime.source.local.RunEntity
import com.liztstudio.runtime.ui.viewmodels.MainViewModel
import com.liztstudio.runtime.utils.Constant
import com.liztstudio.runtime.utils.TrackingUtils
import com.liztstudio.runtime.utils.TrackingUtils.calculatePolylineLength
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.round

const val CANCEL_DIALOG_TAG = "cancelDialog"

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var bind: FragmentTrackingBinding

    private var isTracking = false
    private var pathPoint = mutableListOf<Polyline>()

    private var map: GoogleMap? = null

    private var curTimeInMillis = 0L

    private var menu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        bind = FragmentTrackingBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (curTimeInMillis > 0L) {
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    private fun showCancelTrackingDialog() {
        CancelTrackingDialog().apply {
            setYesListener {
                stopRun()
            }
        }.show(parentFragmentManager, CANCEL_DIALOG_TAG)
    }

    private fun stopRun() {
        bind.tvTimer.text = "00:00:00:00"
        sendCommandToService(Constant.ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miCancelTracking -> {
                showCancelTrackingDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            val cancelTrackingDialog =
                parentFragmentManager.findFragmentByTag(CANCEL_DIALOG_TAG) as CancelTrackingDialog?
            cancelTrackingDialog?.setYesListener {
                stopRun()
            }
        }

        bind.mapView.onCreate(savedInstanceState)

        bind.mapView.getMapAsync {
            map = it
            addAllPolylines()
        }
        bind.btnToggleRun.setOnClickListener {
            toogleRun()
        }

        bind.btnFinishRun.setOnClickListener {
            zoomToSeeTrack()
            endRundAndSave()
        }
        subscriber()
    }

    private fun addLatestPolyLine() {
        if (pathPoint.isNotEmpty() && pathPoint.last().size > 1) {
            val preLastLatlng = pathPoint.last()[pathPoint.last().size - 2]
            val lastLatLng = pathPoint.last().last()
            val polylineOptions =
                PolylineOptions().color(Constant.POLYLINE_COLOR).width(Constant.POLYLINE_WIDTH)
                    .add(preLastLatlng)
                    .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addAllPolylines() {
        for (poly in pathPoint) {
            val polylineOptions =
                PolylineOptions().color(Constant.POLYLINE_COLOR).width(Constant.POLYLINE_WIDTH)
                    .addAll(poly)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun moveCameraToUser() {
        if (pathPoint.isNotEmpty() && pathPoint.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoint.last().last(),
                    15f
                )
            )
        }
    }

    private fun zoomToSeeTrack() {
        val bounds = LatLngBounds.Builder()
        for (poly in pathPoint) {
            for (pos in poly) {
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                bind.mapView.width,
                bind.mapView.height,
                (bind.mapView.height * 0.0f).toInt()
            )
        )
    }

    @set:Inject
    var weight = 80f
    private fun endRundAndSave() {
        map?.snapshot { bmp ->
            var distanceMeters = 0
            for (poly in pathPoint) {
                distanceMeters += calculatePolylineLength(poly).toInt()
            }
            val avgSpeed =
                round((distanceMeters / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f
            val dateTimestamp = Calendar.getInstance().timeInMillis
            val caloriesBurn = ((distanceMeters / 1000f) * weight).toInt()
            val run = RunEntity(
                img = bmp,
                timestamp = dateTimestamp,
                avgSpeedInKMH = avgSpeed,
                distanceInMeters = distanceMeters,
                caloriesBurned = caloriesBurn,
                timeInMillis = curTimeInMillis
            )
            viewModel.insertRun(run)
            Snackbar.make(
                bind.root,
                "Run saved success", Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }

    private fun subscriber() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner) {
            pathPoint = it
            addLatestPolyLine()
            moveCameraToUser()
        }
        TrackingService.timeRunInMillis.observe(viewLifecycleOwner) {
            curTimeInMillis = it
            val formattedTime = TrackingUtils.getFormattedTime(curTimeInMillis, true)
            bind.tvTimer.text = formattedTime
        }

    }

    private fun toogleRun() {
        if (isTracking) {
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(Constant.ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(Constant.ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking && curTimeInMillis > 0L) {
            bind.btnToggleRun.text = "Start"
            bind.btnFinishRun.visibility = View.VISIBLE
        } else if (isTracking) {
            bind.btnToggleRun.text = "Stop"
            menu?.getItem(0)?.isVisible = true
            bind.btnFinishRun.visibility = View.GONE

        }
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireActivity().startService(it)
        }

    override fun onResume() {
        super.onResume()
        bind.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        bind.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        bind.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        bind.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        bind.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        bind.mapView.onSaveInstanceState(outState)
    }
}