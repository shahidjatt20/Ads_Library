package com.integrated.ads_library

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.integrated.mylibrary.Admob_Helper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Admob_Helper.loadAdaptiveBannerAd(this,getString(R.string.banner_id))
        val button:Button=findViewById(R.id.button)
        button.setOnClickListener{
            val intent=Intent(this,MainActivity2::class.java)
            Admob_Helper.showAdmobInterstitial(this,getString(R.string.interstitial_id),intent)
        }
    }
}