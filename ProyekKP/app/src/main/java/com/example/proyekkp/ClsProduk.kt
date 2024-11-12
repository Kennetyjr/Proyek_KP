package com.example.proyekkp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ClsProduk(
    var id : String,
    var idProduk : String,
    var namaProduk: String,
    var jumlahBarang : Int,
    var statusBarang : Boolean,
): Parcelable