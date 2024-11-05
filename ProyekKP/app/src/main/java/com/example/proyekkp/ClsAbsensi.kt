package com.example.proyekkp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class ClsAbsensi(
    var id : String,
    var id_pegawai : String,
    var tgl_absensi : Date,
    var gaji_harian : Int,
    var tanggal_merah : Boolean,
): Parcelable {
}