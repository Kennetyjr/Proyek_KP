package com.example.proyekkp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ClsAbsensi(
    var id : String,
    var id_pegawai : String,
    var tgl_absensi : String,
    var gaji_harian : Int,
): Parcelable {
}