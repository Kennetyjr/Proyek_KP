package com.example.proyekkp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.proyekkp.databinding.ActivityHomePegawaiBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Activity_Home_Pegawai : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityHomePegawaiBinding

    var idPegawai: String? = null
    var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_home_pegawai)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_pegawai)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        binding.btninputplot.setOnClickListener {
            val nextIntent = Intent(this, Activity_PlottingMesin::class.java)
            startActivity(nextIntent)
        }

        binding.btnlihatlistplotting.setOnClickListener {
            val nextIntent = Intent(this, Activity_HalamanListPlotting::class.java)
            startActivity(nextIntent)
        }

        binding.btnkepenjualan.setOnClickListener {
            val nextIntent = Intent(this, Activity_Penjualan::class.java)
            startActivity(nextIntent)
        }


        idPegawai = intent.getStringExtra("ID_PEGAWAI")
        password = intent.getStringExtra("PASSWORD")

        binding.btnprofilepegawai.setOnClickListener {
            val nextIntent = Intent(this, Activity_ProfilePegawai::class.java)
            // Kirimkan idPegawai ke Activity_ProfilePegawai
            nextIntent.putExtra("ID_PEGAWAI", idPegawai)
            nextIntent.putExtra("PASSWORD", password)
            startActivity(nextIntent)
        }

    }
}