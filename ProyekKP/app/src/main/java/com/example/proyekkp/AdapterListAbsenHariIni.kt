package com.example.proyekkp

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterListAbsenHariIni(
    private val context: Activity,
    private val arr: ArrayList<ClsAbsensi>
) : ArrayAdapter<ClsAbsensi>(context, R.layout.custom_list_absen_hari_ini, arr) {

    private val db = FirebaseFirestore.getInstance()

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list_absen_hari_ini, null, true)

        val txtabsenhariiniid: TextView = rowView.findViewById(R.id.txtabsenhariiniid)
        val txtabsenhariininama: TextView = rowView.findViewById(R.id.txtabsenhariininama)

        // Dapatkan data absensi
        val absensi = arr[position]

        // Set nilai id_pegawai
        txtabsenhariiniid.text = absensi.id_pegawai

        // Dapatkan namaPegawai dari Firestore berdasarkan id_pegawai
        db.collection("data_pegawai")
            .whereEqualTo("idpegawai", absensi.id_pegawai)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val pegawaiDocument = documents.documents[0]
                    val namaPegawai = pegawaiDocument.getString("namaPegawai") ?: "Unknown"
                    txtabsenhariininama.text = namaPegawai
                } else {
                    txtabsenhariininama.text = "Pegawai tidak ditemukan"
                }
            }
            .addOnFailureListener { e ->
                txtabsenhariininama.text = "Gagal memuat nama"
            }

        return rowView
    }
}

