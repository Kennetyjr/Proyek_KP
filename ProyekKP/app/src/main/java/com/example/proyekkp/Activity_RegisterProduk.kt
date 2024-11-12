package com.example.proyekkp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.proyekkp.databinding.ActivityRegisterProdukBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Activity_RegisterProduk : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityRegisterProdukBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_produk)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        binding.btntambahproduk.setOnClickListener {
            val namaproduk = binding.txtnamaproduk.text.toString().trim()
            var jumlahproduk = binding.txtjumlahproduk.text.toString().toIntOrNull() ?: 0

            if (namaproduk.isNotEmpty()) {
                val initials = generateInitials(namaproduk)

                // Ambil seluruh data produk dari koleksi untuk menentukan nomor urut tertinggi
                db.collection("data_produk")
                    .get()
                    .addOnSuccessListener { documents ->
                        // Temukan nomor urut tertinggi yang ada di idProduk
                        val maxNumber = documents.mapNotNull { doc ->
                            // Ambil bagian angka dari idProduk jika ada
                            doc.getString("idProduk")?.takeLast(3)?.toIntOrNull()
                        }.maxOrNull() ?: 0

                        val nextNumber = maxNumber + 1
                        val idProduk = "$initials${String.format("%03d", nextNumber)}"

                        // Buat data produk baru dengan statusBarang default true
                        val newProduct = ClsProduk(
                            id = "",
                            idProduk = idProduk,
                            namaProduk = namaproduk,
                            jumlahBarang = jumlahproduk,
                            statusBarang = true
                        )

                        // Simpan produk ke Firestore
                        db.collection("data_produk")
                            .add(newProduct)
                            .addOnSuccessListener { documentReference ->
                                val generatedId = documentReference.id

                                // Update dokumen dengan `id` dari Firestore
                                db.collection("data_produk").document(generatedId)
                                    .update("id", generatedId)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Produk berhasil ditambahkan dengan ID: $idProduk", Toast.LENGTH_SHORT).show()
                                        clearFields()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Gagal memperbarui ID produk: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Gagal menambahkan produk: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal memeriksa produk: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Nama Produk harus diisi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateInitials(namaproduk: String): String {
        val words = namaproduk.split(" ").filter { it.isNotEmpty() }
        return if (words.size == 1) {
            // Jika hanya satu kata, ambil dua huruf pertama dari kata tersebut
            words[0].take(2).uppercase()
        } else {
            // Jika lebih dari satu kata, ambil huruf pertama dari setiap kata
            words.joinToString("") { it.take(1).uppercase() }
        }
    }

    private fun clearFields() {
        binding.txtnamaproduk.text.clear()
        binding.txtjumlahproduk.text.clear()
        binding.txtidproduk.text.clear()
    }
}
