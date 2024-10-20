package com.example.proyekkp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.proyekkp.databinding.ActivityRegisterPegawaiBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Activity_Register_Pegawai : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityRegisterPegawaiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_pegawai)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        binding.btnRegSave.setOnClickListener {
            val namaPegawai = binding.txtRegNamapegawai.text.toString()
            val noTelpon = binding.txtRegNotelpon.text.toString()
            val gajiHarian = binding.txtRegGajiharian.text.toString().toIntOrNull()

            if (namaPegawai.isNotEmpty() && noTelpon.isNotEmpty() && gajiHarian != null) {
                // Call the method to generate ID and save the employee
                generateIdAndSaveEmployee(namaPegawai, noTelpon, gajiHarian)
            } else {
                Toast.makeText(this, "Semua field harus diisi dengan benar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateIdAndSaveEmployee(namaPegawai: String, noTelpon: String, gajiHarian: Int) {
        // Extract initials from the employee's name for the ID
        val initials = namaPegawai.split(" ").mapNotNull { it.firstOrNull()?.toUpperCase() }.joinToString("")

        db.collection("data_pegawai")
            .get()
            .addOnSuccessListener { documents ->
                val recordCount = documents.size()

                // Generate the employee ID, e.g., JT001
                val idPegawai = initials + String.format("%03d", recordCount + 1)

                // Set role as "pegawai" and password as the employee's name
                val role = "pegawai"
                val password = namaPegawai

                // Create a map containing the new employee's data
                val newPegawai = mapOf(
                    "idpegawai" to idPegawai,
                    "namaPegawai" to namaPegawai,
                    "noTelpon" to noTelpon,
                    "gajiHarian" to gajiHarian,
                    "role" to role,
                    "password" to password,
                    "status" to true
                )

                // Save the new employee to Firestore
                db.collection("data_pegawai")
                    .add(newPegawai)
                    .addOnSuccessListener { documentReference ->
                        val firebaseId = documentReference.id

                        // Setelah dokumen tersimpan, update field "id" dengan document ID dari Firestore
                        db.collection("data_pegawai").document(firebaseId)
                            .update("id", firebaseId)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Pegawai berhasil didaftarkan dengan ID: $idPegawai", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error updating document ID: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching existing records: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
