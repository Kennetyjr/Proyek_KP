package com.example.proyekkp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.proyekkp.databinding.ActivityEditProdukBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Activity_EditProduk : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityEditProdukBinding
    private val listProdukAktif = mutableListOf<Pair<String, String>>() // List untuk spinner produk aktif

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_produk)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        // Load data untuk spinner
        loadSpinnerProduk()

        binding.btnconfirmbuateditproduk.setOnClickListener {
            val selectedProduk = binding.spinnerbuateditproduk.selectedItem as? String
            val produkId = listProdukAktif.find { it.first == selectedProduk }?.second

            if (produkId != null) {
                // Ubah statusProduk menjadi false di Firestore
                db.collection("data_produk").document(produkId)
                    .update("statusBarang", false)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Status produk berhasil diperbarui", Toast.LENGTH_SHORT).show()

                        // Refresh spinner setelah perubahan
                        loadSpinnerProduk()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error updating status: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Tidak ada produk yang dipilih", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadSpinnerProduk() {
        // Clear existing list
        listProdukAktif.clear()

        db.collection("data_produk")
            .whereEqualTo("statusBarang", true)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val namaProduk = document.getString("namaProduk") ?: continue
                    val id = document.id

                    listProdukAktif.add(Pair(namaProduk, id))
                }

                // Update spinner dengan nama produk aktif
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listProdukAktif.map { it.first })
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerbuateditproduk.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching produk: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
