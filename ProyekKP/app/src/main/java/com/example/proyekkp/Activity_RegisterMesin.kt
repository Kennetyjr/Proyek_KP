package com.example.proyekkp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.proyekkp.databinding.ActivityRegisterMesinBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Activity_RegisterMesin : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityRegisterMesinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_mesin)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        binding.btnsubmitregistermesin.setOnClickListener {
            val noMesin = binding.txtnomesin.text.toString().trim()
            val namaMesin = binding.txtnamamesin.text.toString().trim()

            if (namaMesin.isNotEmpty() && noMesin.isNotEmpty()) {
                // Cek apakah noMesin sudah digunakan
                db.collection("data_mesin")
                    .whereEqualTo("noMesin", noMesin)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            val initials = generateInitials(namaMesin)

                            // Periksa nomor urut tertinggi di data_mesin untuk ID Mesin
                            db.collection("data_mesin")
                                .whereGreaterThanOrEqualTo("idMesin", initials)
                                .whereLessThan("idMesin", initials + "Z")
                                .get()
                                .addOnSuccessListener { docs ->
                                    val maxNumber = docs.mapNotNull { doc ->
                                        doc.getString("idMesin")?.removePrefix(initials)?.toIntOrNull()
                                    }.maxOrNull() ?: 0

                                    val nextNumber = maxNumber + 1
                                    val idMesin = "$initials${String.format("%03d", nextNumber)}"

                                    // Buat data mesin baru dengan statusMesin default true
                                    val newMachine = ClsMesin(
                                        id = "",
                                        idMesin = idMesin,
                                        noMesin = noMesin,
                                        namaMesin = namaMesin,
                                        statusmesin = true
                                    )

                                    // Simpan mesin ke Firestore
                                    db.collection("data_mesin")
                                        .add(newMachine)
                                        .addOnSuccessListener { documentReference ->
                                            val generatedId = documentReference.id

                                            // Update dokumen dengan `id` dari Firestore
                                            db.collection("data_mesin").document(generatedId)
                                                .update("id", generatedId)
                                                .addOnSuccessListener {
                                                    Toast.makeText(this, "Mesin berhasil ditambahkan dengan ID: $idMesin", Toast.LENGTH_SHORT).show()
                                                    clearFields()
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(this, "Gagal memperbarui ID mesin: ${e.message}", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(this, "Gagal menambahkan mesin: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Gagal memeriksa ID mesin: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            // Jika noMesin sudah ada
                            Toast.makeText(this, "Nomor Mesin ini sudah dipakai. Silakan pilih nomor lain.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal memeriksa nomor mesin: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Nomor Mesin dan Nama Mesin harus diisi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateInitials(namaMesin: String): String {
        // Ambil tiga huruf pertama dari namaMesin atau lebih sedikit jika tidak tersedia
        return namaMesin.take(3).uppercase()
    }

    private fun clearFields() {
        binding.txtnomesin.text.clear()
        binding.txtnamamesin.text.clear()
    }
}
