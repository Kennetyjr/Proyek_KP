package com.example.proyekkp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.proyekkp.databinding.ActivityEditMesinBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Activity_EditMesin : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityEditMesinBinding
    private val listMesinAktif = mutableListOf<Pair<String, String>>() // List untuk spinner mesin aktif

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_mesin)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        // Load data untuk spinner
        loadSpinnerMesin()

        binding.btnkonfirmasieditmesin.setOnClickListener {
            val selectedMesin = binding.spinnereditmesin.selectedItem as? String
            val mesinId = listMesinAktif.find { it.first == selectedMesin }?.second

            if (mesinId != null) {
                // Update statusmesin menjadi false di Firestore
                db.collection("data_mesin")
                    .document(mesinId)
                    .update("statusmesin", false)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Mesin $selectedMesin berhasil dinonaktifkan.", Toast.LENGTH_SHORT).show()
                        loadSpinnerMesin() // Refresh spinner setelah mengubah status
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error updating mesin: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Tidak ada mesin yang dipilih", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadSpinnerMesin() {
        // Clear existing list
        listMesinAktif.clear()

        db.collection("data_mesin")
            .whereEqualTo("statusmesin", true)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val noMesin = document.getString("noMesin") ?: continue
                    val id = document.id

                    listMesinAktif.add(Pair(noMesin, id))
                }

                // Update spinner dengan noMesin aktif
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listMesinAktif.map { it.first })
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnereditmesin.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching mesin: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
