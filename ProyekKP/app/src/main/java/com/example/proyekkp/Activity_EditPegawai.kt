package com.example.proyekkp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.proyekkp.databinding.ActivityEditPegawaiBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Activity_EditPegawai : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityEditPegawaiBinding
    private val listPegawaiAktif = mutableListOf<Pair<String, String>>() // List untuk spinner pegawai aktif
    private val listPegawaiNonAktif = mutableListOf<Pair<String, String>>() // List untuk spinner pegawai non-aktif

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_pegawai)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        // Load data untuk spinner
        loadSpinnerData()

        binding.btnpecatpegawai.setOnClickListener {
            val selectedPegawai = binding.spinneruntukpecatpegawai.selectedItem as? String
            val pegawaiId = listPegawaiAktif.find { it.first == selectedPegawai }?.second

            if (pegawaiId != null) {
                updatePegawaiStatus(pegawaiId, false)
            }
        }

        binding.btnterimakembalipegawai.setOnClickListener {
            val selectedPegawai = binding.spinneruntukterimakembalipegawai.selectedItem as? String
            val pegawaiId = listPegawaiNonAktif.find { it.first == selectedPegawai }?.second

            if (pegawaiId != null) {
                updatePegawaiStatus(pegawaiId, true)
            }
        }
    }

    private fun loadSpinnerData() {
        // Clear existing lists
        listPegawaiAktif.clear()
        listPegawaiNonAktif.clear()

        db.collection("data_pegawai")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val namaPegawai = document.getString("namaPegawai") ?: continue
                    val id = document.id
                    val role = document.getString("role")
                    val status = document.getBoolean("status") ?: false

                    if (role == "pegawai") {
                        if (status) {
                            listPegawaiAktif.add(Pair(namaPegawai, id))
                        } else {
                            listPegawaiNonAktif.add(Pair(namaPegawai, id))
                        }
                    }
                }

                // Update spinners
                val adapterAktif = ArrayAdapter(this, android.R.layout.simple_spinner_item, listPegawaiAktif.map { it.first })
                adapterAktif.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinneruntukpecatpegawai.adapter = adapterAktif

                val adapterNonAktif = ArrayAdapter(this, android.R.layout.simple_spinner_item, listPegawaiNonAktif.map { it.first })
                adapterNonAktif.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinneruntukterimakembalipegawai.adapter = adapterNonAktif
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updatePegawaiStatus(id: String, status: Boolean) {
        db.collection("data_pegawai").document(id)
            .update("status", status)
            .addOnSuccessListener {
                val message = if (status) "Pegawai diterima kembali" else "Pegawai dipecat"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                // Refresh data
                loadSpinnerData()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
