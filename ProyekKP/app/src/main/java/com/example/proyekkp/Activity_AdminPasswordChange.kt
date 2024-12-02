package com.example.proyekkp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.proyekkp.databinding.ActivityAdminPasswordChangeBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Activity_AdminPasswordChange : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityAdminPasswordChangeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_password_change)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        // Load data admin ke form
        loadAdminData()

        binding.btnkonfirmasipasswordadmin.setOnClickListener {
            val newPassword = binding.txtprofilepasswordadmin.text.toString().trim()
            val newNoTelp = binding.txtprofilenotelpadmin.text.toString().trim()

            if (newPassword.isNotEmpty() && newNoTelp.isNotEmpty()) {
                updateAdminData(newPassword, newNoTelp)
            } else {
                Toast.makeText(this, "Password dan Nomor Telepon tidak boleh kosong.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadAdminData() {
        db.collection("data_pegawai")
            .whereEqualTo("role", "admin")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val adminDoc = documents.first()

                    val namaPegawai = adminDoc.getString("namaPegawai") ?: "N/A"
                    val idPegawai = adminDoc.getString("idpegawai") ?: "N/A"
                    val password = adminDoc.getString("password") ?: "N/A"
                    val noTelp = adminDoc.getString("noTelpon") ?: "N/A"

                    binding.txtprofileadminnama.setText(namaPegawai)
                    binding.txtprofileidadmin.setText(idPegawai)
                    binding.txtprofilepasswordadmin.setText(password)
                    binding.txtprofilenotelpadmin.setText(noTelp)
                } else {
                    Toast.makeText(this, "Data admin tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengambil data admin: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateAdminData(newPassword: String, newNoTelp: String) {
        db.collection("data_pegawai")
            .whereEqualTo("role", "admin")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val adminDoc = documents.first()
                    val adminId = adminDoc.id

                    db.collection("data_pegawai").document(adminId)
                        .update(
                            mapOf(
                                "password" to newPassword,
                                "noTelpon" to newNoTelp
                            )
                        )
                        .addOnSuccessListener {
                            Toast.makeText(this, "Data berhasil diperbarui.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Gagal memperbarui data: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Data admin tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengambil data admin: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
