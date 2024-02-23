package com.usyssoft.myapplication

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding


abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {
    companion object {
        const val PERMISSION_CODE_VIDEO_CALL = 987
    }

    val permissionResultLiveData = MutableLiveData<Boolean>()
    abstract fun initializeBinding(): T
    private lateinit var binding: ViewBinding
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = initializeBinding()
        setContentView(binding.root)
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
        }
    }
}