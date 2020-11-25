package com.pixlee.pixleeandroidsdk.ui.live

import android.Manifest
import android.app.ProgressDialog.show
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pixlee.pixleeandroidsdk.R
import com.pixlee.pixleesdk.client.PXLKtxAlbum
import com.pixlee.pixleesdk.ui.viewholder.PhotoWithVideoInfo
import com.pixlee.pixleesdk.util.PXLViewUtil
import com.takusemba.rtmppublisher.Publisher
import com.takusemba.rtmppublisher.PublisherListener
import kotlinx.android.synthetic.main.activity_live_camera.*
import kotlinx.coroutines.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

/**
 * Created by sungjun on 9/11/20.
 */
/**
 * This shows how to play the video and its product list
 */
class LiveCameraActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {
    private var publisher: Publisher? = null
    private val handler = Handler()
    private var thread: Thread? = null
    private var isCounting = false
    var item: PhotoWithVideoInfo? = null

    var pxlKtxAlbum: PXLKtxAlbum? = null
    fun postLive(isLive: Boolean) {
        GlobalScope.launch {
            item?.pxlPhoto?.also {
                if (pxlKtxAlbum == null) {
                    pxlKtxAlbum = PXLKtxAlbum(this@LiveCameraActivity)
                }
                pxlKtxAlbum?.postLives(it, isLive)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        postLive(false)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_camera)

        // set a full screen mode
        PXLViewUtil.expandContentAreaOverStatusBar(this)

        headerView.setPadding(0, PXLViewUtil.getStatusBarHeight(this), 0, 0)


        val i = intent
        if (i == null) {
            finish()
            return
        }
        val item: PhotoWithVideoInfo? = i.getParcelableExtra("photoWithVideoInfo")
        // if the photo is null, close this image view
        if (item == null) {
            finish()
            return
        }

        this.item = item

        if (hasCameraAndAudioPermissions()) {
            startLiveCamera()
        } else {
            cameraAndAudioTask()
        }


        publishButton.setOnClickListener {
            publisher?.also {
                if (it.isPublishing) {
                    it.stopPublishing()
                } else {
                    it.startPublishing()
                }
            }

        }

        cameraButton.setOnClickListener {
            publisher?.switchCamera()
        }
    }

    fun startLiveCamera() {
        item?.also {
            val url = "rtmp://175.195.207.155:1935/live/${it.pxlPhoto.albumPhotoId}"
            Log.e("livecamera", "url: $url")
            publisher = Publisher.Builder(this)
                    .setGlView(glView)
                    .setUrl(url)
                    .setSize(Publisher.Builder.DEFAULT_WIDTH, Publisher.Builder.DEFAULT_HEIGHT)
                    .setAudioBitrate(Publisher.Builder.DEFAULT_AUDIO_BITRATE)
                    .setVideoBitrate(Publisher.Builder.DEFAULT_VIDEO_BITRATE)
                    .setCameraMode(Publisher.Builder.DEFAULT_MODE)
                    .setListener(object : PublisherListener {
                        override fun onStarted() {
                            Log.e("livecamera", "=== onStarted ===")
                            postLive(true)
                            Toast.makeText(this@LiveCameraActivity, R.string.started_publishing, Toast.LENGTH_SHORT)
                                    .apply { setGravity(Gravity.CENTER, 0, 0) }
                                    .run { show() }
                            updateControls()
                            startCounting()
                        }

                        override fun onStopped() {
                            Log.e("livecamera", "=== onStopped ===")
                            Toast.makeText(this@LiveCameraActivity, R.string.stopped_publishing, Toast.LENGTH_SHORT)
                                    .apply { setGravity(Gravity.CENTER, 0, 0) }
                                    .run { show() }
                            updateControls()
                            stopCounting()
                        }

                        override fun onDisconnected() {
                            Log.e("livecamera", "=== onDisconnected ===")
                            postLive(false)
                            Toast.makeText(this@LiveCameraActivity, R.string.disconnected_publishing, Toast.LENGTH_SHORT)
                                    .apply { setGravity(Gravity.CENTER, 0, 0) }
                                    .run { show() }
                            updateControls()
                            stopCounting()
                        }

                        override fun onFailedToConnect() {
                            Log.e("livecamera", "=== onFailedToConnect ===")
                            postLive(false)
                            Toast.makeText(this@LiveCameraActivity, R.string.failed_publishing, Toast.LENGTH_SHORT)
                                    .apply { setGravity(Gravity.CENTER, 0, 0) }
                                    .run { show() }
                            updateControls()
                            stopCounting()
                        }
                    })
                    .build()
        }

    }

    override fun onResume() {
        super.onResume()
        updateControls()
    }


    private fun updateControls() {
        publisher?.also {
            publishButton.text = getString(if (it.isPublishing) R.string.stop_publishing else R.string.start_publishing)
        }

    }

    private fun startCounting() {
        isCounting = true
        label.text = getString(R.string.publishing_label, 0L.format(), 0L.format())
        label.visibility = View.VISIBLE
        val startedAt = System.currentTimeMillis()
        var updatedAt = System.currentTimeMillis()
        thread = Thread {
            while (isCounting) {
                if (System.currentTimeMillis() - updatedAt > 1000) {
                    updatedAt = System.currentTimeMillis()
                    handler.post {
                        val diff = System.currentTimeMillis() - startedAt
                        val second = diff / 1000 % 60
                        val min = diff / 1000 / 60
                        label.text = getString(R.string.publishing_label, min.format(), second.format())
                    }
                }
            }
        }
        thread?.start()
    }

    private fun stopCounting() {
        isCounting = false
        label.text = ""
        label.visibility = View.GONE
        thread?.interrupt()
    }

    private fun Long.format(): String {
        return String.format("%02d", this)
    }

    companion object {
        const val RC_CAMERA_AUDIO_PERM = 124

        // start video view with a photo data
        fun launch(context: Context, pxlPhoto: PhotoWithVideoInfo?) {
            val i = Intent(context, LiveCameraActivity::class.java)
            i.putExtra("photoWithVideoInfo", pxlPhoto)
            context.startActivity(i)
        }
    }


    //private val LOCATION_AND_CONTACTS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

    @AfterPermissionGranted(RC_CAMERA_AUDIO_PERM)
    fun cameraAndAudioTask() {
        if (hasCameraAndAudioPermissions()) {
            // Have permissions, do the thing!
            LiveCameraActivity.launch(this, item)
            Toast.makeText(this, "TODO: camera and audio things", Toast.LENGTH_LONG).show()
            finish()
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_location_contacts),
                    RC_CAMERA_AUDIO_PERM,
                    Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun hasCameraAndAudioPermissions(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String?>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)
    }

    private fun hasRecordAudioPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.RECORD_AUDIO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            val yes = getString(R.string.yes)
            val no = getString(R.string.no)

            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(
                    this,
                    getString(R.string.returned_from_app_settings_to_activity,
                            if (hasCameraPermission()) yes else no,
                            if (hasRecordAudioPermission()) yes else no),
                    Toast.LENGTH_LONG)
                    .show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d("livecamera", "onPermissionsGranted:" + requestCode + ":" + perms.size)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d("livecamera", "onPermissionsDenied:" + requestCode + ":" + perms.size)

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {
        Log.d("livecamera", "onRationaleAccepted:$requestCode")
    }

    override fun onRationaleDenied(requestCode: Int) {
        Log.d("livecamera", "onRationaleDenied:$requestCode")
    }
}
