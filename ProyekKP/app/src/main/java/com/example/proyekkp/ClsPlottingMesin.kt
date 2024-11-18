package com.example.proyekkp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class ClsPlottingMesin(
    var id: String = "",
    var idMesin: String = "",
    var noMesin: String = "",
    var idPegawai: String = "",
    var namaPegawai: String = "",
    var namaProduk: String = "",
    var quantity: Int = 0,
    var Keterangan: String = "",
    var tanggalplotting: Date = Date()
) : Parcelable
