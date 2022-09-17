package com.svrutas.app.data

import com.google.firebase.firestore.GeoPoint

data class Ruta_estacion(
    var id:String? = null,
    val cities_towns: List<String>? = null,
    val departments_states: List<String>? = null,
    val name: String? = null,
    val type: String? = null
)
