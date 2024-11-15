package com.example.proyekkp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ClsPlottingMesin(
    var id : String,
    var idMesin : String,
    var noMesin : String,
    var idPegawai : String,
    var namaPegawai : String,
    var namaProduk: String,
    var quantity : Int,
    var Keterangan : String,
): Parcelable