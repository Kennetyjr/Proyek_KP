package com.example.proyekkp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.proyekkp.databinding.ActivityProfilePegawaiBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Activity_ProfilePegawai : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityProfilePegawaiBinding

    var idPegawai: String? = null
    var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_pegawai)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        // Menerima ID Pegawai dan Password dari Intent
        idPegawai = intent.getStringExtra("ID_PEGAWAI")
        password = intent.getStringExtra("PASSWORD")

        // Menampilkan Data Pegawai
        idPegawai?.let { id ->
            db.collection("data_pegawai")
                .whereEqualTo("idpegawai", id)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val document = documents.documents[0]
                        // Ambil data dari Firestore dan tampilkan di TextView
                        val namaPegawai = document.getString("namaPegawai") ?: "Tidak ada nama"
                        val noTelpon = document.getString("noTelpon") ?: "Tidak ada nomor telepon"
                        val password = document.getString("password") ?: "Tidak ada password"

                        binding.txtprofilenamapegawai.setText(id) // Set idpegawai ke nama
                        binding.txtprofileidpegawai.setText(namaPegawai)
                        binding.txtprofilepasswordpegawai.setText(password)
                        binding.txtprofilenotelppegawai.setText(noTelpon)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal mengambil data pegawai: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Ketika tombol update profile diklik
        binding.btnupdateprofile.setOnClickListener {
            val updatedNama = binding.txtprofileidpegawai.text.toString()
            val updatedPassword = binding.txtprofilepasswordpegawai.text.toString()
            val updatedNoTelpon = binding.txtprofilenotelppegawai.text.toString()

            if (updatedNama.isNotEmpty() && updatedPassword.isNotEmpty() && updatedNoTelpon.isNotEmpty()) {
                // Update data pegawai di Firestore
                idPegawai?.let { id ->
                    db.collection("data_pegawai")
                        .whereEqualTo("idpegawai", id)
                        .whereEqualTo("password", password)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty) {
                                val document = documents.documents[0]
                                val docId = document.id

                                // Update data pegawai
                                db.collection("data_pegawai").document(docId)
                                    .update(
                                        "namaPegawai", updatedNama,
                                        "password", updatedPassword,
                                        "noTelpon", updatedNoTelpon
                                    )
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Profile berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Gagal memperbarui profile: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Gagal mencari data pegawai: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

