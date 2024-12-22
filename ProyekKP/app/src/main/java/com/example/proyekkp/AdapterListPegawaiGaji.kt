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

class AdapterListPegawaiGaji(
    private val context: Activity,
    private val arr: ArrayList<ClsPegawai>
) : ArrayAdapter<ClsPegawai>(context, R.layout.custom_list_admingajian, arr) {

    private val db = FirebaseFirestore.getInstance() // Inisialisasi Firebase Firestore

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list_admingajian, null, true)

        val txtnamapegawai: TextView = rowView.findViewById(R.id.txtnamapegawai)
        val txtidpegawai: TextView = rowView.findViewById(R.id.txtidpegawai)
        val txtgajiharian: TextView = rowView.findViewById(R.id.txtgajiharian)
        val txtbonus: TextView = rowView.findViewById(R.id.txtbonus)
        val txttotal: TextView = rowView.findViewById(R.id.txttotal)
        val txtfullminggumasukgaji: EditText = rowView.findViewById(R.id.txtfullminggumasukgaji)
        val txtinputlembur: EditText = rowView.findViewById(R.id.txtinputlembur)
        val txtabsensiMingguan: TextView = rowView.findViewById(R.id.txtabsensiMingguan)
        val btnhitungtotalakhir: Button = rowView.findViewById(R.id.btnhitungtotalakhir)

        val pegawai = arr[position]

        txtidpegawai.text = pegawai.id_pegawai
        txtnamapegawai.text = pegawai.nama_pegawai
        txtgajiharian.text = "Gaji Harian: " + pegawai.gaji_harian.toString()
        txtabsensiMingguan.text =  "Seminggu Masuk: ${pegawai.jumlah_absensi_mingguan}/7 hari"

        // Panggil fungsi untuk menghitung jumlah tanggal merah
        cekJumlahTanggalMerah(pegawai.id_pegawai) { jumlahTanggalMerah ->
            txtbonus.text = jumlahTanggalMerah.toString() // Tampilkan jumlah tanggal merah di txtbonus
        }

        btnhitungtotalakhir.setOnClickListener {
            val jumTanggalSetelahKurangiHariMerah = txtabsensiMingguan.text.toString().split("/")[0].toInt() - txtbonus.text.toString().toInt()
            val gajiHarian = txtgajiharian.text.toString().toInt()

            // Hitung gaji tanggal merah
            val hitungGajiTglMerah = (txtbonus.text.toString().toInt() * (gajiHarian * 1.5)).toInt()

            // Hitung gaji normal
            val hitungTotalGajiNormal = jumTanggalSetelahKurangiHariMerah * gajiHarian

            // Cek inputan lembur dan full minggu
            val inputLembur = if (txtinputlembur.text.toString().isEmpty()) 0 else txtinputlembur.text.toString().toInt()
            val fullMinggu = if (txtfullminggumasukgaji.text.toString().isEmpty()) 0 else txtfullminggumasukgaji.text.toString().toInt()

            // Inisialisasi total gaji
            var totalGaji = hitungGajiTglMerah + hitungTotalGajiNormal + inputLembur + fullMinggu

            // Tambahkan gaji mingguan jika absensi adalah 7/7 hari
            if (txtabsensiMingguan.text.toString() == "7/7 hari") {
                totalGaji += pegawai.gaji_mingguan // Tambahkan gaji mingguan dari data pegawai
            }

            // Tampilkan total gaji di TextView
            txttotal.text = totalGaji.toString()
        }


        return rowView
    }

    /**
     * Fungsi untuk mengecek jumlah tanggal merah dari data absensi pegawai di Firestore.
     * @param idPegawai ID pegawai untuk mencari data absensi.
     * @param callback Callback yang akan menerima jumlah tanggal merah.
     */
    private fun cekJumlahTanggalMerah(idPegawai: String, callback: (Int) -> Unit) {
        db.collection("data_absensi")
            .whereEqualTo("id_pegawai", idPegawai) // Filter berdasarkan ID pegawai
            .whereEqualTo("tanggal_merah", true) // Hanya ambil data dengan tanggal merah true
            .get()
            .addOnSuccessListener { documents ->
                val jumlahTanggalMerah = documents.size() // Hitung jumlah dokumen (tanggal merah)
                callback(jumlahTanggalMerah) // Kembalikan jumlah tanggal merah melalui callback
            }
            .addOnFailureListener {
                callback(0) // Jika terjadi kesalahan, kembalikan 0
            }
    }
}
