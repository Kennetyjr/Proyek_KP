package com.example.proyekkp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.proyekkp.databinding.ActivityPenjualanBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Activity_Penjualan : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityPenjualanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_penjualan)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        // Load data produk ke Spinner
        loadProductsToSpinner()

        binding.btnbuypenjualan.setOnClickListener {
            saveDataToFirestore()
        }
    }

    private fun loadProductsToSpinner() {
        db.collection("data_produk")
            .get()
            .addOnSuccessListener { documents ->
                val productNames = ArrayList<String>()

                for (document in documents) {
                    val namaProduk = document.getString("namaProduk")
                    if (namaProduk != null) {
                        productNames.add(namaProduk)
                    }
                }

                // Set data produk ke spinner
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    productNames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerprodukpenjualan.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat produk: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveDataToFirestore() {
        val namaPembeli = binding.txtnamapembeli.text.toString().trim()
        val namaProduk = binding.spinnerprodukpenjualan.selectedItem?.toString() ?: ""
        val jumlahBarang = binding.txtjumlahbarangjual.text.toString().trim()
        val keterangan = binding.txtpenjualanketerangan.text.toString().trim()
        val catatan = binding.txtcatatanpenjualan.text.toString().trim()

        if (namaPembeli.isEmpty() || jumlahBarang.isEmpty() || namaProduk.isEmpty()) {
            Toast.makeText(this, "Harap lengkapi semua data!", Toast.LENGTH_SHORT).show()
            return
        }

        val jumlahBarangDibeli = jumlahBarang.toIntOrNull()
        if (jumlahBarangDibeli == null || jumlahBarangDibeli <= 0) {
            Toast.makeText(this, "Jumlah barang harus berupa angka positif!", Toast.LENGTH_SHORT).show()
            return
        }

        // Cek stok produk di Firestore
        db.collection("data_produk")
            .whereEqualTo("namaProduk", namaProduk)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "Produk tidak ditemukan!", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val produkDocument = documents.documents[0]
                val stokTersedia = produkDocument.getLong("jumlahBarang")?.toInt() ?: 0

                if (jumlahBarangDibeli > stokTersedia) {
                    Toast.makeText(this, "Stok barang tidak mencukupi! Stok tersedia: $stokTersedia", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Hitung jumlah data saat ini untuk ID Penjualan manual
                db.collection("data_penjualan")
                    .get()
                    .addOnSuccessListener { penjualanDocuments ->
                        val totalData = penjualanDocuments.size() + 1
                        val idPenjualan = "DTP0$totalData"

                        // Simpan data penjualan ke Firestore
                        val penjualan = hashMapOf(
                            "idPenjualan" to idPenjualan,
                            "namaPembeli" to namaPembeli,
                            "namaProduk" to namaProduk,
                            "jumlahBarang" to jumlahBarang,
                            "keterangan" to keterangan,
                            "catatan" to catatan,
                            "tanggalTransaksi" to com.google.firebase.firestore.FieldValue.serverTimestamp(), // Waktu saat ini
                            "status" to false // Status default
                        )

                        db.collection("data_penjualan")
                            .add(penjualan)
                            .addOnSuccessListener { documentReference ->
                                val documentId = documentReference.id // ID dokumen Firestore

                                // Perbarui dokumen dengan kolom "id"
                                db.collection("data_penjualan").document(documentId)
                                    .update("id", documentId)
                                    .addOnSuccessListener {
                                        // Perbarui stok barang di data_produk
                                        val stokBaru = stokTersedia - jumlahBarangDibeli
                                        db.collection("data_produk").document(produkDocument.id)
                                            .update("jumlahBarang", stokBaru)
                                            .addOnSuccessListener {
                                                Toast.makeText(this, "Penjualan berhasil disimpan! ID: $documentId", Toast.LENGTH_SHORT).show()
                                                resetFields()
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(this, "Gagal memperbarui stok: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Gagal menyimpan kolom ID: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Gagal menyimpan data penjualan: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal menghitung data penjualan: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat data produk: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun resetFields() {
        binding.txtnamapembeli.text.clear()
        binding.txtjumlahbarangjual.text.clear()
        binding.txtpenjualanketerangan.text.clear()
        binding.txtcatatanpenjualan.text.clear()
        binding.spinnerprodukpenjualan.setSelection(0)
    }
}
