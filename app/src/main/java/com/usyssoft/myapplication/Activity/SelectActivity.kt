package com.usyssoft.myapplication.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.usyssoft.myapplication.BaseActivity
import com.usyssoft.myapplication.R
import com.usyssoft.myapplication.databinding.ActivitySelectBinding

class SelectActivity : BaseActivity<ActivitySelectBinding>() {
    override fun initializeBinding(): ActivitySelectBinding {
        return ActivitySelectBinding.inflate(layoutInflater)
    }
    private lateinit var b : ActivitySelectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = initializeBinding()
        setContentView(b.root)

        b.apply {

            customAgoraA.setOnClickListener {
                if (editTextPhone.text.toString().toIntOrNull() != null) {
                    val intent = Intent(this@SelectActivity,CustomAgoraActivity::class.java)
                    intent.putExtra("userId",editTextPhone.text.toString().toInt())
                    startActivity(intent)
                }else {
                    Toast.makeText(this@SelectActivity, "userId sadece Int olmalı", Toast.LENGTH_SHORT).show()
                }

            }
            uikitAgoraA.setOnClickListener {
                startActivity(Intent(this@SelectActivity,UikitAgoraActivity::class.java))
            }
            voiceCallBtn.setOnClickListener {
                if (editTextPhone.text.toString().toIntOrNull() != null) {
                    val intent = Intent(this@SelectActivity,VoiceCallAgoraActivity::class.java)
                    intent.putExtra("userId",editTextPhone.text.toString().toInt())
                    startActivity(intent)
                }else {
                    Toast.makeText(this@SelectActivity, "userId sadece Int olmalı", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}