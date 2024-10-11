package com.example.proyekkp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.Date

class TanggalMerahInputActivity : AppCompatActivity() {
    private lateinit var txt_tanggalmerah : EditText
    private lateinit var txt_keterangan : EditText
    private lateinit var txt_pengkali : EditText
    private lateinit var txt_jenis : EditText
    private lateinit var btn_tambah : Button
    private lateinit var btn_cektampilan : Button


    lateinit var binding: TanggalMerahInputActivity
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tanggalmerahinput)
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_tanggalmerahinput)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        txt_tanggalmerah = findViewById(R.id.txt_tanggalmerah)
        txt_keterangan = findViewById(R.id.txt_keterangan)
        txt_pengkali = findViewById(R.id.txt_pengkali)
        txt_jenis = findViewById(R.id.txt_jenis)
        btn_tambah = findViewById(R.id.btn_tambah_tanggal)
        btn_cektampilan = findViewById(R.id.btn_ke_tampil_tgl)

        btn_tambah.setOnClickListener {
            val tanggalmerah = txt_tanggalmerah.text.toString()
            val keterangan = txt_keterangan.text.toString()
            val pengkali = txt_pengkali.text.toString()
            val jenis = txt_jenis.text.toString()

            if (tanggalmerah.isNotEmpty() && keterangan.isNotEmpty() && pengkali.isNotEmpty() && jenis.isNotEmpty()) {
                val pengkaliDouble = pengkali.toDoubleOrNull()

                if (pengkaliDouble != null) {
                    val tanggalMerahObj = ClsTanggalMerah(
                        tanggalmerah = tanggalmerah,
                        keterangan = keterangan,
                        pengkali = pengkaliDouble,
                        jenis = jenis
                    )

                    // ditambahkan ke Firebase Firestore
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

        btn_cektampilan.setOnClickListener {
            var nextIntent = Intent(this, Tampilkan_tanggal_merah::class.java)
            startActivity(nextIntent)
        }



    }
}