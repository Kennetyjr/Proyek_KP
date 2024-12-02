package com.example.proyekkp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.proyekkp.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class Activity_Login : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityLoginBinding
    lateinit var adapter: AdapterListAbsenHariIni
    var absensiList = ArrayList<ClsAbsensi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        // Inisialisasi Adapter
//        adapter = AdapterListAbsenHariIni(this, absensiList)
//        binding.listviewabsenhariini.adapter = adapter

        // Load data absensi hari ini saat aplikasi dijalankan
        //loadAbsensiHariIni()


        binding.btnkeadmin.setOnClickListener {
            val nextIntent = Intent(this, Activity_HomeUtamaAdmin::class.java)
            startActivity(nextIntent)
        }

        binding.btnlogin.setOnClickListener {
            val idPegawai = binding.txtloginname.text.toString()
            val password = binding.txtloginpassword.text.toString()

            if (idPegawai.isNotEmpty() && password.isNotEmpty()) {
                db.collection("data_pegawai")
                    .get()
                    .addOnSuccessListener { documents ->
                        var userFound = false
                        for (item in documents) {
                            val dbIdPegawai = item.data["idpegawai"].toString()
                            val dbPassword = item.data["password"].toString()

                            if (dbIdPegawai == idPegawai && dbPassword == password) {
                                userFound = true
                                val role = item.data["role"].toString()

                                if (role == "pegawai") {
                                    val gajiHarian = item.data["gajiHarian"].toString().toInt()
                                    cekAbsensi(idPegawai, gajiHarian)
                                } else if (role == "admin") {
                                    val nextIntent = Intent(this, Activity_HomeAdmin::class.java)
                                    startActivity(nextIntent)
                                    Toast.makeText(this, "Berhasil masuk sebagai Admin", Toast.LENGTH_SHORT).show()
                                }
                                break
                            }
                        }
                        if (!userFound) {
                            Toast.makeText(this, "ID Pegawai atau Password salah", Toast.LENGTH_SHORT).show()
                        } else {
                            clearTextFields()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Gagal terhubung ke database: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "ID Pegawai dan Password harus diisi", Toast.LENGTH_SHORT).show()
            }
        }


        binding.btntohomepegawai.setOnClickListener {
            val idPegawai = binding.txtloginname.text.toString()
            val password = binding.txtloginpassword.text.toString()

            if (idPegawai.isNotEmpty() && password.isNotEmpty()) {
                db.collection("data_pegawai")
                    .get()
                    .addOnSuccessListener { documents ->
                        var userFound = false
                        for (item in documents) {
                            val dbIdPegawai = item.data["idpegawai"].toString()
                            val dbPassword = item.data["password"].toString()

                            if (dbIdPegawai == idPegawai && dbPassword == password) {
                                userFound = true
                                val nextIntent = Intent(this, Activity_Home_Pegawai::class.java)
                                // Mengirim idPegawai dan password ke Activity_Home_Pegawai
                                nextIntent.putExtra("ID_PEGAWAI", idPegawai)
                                nextIntent.putExtra("PASSWORD", password)
                                startActivity(nextIntent)
                                Toast.makeText(this, "Berhasil masuk sebagai Pegawai", Toast.LENGTH_SHORT).show()
                                break
                            }
                        }
                        if (!userFound) {
                            Toast.makeText(this, "ID Pegawai atau Password salah", Toast.LENGTH_SHORT).show()
                        } else {
                            clearTextFields()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal terhubung ke database: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "ID Pegawai dan Password harus diisi", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun cekAbsensi(idPegawai: String, gajiHarian: Int) {
        //val tanggalSekarang = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val tanggalSekarang = Calendar.getInstance().time

        // Query ke Firestore untuk mendapatkan data pegawai dengan idPegawai
        db.collection("data_pegawai")
            .whereEqualTo("idpegawai", idPegawai)
            .get()
            .addOnSuccessListener { pegawaiDocuments ->
                if (!pegawaiDocuments.isEmpty) {
                    val pegawaiDocument = pegawaiDocuments.documents[0]
                    val jumlahAbsensiMingguan = pegawaiDocument.getLong("jumlah_absensi_mingguan")?.toInt() ?: 0

                    if (jumlahAbsensiMingguan >= 7) {
                        // Reset jumlah_absensi_mingguan ke 1 jika sudah 7
                        db.collection("data_pegawai").document(pegawaiDocument.id)
                            .update("jumlah_absensi_mingguan", 1)
                            .addOnSuccessListener {
                                // Lanjutkan dengan menyimpan absensi
                                cekAbsensiHariIni(idPegawai, gajiHarian, tanggalSekarang.toString())
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Gagal memperbarui jumlah absensi: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // Lanjutkan jika jumlah absensi < 7
                        cekAbsensiHariIni(idPegawai, gajiHarian, tanggalSekarang.toString())
                    }
                } else {
                    Toast.makeText(this, "Pegawai tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengecek data pegawai: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cekAbsensiHariIni(idPegawai: String, gajiHarian: Int, tanggalSekarang: String) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Format tanggal tanpa waktu
        val formattedTanggalSekarang = dateFormat.format(Date()) // Dapatkan tanggal hari ini dalam format yang sama

        db.collection("data_absensi")
            .whereEqualTo("id_pegawai", idPegawai)
            .get()
            .addOnSuccessListener { documents ->
                var sudahAbsen = false

                for (document in documents) {
                    val tglAbsensi = document.getDate("tgl_absensi") // Ambil tanggal absensi yang ada di database
                    val formattedTglAbsensi = dateFormat.format(tglAbsensi) // Format tanggal absensi yang ada

                    if (formattedTglAbsensi == formattedTanggalSekarang) {
                        // Jika sudah ada absensi untuk tanggal yang sama
                        sudahAbsen = true
                        break
                    }
                }

                if (sudahAbsen) {
                    // Jika sudah absen, tampilkan pesan
                    Toast.makeText(this, "Anda sudah absen hari ini.", Toast.LENGTH_SHORT).show()
                } else {
                    // Jika belum absen, simpan absensi
                    simpanDataAbsensi(idPegawai, gajiHarian, tanggalSekarang)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengecek absensi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun simpanDataAbsensi(idPegawai: String, gajiHarian: Int, tanggalSekarang: String) {
        // Format tanggal saat ini
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedCurrentDate = dateFormat.format(Date())

        // Cek apakah tanggalSekarang adalah tanggal merah di Firestore
        db.collection("tanggal_merah")
            .get()
            .addOnSuccessListener { tanggalMerahDocuments ->
                var isTanggalMerah = false

                for (document in tanggalMerahDocuments) {
                    val tanggalMerahDate = document.getDate("tanggalmerah")
                    val formattedTanggalMerah = dateFormat.format(tanggalMerahDate)

                    // Bandingkan tanggal (tanpa waktu)
                    if (formattedCurrentDate == formattedTanggalMerah) {
                        isTanggalMerah = true
                        break
                    }
                }

                // Buat objek absensi dengan nilai tanggal_merah sesuai hasil cek tanggal merah
                val absensi = ClsAbsensi(
                    id = "",
                    id_pegawai = idPegawai,
                    tgl_absensi = Calendar.getInstance().time,
                    gaji_harian = gajiHarian,
                    tanggal_merah = isTanggalMerah
                )

                // Simpan absensi ke Firestore
                db.collection("data_absensi")
                    .add(absensi)
                    .addOnSuccessListener { documentReference ->
                        val generatedId = documentReference.id
                        db.collection("data_absensi").document(generatedId)
                            .update("id", generatedId)
                            .addOnSuccessListener {
                                // Setelah absensi berhasil disimpan, update jumlah_absensi_mingguan pegawai
                                updateJumlahAbsensiMingguan(idPegawai)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Gagal memperbarui ID absensi: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal menyimpan absensi: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengecek tanggal merah: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateJumlahAbsensiMingguan(idPegawai: String) {
        // Dapatkan dokumen pegawai
        db.collection("data_pegawai")
            .whereEqualTo("idpegawai", idPegawai)
            .get()
            .addOnSuccessListener { pegawaiDocuments ->
                if (!pegawaiDocuments.isEmpty) {
                    val pegawaiDocument = pegawaiDocuments.documents[0]
                    val jumlahAbsensiMingguan = pegawaiDocument.getLong("jumlah_absensi_mingguan")?.toInt() ?: 0

                    // Tambah jumlah_absensi_mingguan +1
                    db.collection("data_pegawai").document(pegawaiDocument.id)
                        .update("jumlah_absensi_mingguan", jumlahAbsensiMingguan + 1)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Absensi berhasil disimpan dan jumlah absensi mingguan diperbarui.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Gagal memperbarui jumlah absensi mingguan: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengambil data pegawai untuk update absensi mingguan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

//    private fun loadAbsensiHariIni() {
//        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//        val currentDate = dateFormat.format(Date()) // Format tanggal hari ini
//
//        absensiList.clear() // Bersihkan list sebelumnya
//        db.collection("data_absensi")
//            .whereEqualTo("tgl_absensi", currentDate)
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    val absensi = ClsAbsensi(
//                        id = document.getString("id") ?: "",
//                        id_pegawai = document.getString("id_pegawai") ?: "",
//                        tgl_absensi = document.getDate("tgl_absensi") ?: Date(),
//                        gaji_harian = document.getLong("gaji_harian")?.toInt() ?: 0,
//                        tanggal_merah = document.getBoolean("tanggal_merah") ?: false
//                    )
//                    absensiList.add(absensi) // Tambahkan ke list
//                }
//                adapter.notifyDataSetChanged() // Refresh adapter
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Gagal memuat data absensi: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }


    private fun clearTextFields() {
        binding.txtloginname.text.clear()
        binding.txtloginpassword.text.clear()
    }

}
