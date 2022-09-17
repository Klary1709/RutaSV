package com.svrutas.app.data

import com.google.firebase.firestore.GeoPoint

data class Estacion_ruta(
    val id: String? = null,
    val location: GeoPoint? = null,
    val name: String? = null
)
