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

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list_absen_hari_ini, null, true)

        val txtabsenhariiniid: TextView = rowView.findViewById(R.id.txtabsenhariiniid)
        val txtabsenhariininama: TextView = rowView.findViewById(R.id.txtabsenhariininama)
//        val txtabsenhariinitanggal: TextView = rowView.findViewById(R.id.txtabsenhariinitanggal)
//        val txtabsenhariinitanggalMerah: TextView = rowView.findViewById(R.id.txtabsenhariinitanggalMerah)

        // Get the current item
        val absensi = arr[position]

        // Format the date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(absensi.tgl_absensi)

        // Set the TextViews with the data
        txtabsenhariiniid.text = absensi.id_pegawai
        txtabsenhariininama.text = getEmployeeName(absensi.id_pegawai) // Retrieve employee name from data
//        txtabsenhariinitanggal.text = formattedDate
//        txtabsenhariinitanggalMerah.text = if (absensi.tanggal_merah) "Tanggal Merah" else "Hari Biasa"

        return rowView
    }

    private fun getEmployeeName(idPegawai: String): String {
        // You can replace this with code to get the employee name from your Firestore database
        // based on the idPegawai
        var employeeName = ""

        // Example query to get the employee name based on idPegawai:
        val db = FirebaseFirestore.getInstance()
        db.collection("data_pegawai")
            .whereEqualTo("idpegawai", idPegawai)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val employeeDocument = documents.documents[0]
                    employeeName = employeeDocument.getString("namaPegawai") ?: "Unknown"
                }
            }
        return employeeName
    }
}
