package com.svrutas.app.data

import com.google.firebase.firestore.GeoPoint

data class Estacion(
    var id:String? = null,
    val city_town: String? = null,
    val department_state: String? = null,
    val location: GeoPoint? = null,
    val name: String? = null,
    val reference: String? = null,
    val quality: String? = null,
    val routes: List<Ruta_estacion>? = null,
    val type: String? = null
)
