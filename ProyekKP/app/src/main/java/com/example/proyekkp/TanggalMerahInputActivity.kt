package com.example.proyekkp

import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Date

class TanggalMerahInputActivity : AppCompatActivity() {
    private lateinit var txt_keterangan: EditText
    private lateinit var txt_pengkali: EditText
    private lateinit var txt_jenis: EditText
    private lateinit var btn_tambah: Button
    private lateinit var datePicker: DatePicker

    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tanggal_merah_input)

        FirebaseApp.initializeApp(this)
        db = FirebaseFirestore.getInstance()

        datePicker = findViewById(R.id.datePickertanggalmerah)
        txt_keterangan = findViewById(R.id.txt_keterangan)
        txt_pengkali = findViewById(R.id.txt_pengkali)
        txt_jenis = findViewById(R.id.txt_jenis)
        btn_tambah = findViewById(R.id.btn_tambah_tanggal)

        btn_tambah.setOnClickListener {
            val keterangan = txt_keterangan.text.toString()
            val pengkali = "1.5"
            val jenis = "lokal"

            val selectedYear = datePicker.year
            val selectedMonth = datePicker.month
            val selectedDay = datePicker.dayOfMonth

            // Create a Calendar instance and set the selected date
            val calendar = Calendar.getInstance()
            calendar.set(selectedYear, selectedMonth, selectedDay)
            val selectedDate: Date = calendar.time

            if (keterangan.isNotEmpty() && pengkali.isNotEmpty() && jenis.isNotEmpty()) {
                val pengkaliDouble = pengkali.toDoubleOrNull()

                if (pengkaliDouble != null) {
                    val tanggalMerahObj = ClsTanggalMerah(
                        tanggalmerah = selectedDate,
                        keterangan = keterangan,
                        pengkali = pengkaliDouble,
                        jenis = jenis
                    )

                    // Add to Firebase Firestore
                    db.collection("tanggal_merah")
                        .add(tanggalMerahObj)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Tanggal Merah Baru berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Pengkali harus berupa angka", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
