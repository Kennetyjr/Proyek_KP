package com.example.proyekkp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ClsPegawai(
    var id_pegawai : String,
    var nama_pegawai : String,
    var password : String,
    var no_telpon : Int,
    var gaji_harian : Int,
    var role : String,
    var status : Boolean,
    var id : String = ""
): Parcelable {
}