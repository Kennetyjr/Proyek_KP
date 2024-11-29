package com.example.proyekkp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class ClsHistoryPenjualan(
    var id: String = "",
    var idPenjualan: String = "",
    var namaPembeli: String = "",
    var namaProduk: String = "",
    var jumlahBarang: String = "",
    var keterangan: String = "",
    var catatan: String = "",
    var tanggaltransaksi: Date = Date(),
    var status : Boolean = false
): Parcelable