package ru.shum.yandexapitest.util

import android.content.Context
import ru.shum.common.enums.TransportType
import ru.shum.yandexapitest.R

fun getTransportImage(name: String): Int {
    return when (name) {
        TransportType.PLANE.value -> R.drawable.ic_plane
        TransportType.TRAIN.value -> R.drawable.ic_train
        TransportType.SUBURBAN.value -> R.drawable.ic_electric_train
        TransportType.BUS.value -> R.drawable.ic_bus
        TransportType.WATER.value -> R.drawable.ic_water
        TransportType.HELICOPTER.value -> R.drawable.ic_helicopter
        else -> R.drawable.ic_train
    }
}

fun getTransportNameInRussian(context: Context, name: String): String {
    val transportNameResId = when (name) {
        TransportType.PLANE.value -> R.string.plane
        TransportType.TRAIN.value -> R.string.train
        TransportType.SUBURBAN.value -> R.string.electric_train
        TransportType.BUS.value -> R.string.bus
        TransportType.WATER.value -> R.string.water
        TransportType.HELICOPTER.value -> R.string.helicopter
        else -> R.string.unknown
    }
    return context.getString(transportNameResId)
}