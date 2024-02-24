package com.usyssoft.myapplication.Activity

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import io.agora.rtc2.Constants;
import io.agora.agorauikit_android.*;
import android.os.Bundle
import com.usyssoft.myapplication.BaseActivity
import com.usyssoft.myapplication.databinding.ActivityUikitAgoraBinding
import io.agora.agorauikit_android.AgoraConnectionData
import io.agora.agorauikit_android.AgoraVideoViewer

class UikitAgoraActivity : BaseActivity<ActivityUikitAgoraBinding>() {
    override fun initializeBinding(): ActivityUikitAgoraBinding {
        return ActivityUikitAgoraBinding.inflate(layoutInflater)
    }

    private lateinit var b: ActivityUikitAgoraBinding
    private var agView: AgoraVideoViewer? = null

    private var AGORA_TOKEN: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = initializeBinding()
        setContentView(b.root)

        AGORA_TOKEN = tokenBuilder(0)
        //AGORA_TOKEN = "007eJxTYGBquPk7rbf23IGwbY/TZYNUNxhdn/5XqJSbxdx76aYbouEKDOZGSZZmJoZpKYmpySZJBhYWSanJRpZpKQaGqSZpyeams5bcTG0IZGQQXrCamZEBAkF8HobE9PyixNSKxNyCnFQGBgB93CNc"

        agView = AgoraVideoViewer(this@UikitAgoraActivity, AgoraConnectionData(AGORA_APP_ID,AGORA_TOKEN!!))
        this@UikitAgoraActivity.addContentView(
            agView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        runOnUiThread {
            agView!!.join(AGORA_CHANNEL_NAME, role = Constants.CLIENT_ROLE_BROADCASTER)

        }

    }
}