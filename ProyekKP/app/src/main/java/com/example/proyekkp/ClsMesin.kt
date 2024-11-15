package com.example.proyekkp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ClsMesin(
    var id : String,
    var idMesin : String,
    var noMesin: String,
    var namaMesin : String,
    var statusmesin : Boolean,
): Parcelable