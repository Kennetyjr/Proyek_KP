package com.example.proyekkp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class Tampilkan_tanggal_merah : AppCompatActivity() {

    private lateinit var text1 : TextView
    private lateinit var text2 : TextView
    private lateinit var text3 : TextView
    private lateinit var text4 : TextView

    lateinit var binding: Tampilkan_tanggal_merah
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tampilkan_tanggal_merah)
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_tampilkan_tanggal_merah)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        text1 = findViewById(R.id.textView_tanggal)
        text2 = findViewById(R.id.textView_keterangan)
        text3 = findViewById(R.id.textView_pengkali)
        text4 = findViewById(R.id.textView_jenis)

        refreshdatatanggaltampil()
    }

    private fun refreshdatatanggaltampil() {
        db.collection("tanggal_merah")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val tanggalmerah = document.getString("tanggalmerah")
                    val keterangan = document.getString("keterangan")
                    val pengkali = document.getDouble("pengkali")
                    val jenis = document.getString("jenis")

                    if (tanggalmerah != null && keterangan != null && pengkali != null && jenis != null) {
                        text1.text = tanggalmerah
                        text2.text = keterangan
                        text3.text = pengkali.toString()
                        text4.text = jenis
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}