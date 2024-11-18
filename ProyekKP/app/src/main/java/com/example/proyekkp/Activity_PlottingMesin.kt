package com.example.proyekkp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.proyekkp.databinding.ActivityPlottingMesinBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Activity_PlottingMesin : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityPlottingMesinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_plotting_mesin)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        // Load data for spinners
        loadNoMesinSpinner()
        loadNamaPegawaiSpinner()
        loadNamaProdukSpinner()

        binding.btnsimpanplotting.setOnClickListener {
            val noMesin = binding.spinnerNoMesin.selectedItem?.toString()
            val namaPegawai = binding.spinnerNamaPegawai.selectedItem?.toString()
            val namaProduk = binding.spinnerNamaProduk.selectedItem?.toString()
            val qtyText = binding.txtqtyproduk.text.toString()
            val quantity = qtyText.toIntOrNull()
            val keterangan = binding.txtketeranganplotting.text.toString()

            if (noMesin != null && namaPegawai != null && namaProduk != null && quantity != null && quantity > 0) {
                // Cari idMesin berdasarkan noMesin
                db.collection("data_mesin")
                    .whereEqualTo("noMesin", noMesin)
                    .get()
                    .addOnSuccessListener { mesinDocuments ->
                        val idMesin = mesinDocuments.documents.firstOrNull()?.getString("id")

                        if (idMesin != null) {
                            // Cari idPegawai berdasarkan namaPegawai
                            db.collection("data_pegawai")
                                .whereEqualTo("namaPegawai", namaPegawai)
                                .get()
                                .addOnSuccessListener { pegawaiDocuments ->
                                    val idPegawai = pegawaiDocuments.documents.firstOrNull()?.getString("id")

                                    if (idPegawai != null) {
                                        // Simpan plotting mesin
                                        savePlottingMesin(idMesin, noMesin, idPegawai, namaPegawai, namaProduk, quantity, keterangan)
                                    } else {
                                        Toast.makeText(this, "Pegawai tidak ditemukan.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Gagal mencari pegawai: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Mesin tidak ditemukan.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal mencari mesin: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Semua field harus diisi dengan benar.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun savePlottingMesin(
        idMesin: String,
        noMesin: String,
        idPegawai: String,
        namaPegawai: String,
        namaProduk: String,
        quantity: Int,
        keterangan: String
    ) {
        val newPlotting = hashMapOf(
            "id" to "",
            "idMesin" to idMesin,
            "noMesin" to noMesin,
            "idPegawai" to idPegawai,
            "namaPegawai" to namaPegawai,
            "namaProduk" to namaProduk,
            "quantity" to quantity,
            "Keterangan" to keterangan,
            "tanggalplotting" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )

        // Simpan plotting mesin ke data_plotting_mesin
        db.collection("data_plotting_mesin")
            .add(newPlotting)
            .addOnSuccessListener { documentReference ->
                val generatedId = documentReference.id
                db.collection("data_plotting_mesin").document(generatedId)
                    .update("id", generatedId)
                    .addOnSuccessListener {
                        // Setelah berhasil menyimpan plotting, tambahkan jumlah barang
                        updateProductQuantity(namaProduk, quantity)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal memperbarui ID: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menyimpan plotting: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateProductQuantity(namaProduk: String, quantity: Int) {
        db.collection("data_produk")
            .whereEqualTo("namaProduk", namaProduk)
            .get()
            .addOnSuccessListener { productDocuments ->
                val productDocument = productDocuments.documents.firstOrNull()

                if (productDocument != null) {
                    val productId = productDocument.id
                    val currentQuantity = productDocument.getLong("jumlahBarang") ?: 0

                    // Tambahkan quantity ke jumlahBarang
                    val updatedQuantity = currentQuantity + quantity

                    // Perbarui jumlahBarang di data_produk
                    db.collection("data_produk").document(productId)
                        .update("jumlahBarang", updatedQuantity)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Jumlah barang berhasil diperbarui.", Toast.LENGTH_SHORT).show()
                            clearFields()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Gagal memperbarui jumlah barang: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Produk tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mencari produk: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun loadNoMesinSpinner() {
        db.collection("data_mesin")
            .whereEqualTo("statusmesin", true)
            .get()
            .addOnSuccessListener { documents ->
                val noMesinList = documents.mapNotNull { it.getString("noMesin") }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, noMesinList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerNoMesin.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat nomor mesin: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadNamaPegawaiSpinner() {
        db.collection("data_pegawai")
            .whereEqualTo("status", true) // Hanya memuat pegawai yang aktif
            .get()
            .addOnSuccessListener { documents ->
                // Filter data untuk menghindari Admin (idPegawai = "A001")
                val namaPegawaiList = documents.filterNot {
                    it.getString("idpegawai") == "A001"
                }.mapNotNull {
                    it.getString("namaPegawai")
                }

                // Set adapter untuk spinner
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, namaPegawaiList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerNamaPegawai.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat nama pegawai: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadNamaProdukSpinner() {
        db.collection("data_produk")
            .whereEqualTo("statusBarang", true)
            .get()
            .addOnSuccessListener { documents ->
                val namaProdukList = documents.mapNotNull { it.getString("namaProduk") }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, namaProdukList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerNamaProduk.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat nama produk: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        binding.spinnerNoMesin.setSelection(0)
        binding.spinnerNamaPegawai.setSelection(0)
        binding.spinnerNamaProduk.setSelection(0)
        binding.txtqtyproduk.text.clear()
        binding.txtketeranganplotting.text.clear()
    }
}

