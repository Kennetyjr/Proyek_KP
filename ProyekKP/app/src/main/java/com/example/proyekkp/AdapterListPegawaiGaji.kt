package com.example.proyekkp

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView

class AdapterListPegawaiGaji(
    private val context: Activity,
    private val arr: ArrayList<ClsPegawai>
) : ArrayAdapter<ClsPegawai>(context, R.layout.custom_list_admingajian, arr) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list_admingajian, null, true)

        val txtnamapegawai: TextView = rowView.findViewById(R.id.txtnamapegawai)
        val txtidpegawai: TextView = rowView.findViewById(R.id.txtidpegawai)
        val txtgajiharian: TextView = rowView.findViewById(R.id.txtgajiharian)
        val txtbonus: TextView = rowView.findViewById(R.id.txtbonus)
        val txttotal: TextView = rowView.findViewById(R.id.txttotal)
        val txtinputlembur: EditText = rowView.findViewById(R.id.txtinputlembur)
        val txtabsensiMingguan: TextView = rowView.findViewById(R.id.txtabsensiMingguan)

        val pegawai = arr[position]

        txtidpegawai.text = pegawai.id_pegawai
        txtnamapegawai.text = pegawai.nama_pegawai
        txtgajiharian.text = pegawai.gaji_harian.toString()
        txtabsensiMingguan.text = "${pegawai.jumlah_absensi_mingguan}/7 hari"

        val gajiHarian = pegawai.gaji_harian
        val bonus = txtinputlembur.text.toString().toIntOrNull() ?: 0

        txtbonus.text = bonus.toString()
        txttotal.text = (gajiHarian + bonus).toString()

        return rowView
    }

}
