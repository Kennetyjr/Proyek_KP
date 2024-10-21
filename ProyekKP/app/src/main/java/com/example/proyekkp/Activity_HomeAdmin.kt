package com.example.proyekkp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.proyekkp.databinding.ActivityHomeAdminBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.Timestamp
import java.util.Calendar

class Activity_HomeAdmin : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityHomeAdminBinding
    lateinit var adapterListPegawaiGaji: AdapterListPegawaiGaji
    var listPegawai: ArrayList<ClsPegawai> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_admin)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        adapterListPegawaiGaji = AdapterListPegawaiGaji(this, listPegawai)
        binding.listviewadmingajian.adapter = adapterListPegawaiGaji

        loadDataPegawai()

        binding.btncaripegawai.setOnClickListener {

        }
    }

    private fun loadDataPegawai() {
        db.collection("data_pegawai")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val role = document.getString("role") ?: ""
                    val status = document.getBoolean("status") ?: false

                    if (role == "pegawai" && status) {
                        val jumlahAbsensiMingguan = document.getLong("jumlah_absensi_mingguan")?.toInt() ?: 0

                        val pegawai = ClsPegawai(
                            id = document.id,
                            id_pegawai = document.getString("idpegawai") ?: "",
                            nama_pegawai = document.getString("namaPegawai") ?: "",
                            password = document.getString("password") ?: "",
                            no_telpon = document.getString("noTelpon")?.toInt() ?: 0,
                            gaji_harian = document.getLong("gajiHarian")?.toInt() ?: 0,
                            jumlah_absensi_mingguan = jumlahAbsensiMingguan,
                            role = role,
                            status = status
                        )
                        listPegawai.add(pegawai)
                    }
                }
                adapterListPegawaiGaji.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal mengambil data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


}
