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


        txtidpegawai.text = arr[position].id_pegawai
        txtnamapegawai.text = arr[position].nama_pegawai
        txtgajiharian.text = arr[position].gaji_harian.toString()


        val gajiHarian = arr[position].gaji_harian
        val bonus = txtinputlembur.text.toString().toIntOrNull() ?: 0

        txtbonus.text = bonus.toString()
        txttotal.text = (gajiHarian + bonus).toString()

        return rowView
    }
}
