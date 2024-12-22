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
import java.text.SimpleDateFormat
import java.util.*

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
        setDateRangeText() // Memasukkan tanggal awal dan akhir minggu ke dalam textView

        binding.btnresettotalabsen.setOnClickListener {
            resetJumlahAbsensiMingguan()
        }
    }

    private fun resetJumlahAbsensiMingguan() {
        db.collection("data_pegawai")
            .whereEqualTo("role", "pegawai")
            .whereEqualTo("status", true)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val documentId = document.id

                    // Update jumlah_absensi_mingguan menjadi 0 untuk setiap pegawai
                    db.collection("data_pegawai")
                        .document(documentId)
                        .update("jumlah_absensi_mingguan", 0)
                        .addOnSuccessListener {
                            // Jika berhasil, lanjutkan ke pegawai berikutnya
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Gagal mereset data: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }

                // Setelah semua berhasil direset, perbarui data di listView
                for (pegawai in listPegawai) {
                    pegawai.jumlah_absensi_mingguan = 0
                }

                adapterListPegawaiGaji.notifyDataSetChanged()
                Toast.makeText(this, "Jumlah absensi mingguan berhasil direset", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal mengambil data pegawai: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun setDateRangeText() {
        // Mengatur format tanggal
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        // Hitung tanggal paling akhir (hari ini)
        val calEnd = Calendar.getInstance()
        val endDate = dateFormat.format(calEnd.time)
        binding.textView14.text = endDate // Tanggal paling akhir dari seminggu

        // Hitung tanggal paling awal (7 hari ke belakang dari hari ini)
        calEnd.add(Calendar.DAY_OF_YEAR, -6) // Tambah -6 karena hari ini dihitung
        val startDate = dateFormat.format(calEnd.time)
        binding.textView15.text = startDate // Tanggal paling awal dari seminggu
    }

    private fun loadDataPegawai() {
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
                        gaji_mingguan = document.getLong("gajiMingguan")?.toInt() ?: 0,
                        jumlah_absensi_mingguan = 0, // Akan diperbarui berdasarkan data absensi
                        role = "pegawai",
                        status = true
                    )
                    listPegawai.add(pegawai)
                }
                loadAbsensiMingguan()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal mengambil data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadAbsensiMingguan() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -7)
        val satuMingguLalu = Timestamp(cal.time)

        db.collection("data_absensi")
            .whereGreaterThanOrEqualTo("tgl_absensi", satuMingguLalu)
            .get()
            .addOnSuccessListener { absensiDocuments ->
                val absensiMap = mutableMapOf<String, Int>()

                for (document in absensiDocuments) {
                    val idPegawai = document.getString("id_pegawai")
                    if (idPegawai != null) {
                        absensiMap[idPegawai] = absensiMap.getOrDefault(idPegawai, 0) + 1
                    }
                }

                for (pegawai in listPegawai) {
                    pegawai.jumlah_absensi_mingguan = absensiMap[pegawai.id_pegawai] ?: 0
                }

                adapterListPegawaiGaji.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal mengambil data absensi: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
