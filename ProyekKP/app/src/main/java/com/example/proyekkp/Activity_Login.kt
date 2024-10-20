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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        binding.btnlogin.setOnClickListener {
            val idPegawai = binding.txtloginname.text.toString()
            val password = binding.txtloginpassword.text.toString()

            if (idPegawai.isNotEmpty() && password.isNotEmpty()) {
                // Cek data di Firestore
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
                                    // Ambil gajiHarian dari Firestore berdasarkan idPegawai
                                    val gajiHarian = item.data["gajiHarian"].toString().toInt()

                                    // Cek apakah sudah absen hari ini
                                    cekAbsensi(idPegawai, gajiHarian)

                                } else if (role == "admin") {
                                    // Pindah ke ActivityAdmin
                                    Toast.makeText(this, "Berhasil masuk sebagai Admin", Toast.LENGTH_SHORT).show()
                                }
                                break
                            }
                        }
                        if (!userFound) {
                            Toast.makeText(this, "ID Pegawai atau Password salah", Toast.LENGTH_SHORT).show()
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
            //ke halaman pegawai
        }
    }

    private fun cekAbsensi(idPegawai: String, gajiHarian: Int) {
        // tanggal saat absen (tanggal-bulan-tahun)
        val tanggalSekarang = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        // Query ke Firestore untuk cek apakah sudah absen di hari yang sama
        db.collection("data_absensi")
            .whereEqualTo("id_pegawai", idPegawai)
            .whereEqualTo("tgl_absensi", tanggalSekarang)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Belum absen, lanjut simpan absensi
                    simpanDataAbsensi(idPegawai, gajiHarian, tanggalSekarang)
                } else {
                    // Sudah absen, tampilkan pesan
                    Toast.makeText(this, "Anda sudah absen hari ini.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengecek absensi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun simpanDataAbsensi(idPegawai: String, gajiHarian: Int, tanggalSekarang: String) {
        // Buat objek absensi (ID masih kosong karena akan diisi setelah disimpan)
        val absensi = ClsAbsensi(
            id = "",  // ID belum diisi
            id_pegawai = idPegawai,
            tgl_absensi = tanggalSekarang,
            gaji_harian = gajiHarian
        )

        // Simpan absensi ke Firestore
        db.collection("data_absensi")
            .add(absensi)
            .addOnSuccessListener { documentReference ->
                // Update ID dengan ID yang diberikan oleh Firestore
                val generatedId = documentReference.id
                db.collection("data_absensi").document(generatedId)
                    .update("id", generatedId)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Absensi berhasil disimpan dengan ID: $generatedId", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal memperbarui ID absensi: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menyimpan absensi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
