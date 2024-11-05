package com.example.proyekkp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class ClsTanggalMerah(
    var tanggalmerah : Date,
    var keterangan : String,
    var pengkali : Double,
    var jenis : String
): Parcelable {
}
