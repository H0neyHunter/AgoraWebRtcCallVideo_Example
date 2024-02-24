package com.usyssoft.myapplication

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding
import com.usyssoft.myapplication.Utils.media.RtcTokenBuilder2


abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {
    companion object {
        const val AGORA_APP_ID = ""
        const val AGORA_CHANNEL_NAME = "agoraexample"
        const val AGORA_APP_CERTIFICATE = ""
        const val PERMISSION_CODE_VIDEO_CALL = 987
        const val PERMISSION_CODE_VOICE_CALL = 988
    }

    val permissionResultLiveData = MutableLiveData<Boolean>()
    abstract fun initializeBinding(): T
    private lateinit var binding: ViewBinding
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = initializeBinding()
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                println("finish onbackpressed")
                finish()
            }

        })
    }
    fun tokenBuilder(agora_user_uid:Int) : String {
        val tokenBuilder = RtcTokenBuilder2()
        val timestamp = (System.currentTimeMillis() / 1000 + 60).toInt()
        return tokenBuilder.buildTokenWithUid(
            AGORA_APP_ID,
            AGORA_APP_CERTIFICATE,AGORA_CHANNEL_NAME,
            agora_user_uid,
            RtcTokenBuilder2.Role.ROLE_PUBLISHER,
            timestamp,timestamp,
        )

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE_VIDEO_CALL -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionResultLiveData.value = true
                } else {
                    permissionResultLiveData.value = false
                }
            }
            PERMISSION_CODE_VOICE_CALL -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionResultLiveData.value = true
                } else {
                    permissionResultLiveData.value = false
                }
            }
        }
    }
}