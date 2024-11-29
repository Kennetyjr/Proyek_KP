package com.example.proyekkp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.proyekkp.databinding.ActivityHalamanListPlottingBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Date

class Activity_HalamanListPlotting : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityHalamanListPlottingBinding
    lateinit var adapter: AdapterListPlotting
    var plottingList: ArrayList<ClsPlottingMesin> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_halaman_list_plotting)
 
        db = FirebaseFirestore.getInstance()

        // Inisialisasi adapter untuk ListView
        adapter = AdapterListPlotting(this, plottingList)
        binding.listviewhalamanplotting.adapter = adapter

        // Listener untuk tombol cari
        binding.btncariplotting.setOnClickListener {
            val year = binding.datePickerhalamanplotting.year
            val month = binding.datePickerhalamanplotting.month
            val day = binding.datePickerhalamanplotting.dayOfMonth

            val calendar = Calendar.getInstance()
            calendar.set(year, month, day)
            val selectedDate = calendar.time

            // Ambil data dari Firestore sesuai tanggal
            loadDataForDate(selectedDate)
        }
    }

    private fun loadDataForDate(selectedDate: Date) {
        // Konversi tanggal awal dan akhir untuk query
        val calendar = Calendar.getInstance()
        calendar.time = selectedDate
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startDate = calendar.time

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endDate = calendar.time

        db.collection("data_plotting_mesin")
            .whereGreaterThanOrEqualTo("tanggalplotting", startDate)
            .whereLessThan("tanggalplotting", endDate)
            .get()
            .addOnSuccessListener { documents ->
                // Bersihkan list sebelumnya
                plottingList.clear()

                for (document in documents) {
                    val plotting = document.toObject(ClsPlottingMesin::class.java)
                    plottingList.add(plotting)
                }

                // Refresh adapter untuk menampilkan data baru
                adapter.notifyDataSetChanged()

                // Tampilkan pesan jika data ditemukan
                if (plottingList.isEmpty()) {
                    Toast.makeText(this, "Tidak ada data untuk tanggal ini.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
