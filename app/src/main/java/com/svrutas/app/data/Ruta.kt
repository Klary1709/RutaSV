package com.svrutas.app.data

import com.google.firebase.firestore.GeoPoint

data class Ruta(
    var id:String? = null,
    val cities_towns: List<String>? = null,
    val companies: List<String>? = null,
    val departments_states: List<String>? = null,
    val map_points: List<GeoPoint>? = null,
    val min_price: Double? = null,
    val max_price: Double? = null,
    val name: String? = null,
    val quality: String? = null,
    val stops: List<Estacion_ruta>? = null,
    val type: String? = null
)
