package com.example.proyekkp

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AdapterListSemuaPegawaiAktif(
    private val context: Activity,
    private val arr: ArrayList<ClsPegawai>
) : ArrayAdapter<ClsPegawai>(context, R.layout.custom_list_semuapegawaiaktif, arr) {

    private val db = FirebaseFirestore.getInstance() // Inisialisasi Firebase Firestore

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list_semuapegawaiaktif, null, true)

        val txtidpegawai: TextView = rowView.findViewById(R.id.txtaktifpegawaiid)
        val txtnamapegawai: TextView = rowView.findViewById(R.id.txtaktifpegawainama)
        val txtpassword: TextView = rowView.findViewById(R.id.txtaktifpegawaipassword)

        val pegawai = arr[position]

        txtidpegawai.text = "ID : "+ pegawai.id_pegawai
        txtnamapegawai.text = "Nama: "+ pegawai.nama_pegawai
        txtpassword.text = "Password: "+ pegawai.password


        return rowView
    }


}
