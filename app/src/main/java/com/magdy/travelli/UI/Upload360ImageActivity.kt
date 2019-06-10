package com.magdy.travelli.UI

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import com.asha.vrlib.MD360Director
import com.asha.vrlib.MD360DirectorFactory
import com.asha.vrlib.MDVRLibrary
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.magdy.travelli.R
import com.magdy.travelli.helpers.ImageAttachmentManager.SELECT_FILE
import com.magdy.travelli.helpers.StaticMembers
import com.magdy.travelli.helpers.StaticMembers.*
import kotlinx.android.synthetic.main.activity_upload360_image.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList


class Upload360ImageActivity : AppCompatActivity() {

    private val PROGRESS_MAX: Int = 600
    private lateinit var mVRLibrary: MDVRLibrary
    private var currentBitmap: Bitmap? = null
    private lateinit var placesKeys: HashMap<String, String>
    private lateinit var placesNameList: ArrayList<String>
    private lateinit var snackbar: Snackbar
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val TAG = Upload360ImageActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload360_image)
        //Notification manager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        initNotification()
        placesKeys = java.util.HashMap()
        placesNameList = ArrayList()
        snackbar = Snackbar.make(coordinator, R.string.connection_error, Snackbar.LENGTH_INDEFINITE)
        val dataAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, placesNameList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        placesSpinner.adapter = dataAdapter

        FirebaseDatabase.getInstance().reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val placesSnapshot = dataSnapshot.child(StaticMembers.PLACES)
                placesKeys.clear()
                placesNameList.clear()
                for (i in placesSnapshot.children) {
                    val name = i.child(StaticMembers.NAME).getValue(String::class.java)!!
                    placesKeys.put(name, i.key!!)
                    placesNameList.add(name)
                }
                dataAdapter.notifyDataSetChanged()
            }
        })
        mVRLibrary = MDVRLibrary.with(this)
                .displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL)
                .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH)
                .asBitmap { callback ->
                    if (currentBitmap != null) {
                        mVRLibrary.onTextureResize(currentBitmap!!.width.toFloat(), currentBitmap!!.height.toFloat())
                        // texture
                        callback.texture(currentBitmap)
                    }
                }
                .listenTouchPick { hitHotspot, ray -> Log.d(TAG, "Ray:$ray, hitHotspot:$hitHotspot") }
                .pinchEnabled(true)
                .directorFactory(object : MD360DirectorFactory() {
                    override fun createDirector(index: Int): MD360Director {
                        return MD360Director.builder().setPitch(-90f).build()
                    }
                })
                .build(previewImage)

        chooseImage.setOnClickListener {
            galleryIntent()
        }
        uploadImage.setOnClickListener {
            if (currentBitmap != null) {
                showNotificationProgress(PROGRESS_MAX, 0, true)
                val bitmaps = splitBitmap(currentBitmap, 5)
                val ref = FirebaseDatabase.getInstance().getReference(PLACES)
                        .child(placesKeys[placesSpinner.selectedItem.toString()]!!)
                        .child(MEDIA).push()
                ref.setValue(0).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //uploadThumbnail(ref.key!!)
                        FirebaseDatabase.getInstance().getReference(MEDIA).child(ref.key!!).child(TYPE).setValue(0)
                        uploadBitmap(ref.key!!, bitmaps, 0)
                    } else {
                        snackbar.setAction(R.string.retry) {
                            snackbar.dismiss()
                            uploadImage.performClick()
                        }
                        snackbar.show()
                    }
                }
            }
        }
        showSlices.setOnClickListener {
            if (currentBitmap != null) {
                var bitmaps = splitBitmap(currentBitmap, 5)
                var thumbnail = makeThumbnail(currentBitmap)
                thumbnail = makeThumbnailBig(thumbnail, bitmaps[0].height)
                for (i in 0 until bitmaps.size) {
                    thumbnail = compineBitmap(thumbnail, bitmaps[i], when (i) {
                        4 -> thumbnail.width - bitmaps[4].width - 4
                        else -> i * bitmaps[i].width
                    })
                }
                currentBitmap = thumbnail
                mVRLibrary.notifyPlayerChanged()
            }
        }
    }

    private fun uploadThumbnail(key: String) {
        if (currentBitmap != null) {
            val storage = FirebaseStorage.getInstance().getReference(MEDIA).child(key).child(StaticMembers.THUMBNAIL)
            val byteArrayOutputStream = ByteArrayOutputStream()
            val bitmap = makeThumbnail(currentBitmap)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
            storage.putBytes(byteArrayOutputStream.toByteArray())
                    .addOnProgressListener {
                        val p = ((it.bytesTransferred.toFloat() / it.totalByteCount) * 100).toInt()
                        showNotificationProgress(PROGRESS_MAX, p + 500, false)
                    }
                    .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        return@Continuation storage.downloadUrl
                    }).addOnCompleteListener {
                        if (it.isSuccessful) {
                            //// push the link of thumbnail in database
                            FirebaseDatabase.getInstance().getReference(MEDIA).child(key)
                                    .child(THUMBNAIL).setValue(it.result!!.toString())
                            notificationBuilder.setContentText(getString(R.string.upload_finished))
                            showNotificationProgress(0, 0, false)
                        } else {
                            //TODO: Use snack-bar to tell user
                            notificationBuilder.setContentText(getString(R.string.upload_failed))
                            showNotificationProgress(0, 0, false)
                            snackbar.setAction(R.string.retry) {
                                snackbar.dismiss()
                                uploadThumbnail(key)
                            }
                            snackbar.show()
                        }
                    }
        }
    }

    private fun uploadBitmap(key: String, bitmaps: Array<Bitmap>, i: Int) {
        val storage = FirebaseStorage.getInstance().getReference(MEDIA).child(key).child(PARTS).child("$i.png")
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmaps[i].compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        storage.putBytes(byteArrayOutputStream.toByteArray())
                .addOnProgressListener {
                    val p = ((it.bytesTransferred.toFloat() / it.totalByteCount) * 100).toInt()
                    showNotificationProgress(PROGRESS_MAX, p + 100 * i, false)
                }.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation storage.downloadUrl
                }).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //// push the link of this part in database
                        FirebaseDatabase.getInstance().getReference(MEDIA).child(key).child(PARTS)
                                .child("$i").setValue(it.result!!.toString())
                        if (bitmaps.size > i + 1)
                            uploadBitmap(key, bitmaps, i + 1)
                        else {
                            uploadThumbnail(key)
                        }
                    } else {
                        //TODO: Use snack-bar to tell user
                        notificationBuilder.setContentText(getString(R.string.upload_failed))
                        showNotificationProgress(0, 0, false)
                        snackbar.setAction(R.string.retry) {
                            snackbar.dismiss()
                            uploadBitmap(key, bitmaps, i)
                        }
                        snackbar.show()
                    }
                }
    }

    fun initNotification() {
        val intent = Intent(this, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        notificationBuilder = NotificationCompat.Builder(this, getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(getString(R.string.uploading_image))
                .setProgress(PROGRESS_MAX, 0, false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setContentIntent(pendingIntent)
    }

    fun showNotificationProgress(max: Int, progress: Int, indeterminate: Boolean) {
        notificationBuilder.setProgress(max, progress, indeterminate)
        notificationManager.notify(111, notificationBuilder.build())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SELECT_FILE ->
                    onSelectFromGalleryResult(data)
            }
        }
    }

    private fun galleryIntent() {
        //// Send an intent to system to get images from gallery and selecting a file
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE)
    }

    private fun onSelectFromGalleryResult(data: Intent?) {
        //Action after selecting image from Galley
        if (data != null) {
            currentBitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
            val limit = 3000000
            if (currentBitmap!!.byteCount > limit) {
                val out = ByteArrayOutputStream()
                var currSize: Int
                var currQuality = 100
                do {
                    currentBitmap!!.compress(Bitmap.CompressFormat.JPEG, currQuality, out)
                    currSize = out.toByteArray().size
                    // limit quality by 5 percent every time
                    currQuality -= 5

                } while (currSize >= limit && currQuality > 5)

                currentBitmap = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
            }
            mVRLibrary.notifyPlayerChanged()
        }
    }
}
