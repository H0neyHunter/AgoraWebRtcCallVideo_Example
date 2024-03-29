package com.usyssoft.myapplication.Activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceView
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.usyssoft.myapplication.BaseActivity
import com.usyssoft.myapplication.databinding.ActivityCustomagoraBinding
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

class CustomAgoraActivity : BaseActivity<ActivityCustomagoraBinding>() {

    override fun initializeBinding(): ActivityCustomagoraBinding {
        return ActivityCustomagoraBinding.inflate(layoutInflater)
    }

    private lateinit var b: ActivityCustomagoraBinding


    private var AGORA_TOKEN: String? = null
    private var AGORA_UID = 0

    private var isJoin = false
    private var agoraEngine: RtcEngine? = null
    private var localSurface: SurfaceView? = null
    private var remoteSurface: SurfaceView? = null


    private val REQ_PERM = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
    private fun checkPermission(): Boolean {
        return !(ContextCompat.checkSelfPermission(
            this@CustomAgoraActivity,
            REQ_PERM[0]
        ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this@CustomAgoraActivity,
            REQ_PERM[1]
        ) != PackageManager.PERMISSION_GRANTED)
    }

    private var videoTransActionChangeFrameStatus = 0
    private var videoHideStatus = 0
    private var voiceHideStatus = 0

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = initializeBinding()
        //b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        AGORA_UID = intent.getIntExtra("userId",0)


        AGORA_TOKEN = "007eJxTYHAUV3TdIMdSd0ZQ7Ypq4txwd9v1t61jimyjl/ZoMZlvVFZgMDdKsjQzMUxLSUxNNkkysLBISk02skxLMTBMNUlLNjc9IHc7tSGQkeFkXBMjIwMEgvg8DInp+UWJqRWJuQU5qQwMAAjoH38="

        //tokenBuilder()



        b.apply {
            leaveCall.setOnClickListener {
                leaveCallFunc()
            }
            joinCall.setOnClickListener {
                joinCallFunc()
            }
            backBtn.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            localUser.setOnClickListener {
                try {
                    if (videoTransActionChangeFrameStatus == 0) {
                        if (remoteSurface != null && localSurface != null) {
                            remoteUser.removeAllViews()
                            localUser.removeAllViews()
                            remoteSurface?.setZOrderMediaOverlay(true)
                            localSurface?.setZOrderMediaOverlay(false)
                            remoteUser.addView(localSurface)
                            localUser.addView(remoteSurface)
                            videoTransActionChangeFrameStatus = 101
                        }

                    }else {
                        remoteUser.removeAllViews()
                        localUser.removeAllViews()
                        remoteSurface?.setZOrderMediaOverlay(false)
                        localSurface?.setZOrderMediaOverlay(true)
                        remoteUser.addView(remoteSurface)
                        localUser.addView(localSurface)
                        videoTransActionChangeFrameStatus = 0
                    }
                }catch (e:Exception) {
                    println("0localuser ${e.localizedMessage}")
                }

            }



            voiceVideoHideOpen()
            voiceHide.setOnClickListener {
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
            videoHide.setOnClickListener {
                if (agoraEngine != null) {
                    when(videoHideStatus) {
                        0 -> {
                            agoraEngine?.muteLocalVideoStream(true)
                            videoHideStatus = 1
                        }
                        1 -> {
                            agoraEngine?.muteLocalVideoStream(false)
                            videoHideStatus = 0
                        }
                    }
                    voiceVideoHideOpen()
                }

            }
        }

        setupRTCEngine()

        permissionResultLiveData.observe(this@CustomAgoraActivity) { isPermissionGranted ->
            if (isPermissionGranted) {
                joinCallFunc()
            } else {
                //Error Permission Message
            }
        }

        /*
        agoraEngine?.muteLocalVideoStream(false)
        agoraEngine?.muteLocalAudioStream(false)*/


    }

    private fun voiceVideoHideOpen() {
        b.apply {
            when(voiceHideStatus) {
                0 -> {
                    voiceHide.text = "VoiceHide"
                }
                1 -> {
                    voiceHide.text = "VoiceOpen"
                }
            }
            when(videoHideStatus) {
                0 -> {
                    videoHide.text = "VideoHide"
                }
                1 -> {
                    videoHide.text = "VideoOpen"
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
            ActivityCompat.requestPermissions(this, REQ_PERM, PERMISSION_CODE_VIDEO_CALL)
        } else {
            val option = ChannelMediaOptions()
            option.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            option.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            setupLocalVideo()
            localSurface!!.visibility = VISIBLE

            //Video
            agoraEngine?.startPreview()
            //Video

            agoraEngine?.joinChannel(AGORA_TOKEN, AGORA_CHANNEL_NAME, AGORA_UID, option)
        }

    }

    private fun leaveCallFunc() {
        if (!isJoin) {
            println("Join a channel first")
        } else {
            agoraEngine?.leaveChannel()
            println("Yout left the channel")
            if (remoteSurface != null) remoteSurface!!.visibility = GONE
            if (localSurface != null) localSurface!!.visibility = GONE
            isJoin = false
        }

    }

    private val mRtcHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            println("Remote User Joined $uid")
            runOnUiThread {
                setupRemoteVideo(uid)
            }
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            super.onJoinChannelSuccess(channel, uid, elapsed)
            isJoin = true
            println("Joined Channel $channel")
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            super.onUserOffline(uid, reason)
            println("Offline User")
            runOnUiThread {
                remoteSurface!!.visibility = GONE
            }
        }
    }

    private fun setupRTCEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = AGORA_APP_ID
            config.mEventHandler = mRtcHandler
            agoraEngine = RtcEngine.create(config)
            agoraEngine?.enableVideo()
            println("setupRtcEngine Success:")
        } catch (e: Exception) {
            println("setupRtcEngine Error:")
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine?.stopPreview()
        agoraEngine?.leaveChannel()
        Thread {
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }

    private fun setupRemoteVideo(uid: Int) {
        remoteSurface = SurfaceView(baseContext)
        remoteSurface!!.setZOrderMediaOverlay(false)
        b.remoteUser.addView(remoteSurface)


        //VideoCanvas.RENDER_MODE_FIT,
        agoraEngine?.setupRemoteVideo(VideoCanvas(remoteSurface, VideoCanvas.RENDER_MODE_FIT, uid))

    }

    private fun setupLocalVideo() {
        localSurface = SurfaceView(baseContext)
        localSurface!!.setZOrderMediaOverlay(true)
        b.localUser.addView(localSurface)

        agoraEngine?.setupLocalVideo(
            VideoCanvas(
                localSurface,
                VideoCanvas.RENDER_MODE_FIT,
                AGORA_UID
            )
        )

    }
}
