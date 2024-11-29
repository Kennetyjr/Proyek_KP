package com.example.proyekkp

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterHistoryPenjualan(
    private val context: Activity,
    private val arr: ArrayList<ClsHistoryPenjualan>
) : ArrayAdapter<ClsHistoryPenjualan>(context, R.layout.custom_list_historypenjualan, arr) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list_historypenjualan, null, true)

        val txthistoryidpenjualan: TextView = rowView.findViewById(R.id.txthistoryidpenjualan)
        val txthistorynamapembeli: TextView = rowView.findViewById(R.id.txthistorynamapembeli)
        val txthistorynamaproduk: TextView = rowView.findViewById(R.id.txthistorynamaproduk)
        val txthistoryjumlahprodukpenjualan: TextView = rowView.findViewById(R.id.txthistoryjumlahprodukpenjualan)
        val txthistoryketerangan: TextView = rowView.findViewById(R.id.txthistoryketerangan)
        val txthistorycatatan: TextView = rowView.findViewById(R.id.txthistorycatatan)
        val txttanggalhistorypenjualan: TextView = rowView.findViewById(R.id.txttanggalhistorypenjualan)

        val item = arr[position]

        txthistoryidpenjualan.text = "ID: " + item.idPenjualan
        txthistorynamapembeli.text = "Pembeli: " + item.namaPembeli
        txthistorynamaproduk.text = "Nama Produk: "+item.namaProduk
        txthistoryjumlahprodukpenjualan.text = "Jumlah Dibeli: " + item.jumlahBarang
        txthistoryketerangan.text = "Keterangan: " + item.keterangan
        txthistorycatatan.text = "Catatan: " + item.catatan

        val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        txttanggalhistorypenjualan.text = sdf.format(item.tanggaltransaksi)

        return rowView
    }
}
