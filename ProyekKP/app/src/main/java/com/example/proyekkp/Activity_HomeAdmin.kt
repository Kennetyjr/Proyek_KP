package com.example.proyekkp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.proyekkp.databinding.ActivityHomeAdminBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.Timestamp
import java.util.Calendar

class Activity_HomeAdmin : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityHomeAdminBinding
    lateinit var adapterListPegawaiGaji: AdapterListPegawaiGaji
    var listPegawai: ArrayList<ClsPegawai> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_admin)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        adapterListPegawaiGaji = AdapterListPegawaiGaji(this, listPegawai)
        binding.listviewadmingajian.adapter = adapterListPegawaiGaji

        loadDataPegawai()

        binding.btncaripegawai.setOnClickListener {

        }
    }

    private fun loadDataPegawai() {
        // Mengambil data dari koleksi "data_pegawai"
        db.collection("data_pegawai")
            .whereEqualTo("role", "pegawai")
            .whereEqualTo("status", true)
            .get()
            .addOnSuccessListener { documents ->
                listPegawai.clear()
                for (document in documents) {
                    val pegawai = ClsPegawai(
                        id = document.id,
                        id_pegawai = document.getString("idpegawai") ?: "",
                        nama_pegawai = document.getString("namaPegawai") ?: "",
                        password = document.getString("password") ?: "",
                        no_telpon = document.getString("noTelpon")?.toInt() ?: 0,
                        gaji_harian = document.getLong("gajiHarian")?.toInt() ?: 0,
                        jumlah_absensi_mingguan = 0, // Akan diperbarui berdasarkan data absensi
                        role = "pegawai",
                        status = true
                    )
                    listPegawai.add(pegawai)
                }
                // Setelah mengambil data pegawai, lanjutkan mengambil data absensi mingguan
                loadAbsensiMingguan()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal mengambil data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadAbsensiMingguan() {
        // Hitung batas tanggal 7 hari ke belakang
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -7)
        val satuMingguLalu = Timestamp(cal.time)

        // Ambil data absensi untuk setiap pegawai dari seminggu yang lalu hingga sekarang
        db.collection("data_absensi")
            //.whereGreaterThanOrEqualTo("tgl_absensi", satuMingguLalu)
            .get()
            .addOnSuccessListener { absensiDocuments ->
                // Peta untuk melacak jumlah absen setiap pegawai
                val absensiMap = mutableMapOf<String, Int>()

                for (document in absensiDocuments) {
                    val idPegawai = document.getString("id_pegawai")
                    println("idPehgawai ==="+idPegawai);
                    if (idPegawai != null) {
                        println("masuk perhitungan: ==");
                        absensiMap[idPegawai] = absensiMap.getOrDefault(idPegawai, 0) + 1
                        println(absensiMap[idPegawai]);
                    }
                }

                println("Absensi Map "+absensiMap.toString())////////////

                // Perbarui jumlah absensi mingguan pada setiap objek pegawai di `listPegawai`
                for (pegawai in listPegawai) {
                    println("Pegawau "+pegawai.id_pegawai)////////////
                    pegawai.jumlah_absensi_mingguan = absensiMap[pegawai.id_pegawai] ?: 0
                }

                // Perbarui adapter untuk menampilkan data yang diperbarui
                adapterListPegawaiGaji.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal mengambil data absensi: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }



}
