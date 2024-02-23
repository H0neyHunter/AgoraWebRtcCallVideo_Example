package com.usyssoft.myapplication.Activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceView
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.usyssoft.myapplication.BaseActivity
import com.usyssoft.myapplication.Utils.media.RtcTokenBuilder2
import com.usyssoft.myapplication.databinding.ActivityMainBinding
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun initializeBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var b: ActivityMainBinding


    private val AGORA_APP_ID = ""
    private val AGORA_CHANNEL_NAME = "agoraexample"
    private var AGORA_TOKEN: String? = null
    private val AGORA_APP_CERTIFICATE = ""
    private val AGORA_UID = 0

    private var isJoin = false
    private var agoraEngine: RtcEngine? = null
    private var localSurface: SurfaceView? = null
    private var remoteSurface: SurfaceView? = null


    private val REQ_PERM = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
    private fun checkPermission(): Boolean {
        return !(ContextCompat.checkSelfPermission(
            this@MainActivity,
            REQ_PERM[0]
        ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this@MainActivity,
            REQ_PERM[1]
        ) != PackageManager.PERMISSION_GRANTED)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = initializeBinding()
        //b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        tokenBuilder()


        b.apply {
            leaveCall.setOnClickListener {
                leaveCallFunc()
            }
            joinCall.setOnClickListener {
                joinCallFunc()
            }
        }

        setupRTCEngine()

        permissionResultLiveData.observe(this) { isPermissionGranted ->
            if (isPermissionGranted) {
                joinCallFunc()
            } else {
                //Error Permission Message
            }
        }


    }

    private fun tokenBuilder() {
        val tokenBuilder = RtcTokenBuilder2()
        val timestamp = (System.currentTimeMillis() / 1000 + 60).toInt()
        AGORA_TOKEN = tokenBuilder.buildTokenWithUid(
            AGORA_APP_ID,AGORA_CHANNEL_NAME,
            AGORA_APP_CERTIFICATE,
            AGORA_UID,
            RtcTokenBuilder2.Role.ROLE_PUBLISHER,
            timestamp,timestamp,
        )

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
            agoraEngine!!.startPreview()
            agoraEngine!!.joinChannel(AGORA_TOKEN, AGORA_CHANNEL_NAME, AGORA_UID, option)
        }

    }

    private fun leaveCallFunc() {
        if (!isJoin) {
            println("Join a channel first")
        } else {
            agoraEngine!!.leaveChannel()
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
            agoraEngine!!.enableVideo()
            println("setupRtcEngine Success:")
        } catch (e: Exception) {
            println("setupRtcEngine Error:")
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine!!.stopPreview()
        agoraEngine!!.leaveChannel()
        Thread {
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }

    private fun setupRemoteVideo(uid: Int) {
        remoteSurface = SurfaceView(baseContext)
        remoteSurface!!.setZOrderMediaOverlay(true)
        b.remoteUser.addView(remoteSurface)

        //VideoCanvas.RENDER_MODE_FIT,
        agoraEngine!!.setupRemoteVideo(VideoCanvas(remoteSurface, VideoCanvas.RENDER_MODE_FIT, uid))

    }

    private fun setupLocalVideo() {
        localSurface = SurfaceView(baseContext)
        localSurface!!.setZOrderMediaOverlay(true)
        b.localUser.addView(localSurface)

        agoraEngine!!.setupLocalVideo(
            VideoCanvas(
                localSurface,
                VideoCanvas.RENDER_MODE_FIT,
                AGORA_UID
            )
        )

    }
}
