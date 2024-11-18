package com.example.proyekkp

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView

class AdapterListPlotting(
    private val context: Activity,
    private val arr: ArrayList<ClsPlottingMesin>
) : ArrayAdapter<ClsPlottingMesin>(context, R.layout.custom_list_halamanplotting, arr) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list_halamanplotting, null, true)

        val nomesin: TextView = rowView.findViewById(R.id.txthlmplottingnomesin)
        val namapegawai: TextView = rowView.findViewById(R.id.txthlmplottingnamapegawai)
        val namaproduk: TextView = rowView.findViewById(R.id.txthlmplottingnamaproduk)
        val quantity: TextView = rowView.findViewById(R.id.txthlmplottingquantity)
        val tanggal: TextView = rowView.findViewById(R.id.txthlmplottingtanggal)

        val plotting = arr[position]
        nomesin.text = "Nomor Mesin : " + plotting.noMesin
        namapegawai.text = "Nama Pegawai : " + plotting.namaPegawai
        namaproduk.text = "Nama Produk : " + plotting.namaProduk
        quantity.text = "Jumlah : " + plotting.quantity.toString()
        tanggal.text = "Tanggal : " + plotting.tanggalplotting.toString()

        return rowView
    }


}
