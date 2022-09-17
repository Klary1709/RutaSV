package com.svrutas.app.ui.mapa

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.svrutas.app.HomeActivity
import com.svrutas.app.R
import com.svrutas.app.data.Estacion
import com.svrutas.app.data.Estacion_ruta
import com.svrutas.app.data.Ruta
import com.svrutas.app.databinding.MapaFragmentBinding
import com.svrutas.app.ui.estaciones.DetalleEstacionActivity
import com.svrutas.app.ui.estaciones.EstacionAdapter
import com.svrutas.app.utils.LocationPermissionHelper
import java.lang.ref.WeakReference

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class MapaFragment : Fragment() {
    companion object {
        fun newInstance() = MapaFragment()
    }

    private lateinit var binding: MapaFragmentBinding

    lateinit var mapView: MapView
    lateinit var annotationApi: AnnotationPlugin
    lateinit var locationPermissionHelper: LocationPermissionHelper

    val db = Firebase.firestore
    var rutaId: String? = ""
    val rutasCollection = "rutas"
    val listaEstaciones:MutableList<Estacion_ruta> = mutableListOf()
    val listaPuntos:MutableList<Point> = mutableListOf()

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }
    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = MapaFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mapView = binding.mapView

        //pide que se permita usar la localización
        locationPermissionHelper = LocationPermissionHelper(WeakReference(activity))
        locationPermissionHelper.checkPermissions {
            setMap()
        }

        return root
    }

    fun initLocationComponent(){
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.pulsingColor = Color.WHITE
            this.pulsingEnabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_puck,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_puck_halo,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(1.0) //0.0
                        literal(1.0) //0.6
                    }
                    stop {
                        literal(5.0)
                        literal(0.8)
                    }
                }.toJson()
            )
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        val currentNightMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                mapView.getMapboxMap().loadStyleUri(
                    "mapbox://styles/mario-olivo/ckqwsjftk1vxg17mtgzjys9ss"
                ) {
                    initLocationComponent();
                }
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                mapView.getMapboxMap().loadStyleUri("mapbox://styles/mario-olivo/ckv5v1r2d11j914oesvoqhati"){
                    initLocationComponent();
                }
            }
        }
        super.onConfigurationChanged(newConfig)
    }
    private fun setMap(){
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                mapView.getMapboxMap().loadStyleUri("mapbox://styles/mario-olivo/ckqwsjftk1vxg17mtgzjys9ss"){
                    initLocationComponent();
                }
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                mapView.getMapboxMap().loadStyleUri("mapbox://styles/mario-olivo/ckv5v1r2d11j914oesvoqhati"){
                    initLocationComponent();
                }
            }
        }
        annotationApi = mapView.annotations

        //para validar permisos antes de consultar los datos
        getPrefs()
        obtenerRutaFirestore()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))
    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    private fun getPrefs(){
        val prefs = activity?.getSharedPreferences(HomeActivity.KEY_DATA_RUTA, AppCompatActivity.MODE_PRIVATE )
        val id = prefs?.getString(HomeActivity.KEY_RUTA_ID, "").toString()
        if(id.isNotEmpty()) rutaId = id
    }
    private fun obtenerRutaFirestore(){
        if(rutaId!!.isNotEmpty()) {
            db.collection(rutasCollection)
                .document(rutaId!!)
                .get()
                .addOnSuccessListener { document ->
                    val newRuta = document.toObject<Ruta>() //se obtiene el documento

                    if (newRuta != null) { //si el documento NO es nulo (todavia existe la ruta)
                        (activity as AppCompatActivity).supportActionBar?.title = newRuta?.name!! //cambia el titulo del navbar

                        if (newRuta.map_points?.isNotEmpty() == true) { //si hay coordenadas guardadas
                            for (map_points in newRuta.map_points) {
                                listaPuntos.add(
                                    Point.fromLngLat(
                                        map_points.longitude,
                                        map_points.latitude
                                    )
                                )
                            }
                        }
                        if (newRuta.stops != null) { //si hay estaciones guardadas
                            for (estacion in newRuta.stops) {
                                listaEstaciones.add(estacion)
                            }
                        }

                        //se "pintan" las coordenadas y estaciones en el mapa
                        createPolyline()
                        createIconCoords()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this.context, "La ruta ya no existe :(", Toast.LENGTH_SHORT).show()
                }
        }else Toast.makeText(this.context, "No se ha seleccionado ruta :(", Toast.LENGTH_SHORT).show()
    }

    private fun createPolyline(){
        val polylineAnnotationManager = annotationApi.createPolylineAnnotationManager()
        val polylineAnnotationOptions: PolylineAnnotationOptions = PolylineAnnotationOptions()
            .withPoints(listaPuntos)
            .withLineColor(" #26D0C6")
            .withLineWidth(5.0)

        polylineAnnotationManager.create(polylineAnnotationOptions)
    }
    private fun createIconCoords(){
        if (listaEstaciones.isEmpty()) return

        bitmapFromDrawableRes(requireContext(), R.drawable.ic_stop_sign)?.let {

            var pointAnnotationOptions: PointAnnotationOptions

            listaEstaciones.forEachIndexed { i, estacion ->
                if(i==0 || (i==listaEstaciones.size - 1)){ //si es la primera o la ultima
                    pointAnnotationOptions = PointAnnotationOptions()
                        .withIconImage(it)
                        .withIconSize(.2)
                        .withTextHaloColor("#E2CC08")
                        .withTextHaloWidth(1.0)
                        .withTextAnchor(TextAnchor.LEFT)
                        .withTextOffset(listOf(0.8, 0.0))
                }else{
                    pointAnnotationOptions = PointAnnotationOptions()
                        .withIconImage(it)
                        .withIconSize(.2)
                        .withTextHaloColor("#FFFFFF")
                        .withTextHaloWidth(1.0)
                        .withTextAnchor(TextAnchor.LEFT)
                        .withTextOffset(listOf(0.8, 0.0))
                }

                val pointAnnotationManager = annotationApi.createPointAnnotationManager()
                pointAnnotationManager.addClickListener(OnPointAnnotationClickListener {
                    getEstacion(estacion.id!!)
                    true
                })
                pointAnnotationManager.create(pointAnnotationOptions
                    .withPoint(Point.fromLngLat(estacion.location?.longitude!!, estacion.location.latitude))
                    .withTextField("#$i")
                )
            }
        }
    }
    private fun detallesEstacion(estacion:Estacion){
        val view = View.inflate(activity, R.layout.alertdialog_estacion, null)
        view.findViewById<TextView>(R.id.tvNombre).text = estacion.name
        view.findViewById<TextView>(R.id.tvReferencia).text = estacion.reference

        val routes = estacion.routes?.map{ it.name }
        view.findViewById<TextView>(R.id.tvRutas).text = routes?.joinToString("\n")

        view.findViewById<Button>(R.id.btDetalles).setOnClickListener {
            val prefs: SharedPreferences.Editor? = activity?.getSharedPreferences(
                HomeActivity.KEY_DATA_RUTA,
                AppCompatActivity.MODE_PRIVATE
            )?.edit()
            prefs?.putString(HomeActivity.KEY_ESTACION_ID, estacion.id)
            prefs?.apply()

            val intent = Intent(context, DetalleEstacionActivity::class.java)
            startActivity(intent)
        }

        val detallesDialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
        detallesDialog.show()
    }
    private fun getEstacion(id:String){
        db.collection("estaciones")
            .document(id)
            .get()
            .addOnSuccessListener {
                val estacion = it.toObject(Estacion::class.java)
                estacion?.id = it.id
                detallesEstacion(estacion!!)
            }
    }
}