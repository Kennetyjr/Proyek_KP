package com.example.proyekkp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.proyekkp.databinding.ActivityHomeUtamaAdminBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Activity_HomeUtamaAdmin : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityHomeUtamaAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_home_utama_admin)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_utama_admin)

        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        binding.btnkeplotting.setOnClickListener {
            val nextIntent = Intent(this, Activity_PlottingMesin::class.java)
            startActivity(nextIntent)
        }

        binding.btnkeregister.setOnClickListener {
            val nextIntent = Intent(this, Activity_Register_Pegawai::class.java)
            startActivity(nextIntent)
        }

        binding.btnmasuktanggalmerah.setOnClickListener {
            val nextIntent = Intent(this, TanggalMerahInputActivity::class.java)
            startActivity(nextIntent)
        }

        binding.btnregisterproduk.setOnClickListener {
            val nextIntent = Intent(this, Activity_RegisterProduk::class.java)
            startActivity(nextIntent)
        }

        binding.btnregistermesin.setOnClickListener {
            val nextIntent = Intent(this, Activity_RegisterMesin::class.java)
            startActivity(nextIntent)
        }

        binding.btnlistplotting.setOnClickListener {
            val nextIntent = Intent(this, Activity_HalamanListPlotting::class.java)
            startActivity(nextIntent)
        }

        binding.btnkegaji.setOnClickListener {
            val nextIntent = Intent(this, Activity_HomeAdmin::class.java)
            startActivity(nextIntent)
        }

        binding.btnkehistorypenjualan.setOnClickListener {
            val nextIntent = Intent(this, Activity_HistoryPenjualan::class.java)
            startActivity(nextIntent)
        }

        binding.btnadminkepenjualan.setOnClickListener {
            val nextIntent = Intent(this, Activity_Penjualan::class.java)
            startActivity(nextIntent)
        }

        binding.btnkeeditpegawai.setOnClickListener {
            val nextIntent = Intent(this, Activity_EditPegawai::class.java)
            startActivity(nextIntent)
        }

        binding.btnkeeditproduk.setOnClickListener {
            val nextIntent = Intent(this, Activity_EditProduk::class.java)
            startActivity(nextIntent)
        }

        binding.btnkeeditmesin.setOnClickListener {
            val nextIntent = Intent(this, Activity_EditMesin::class.java)
            startActivity(nextIntent)
        }

        binding.btnkeadminchane.setOnClickListener {
            val nextIntent = Intent(this, Activity_AdminPasswordChange::class.java)
            startActivity(nextIntent)
        }

    }
}