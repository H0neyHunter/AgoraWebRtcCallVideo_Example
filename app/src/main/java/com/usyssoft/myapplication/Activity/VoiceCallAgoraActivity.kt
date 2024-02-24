package com.usyssoft.myapplication.Activity

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.usyssoft.myapplication.BaseActivity
import com.usyssoft.myapplication.R
import com.usyssoft.myapplication.databinding.ActivityVoiceCallAgoraBinding
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

class VoiceCallAgoraActivity : BaseActivity<ActivityVoiceCallAgoraBinding>() {
    override fun initializeBinding(): ActivityVoiceCallAgoraBinding {
        return ActivityVoiceCallAgoraBinding.inflate(layoutInflater)
    }

    private var AGORA_TOKEN: String? = null
    private var AGORA_UID = 0

    private var isJoin = false
    private var agoraEngine: RtcEngine? = null


    private val REQ_PERM = arrayOf(Manifest.permission.RECORD_AUDIO)

    private fun checkPermission(): Boolean {
        return !(ContextCompat.checkSelfPermission(
            this@VoiceCallAgoraActivity,
            REQ_PERM[0]
        ) != PackageManager.PERMISSION_GRANTED)
    }
    private var voiceHideStatus = 0
    private lateinit var b : ActivityVoiceCallAgoraBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = initializeBinding()
        setContentView(b.root)

        AGORA_UID = intent.getIntExtra("userId",0)

        AGORA_TOKEN = "007eJxTYBDekD8vJFZAtPZj3e/Sj6s+KAQ4+jUd3H7iXKHeu/Lf79wUGMyNkizNTAzTUhJTk02SDCwsklKTjSzTUgwMU03Sks1NWY7dTG0IZGS4tGoaAyMUgvg8DInp+UWJqRWJuQU5qQwMABKOJao="


        b.apply {
            vcJoinCall.setOnClickListener {
                joinCallFunc()
            }
            vcLeaveCall.setOnClickListener {
                leaveCallFunc()
            }
            backV.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            voiceVideoHideOpen()
            vcVoiceHideBtn.setOnClickListener {
                if (agoraEngine != null) {
                    when(voiceHideStatus) {
                        0 -> {
                            agoraEngine?.muteLocalAudioStream(true)
                            voiceHideStatus = 1
                        }
                        1 -> {
                            agoraEngine?.muteLocalAudioStream(false)
                            voiceHideStatus = 0
                        }
                    }
                    voiceVideoHideOpen()
                }
            }
            setupRTCEngine()

            permissionResultLiveData.observe(this@VoiceCallAgoraActivity) { isPermissionGranted ->
                if (isPermissionGranted) {
                    joinCallFunc()
                } else {
                    //Error Permission Message
                }
            }
        }
    }
    private fun voiceVideoHideOpen() {
        b.apply {
            when(voiceHideStatus) {
                0 -> {
                    vcVoiceHideBtn.text = "VoiceHide"
                }
                1 -> {
                    vcVoiceHideBtn.text = "VoiceOpen"
                }
            }
        }

    }
    private fun tokenBuilder() {
        if (AGORA_TOKEN == null) {
            AGORA_TOKEN = tokenBuilder(AGORA_UID)
        }

    }
    private fun joinCallFunc() {
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this, REQ_PERM, PERMISSION_CODE_VOICE_CALL)
        } else {
            val option = ChannelMediaOptions()
            option.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            option.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER

            agoraEngine?.joinChannel(AGORA_TOKEN, AGORA_CHANNEL_NAME, AGORA_UID, option)
        }

    }
    private fun leaveCallFunc() {
        if (!isJoin) {
            println("Join a channel first")
        } else {
            agoraEngine?.leaveChannel()
            isJoin = false
        }

    }
    private val mRtcHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            println("Remote User Joined $uid")

        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            super.onJoinChannelSuccess(channel, uid, elapsed)
            isJoin = true
            println("Joined Channel $channel")
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            super.onUserOffline(uid, reason)
            println("Offline User")
            /*
            runOnUiThread {
            }*/
        }
    }

    private fun setupRTCEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = AGORA_APP_ID
            config.mEventHandler = mRtcHandler
            agoraEngine = RtcEngine.create(config)
            agoraEngine?.enableAudio()
            println("setupRtcEngine Success:")
        } catch (e: Exception) {
            println("setupRtcEngine Error: ${e.localizedMessage}")
        }


    }
    override fun onDestroy() {
        super.onDestroy()
        agoraEngine?.stopAudioRecording()
        agoraEngine?.leaveChannel()
        Thread {
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }


}