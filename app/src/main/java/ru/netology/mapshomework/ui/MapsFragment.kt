package ru.netology.mapshomework.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import com.yandex.runtime.ui_view.ViewProvider
import kotlinx.android.synthetic.main.fragment_maps.*
import ru.netology.mapshomework.R
import ru.netology.mapshomework.base.BaseFragment
import ru.netology.mapshomework.core.viewmodel.ViewModelFactory
import ru.netology.mapshomework.databinding.FragmentMapsBinding
import ru.netology.mapshomework.utils.getAppComponent
import timber.log.Timber
import javax.inject.Inject


class MapsFragment : BaseFragment<FragmentMapsBinding>(),
    CameraListener, Session.SearchListener, GeoObjectTapListener {

    private val args: MapsFragmentArgs by navArgs()

    @Inject
    lateinit var mapKit: MapKit

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: MapViewModel by viewModels {
        factory
    }

    private val searchManager: SearchManager by lazy {
        SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    }
    private var session: Session? = null
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ru.netology.mapshomework.databinding.FragmentMapsBinding
        get() = FragmentMapsBinding::inflate

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getAppComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() = with(binding) {
        mapView.map.addCameraListener(this@MapsFragment)

        mapView.map.addTapListener(this@MapsFragment)

        viewModel.destination.observe(viewLifecycleOwner) { marker ->
            if (marker != null) {
                val point = Point(marker.latitude, marker.longitude)
                mapView.map.move(
                    CameraPosition(point, 14.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 2.0f),
                    null
                )
                val mark = mapView.map.mapObjects.addPlacemark(
                    point,
                    fromVector(R.drawable.map_marker_radius),
                )
                mark.setText(marker.description)
            }
        }

        val id = args.markId
        if (id != 0L) {
            viewModel.goToMark(id)
        }

    }

    override fun onStart() = with(binding) {
        super.onStart()
        mapKit.onStart()
        mapView.onStart()
    }

    override fun onStop() = with(binding) {
        mapView.onStop()
        mapKit.onStop()
        super.onStop()
    }

    override fun onCameraPositionChanged(
        map: Map,
        position: CameraPosition,
        reason: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished) {

        }
    }

    override fun onSearchResponse(response: Response) = with(binding) {
        val mapObjects = mapView.map.mapObjects
        mapObjects.clear()
        response.collection.children.forEach { searchResult ->
            searchResult.obj?.descriptionText
            searchResult.obj?.geometry?.get(0)?.point?.apply {
                Timber.d("Point: $this")
                mapObjects.addPlacemark(this, fromVector(R.drawable.map_marker_radius))

                mapView.map.move(
                    CameraPosition(this, 14.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 2.0f),
                    null
                )
            }
        }
    }

    override fun onSearchError(error: Error) {
        when (error) {
            is NetworkError -> showSnackbar("Problem with network!")
            is RemoteError -> showSnackbar("Problem with remote server!")
            else -> showSnackbar("Unknown error!")
        }
    }

    private fun submitQuery(query: String) = with(binding) {
        session = searchManager.submit(
            query,
            VisibleRegionUtils.toPolygon(mapView.map.visibleRegion),
            SearchOptions(),
            this@MapsFragment
        )
    }


    private fun fromVector(@DrawableRes vectorId: Int): ViewProvider {
        return ViewProvider(
            View(requireContext()).apply {
                background = AppCompatResources.getDrawable(requireContext(), vectorId)
            }
        )
    }

    override fun onObjectTap(event: GeoObjectTapEvent): Boolean {
        val edit = EditText(requireContext())
        val point = event.geoObject.geometry[0].point
        edit.layoutParams = ViewGroup.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_marks_alert)
            .setMessage(R.string.add_marks_description)
            .setView(edit)
            .setIcon(R.drawable.map_marker_radius)
            .setPositiveButton(
                R.string.add_marks_confirm
            ) { _, _ ->
                val description = edit.text.toString()
                if (description.isNotBlank()) {
                    if (point != null) {
                        viewModel.addMark(description, point)
                        val mark = binding.mapView.map.mapObjects.addPlacemark(
                            point,
                            fromVector(R.drawable.map_marker_radius)
                        )
                        mark.setText(description)
                    }
                }
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            .show()
        return true
    }

}