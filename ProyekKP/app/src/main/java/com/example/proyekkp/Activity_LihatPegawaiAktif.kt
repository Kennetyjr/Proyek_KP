package com.example.proyekkp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.proyekkp.databinding.ActivityLihatPegawaiAktifBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Activity_LihatPegawaiAktif : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityLihatPegawaiAktifBinding
    lateinit var adapterListPegawai: AdapterListSemuaPegawaiAktif
    var listPegawai: ArrayList<ClsPegawai> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lihat_pegawai_aktif)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        // Inisialisasi adapter dan set ke ListView
        adapterListPegawai = AdapterListSemuaPegawaiAktif(this, listPegawai)
        binding.listviewsemuapegawaiaktif.adapter = adapterListPegawai

        // Muat data pegawai aktif
        loadPegawaiAktif()
    }

    private fun loadPegawaiAktif() {
        db.collection("data_pegawai")
            .whereEqualTo("role", "pegawai") // Filter hanya pegawai
            .whereEqualTo("status", true)   // Filter hanya yang aktif
            .get()
            .addOnSuccessListener { documents ->
                listPegawai.clear() // Bersihkan list sebelum menambahkan data baru
                for (document in documents) {
                    val pegawai = ClsPegawai(
                        id_pegawai = document.getString("idpegawai") ?: "",
                        nama_pegawai = document.getString("namaPegawai") ?: "",
                        password = document.getString("password") ?: "",
                        no_telpon = document.getString("noTelpon")?.toInt() ?: 0,
                        gaji_harian = document.getLong("gajiHarian")?.toInt() ?: 0,
                        gaji_mingguan = document.getLong("gajiMingguan")?.toInt() ?: 0,
                        role = "pegawai",
                        status = true,
                        id = document.id
                    )
                    listPegawai.add(pegawai) // Tambahkan ke list
                }
                adapterListPegawai.notifyDataSetChanged() // Perbarui tampilan adapter
            }
            .addOnFailureListener { exception ->
                // Tampilkan pesan jika gagal memuat data
                Toast.makeText(this, "Gagal mengambil data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
