package com.jhr.abdallahsarayrah.whereismycar

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_login.setOnClickListener {
            carInfo.carno = editText_login_carno.text.toString().toInt()

            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }
}
