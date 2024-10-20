package com.example.proyekkp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ClsTanggalMerah(
    var tanggalmerah : String, var keterangan : String, var pengkali : Double,
    var jenis : String
): Parcelable {
}
