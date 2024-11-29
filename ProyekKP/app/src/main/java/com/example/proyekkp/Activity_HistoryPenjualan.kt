package com.example.proyekkp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.proyekkp.databinding.ActivityHistoryPenjualanBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale

class Activity_HistoryPenjualan : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityHistoryPenjualanBinding
    private lateinit var adapter: AdapterHistoryPenjualan
    private val historyList = ArrayList<ClsHistoryPenjualan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history_penjualan)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        adapter = AdapterHistoryPenjualan(this, historyList)
        binding.listviewhistorypenjualan.adapter = adapter

        // Load default data (latest data, ascending order by timestamp)
        loadHistoryData(null)

        // Search by date
        binding.btncarihistorypenjualan.setOnClickListener {
            val tanggalInput = binding.editTextDateFilterhistory.text.toString().trim()
            if (tanggalInput.isEmpty()) {
                Toast.makeText(this, "Masukkan tanggal untuk pencarian!", Toast.LENGTH_SHORT).show()
            } else {
                loadHistoryData(tanggalInput)
            }
        }
    }

    private fun loadHistoryData(dateFilter: String?) {
        val query = if (dateFilter.isNullOrEmpty()) {
            // Default query: all data ordered by timestamp (ascending)
            db.collection("data_penjualan").orderBy("tanggalTransaksi", Query.Direction.ASCENDING)
        } else {
            // Query with date filter
            val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
            val date = sdf.parse(dateFilter)

            if (date == null) {
                Toast.makeText(this, "Format tanggal salah! Gunakan format: Oktober 3, 2024", Toast.LENGTH_SHORT).show()
                return
            }

            val startOfDay = date.time
            val endOfDay = startOfDay + 24 * 60 * 60 * 1000 - 1 // End of the day in milliseconds

            db.collection("data_penjualan")
                .whereGreaterThanOrEqualTo("tanggalTransaksi", startOfDay)
                .whereLessThanOrEqualTo("tanggalTransaksi", endOfDay)
        }

        query.get()
            .addOnSuccessListener { documents ->
                historyList.clear() // Clear existing data
                for (document in documents) {
                    val item = document.toObject<ClsHistoryPenjualan>()
                    item.id = document.id // Set document ID
                    historyList.add(item)
                }
                adapter.notifyDataSetChanged()

                if (historyList.isEmpty()) {
                    Toast.makeText(this, "Tidak ada data ditemukan!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
