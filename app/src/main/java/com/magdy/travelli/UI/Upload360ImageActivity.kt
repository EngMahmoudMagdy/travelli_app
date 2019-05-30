package com.magdy.travelli.UI

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import com.asha.vrlib.MD360Director
import com.asha.vrlib.MD360DirectorFactory
import com.asha.vrlib.MDVRLibrary
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.magdy.travelli.R
import com.magdy.travelli.helpers.ImageAttachmentManager.SELECT_FILE
import com.magdy.travelli.helpers.StaticMembers
import kotlinx.android.synthetic.main.activity_upload360_image.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Upload360ImageActivity : AppCompatActivity() {

    private lateinit var mVRLibrary: MDVRLibrary
    private lateinit var currentBitmap: Bitmap
    private lateinit var placesKeys: HashMap<String, String>
    private lateinit var placesNameList: ArrayList<String>

    private val TAG = Upload360ImageActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload360_image)
        placesKeys = java.util.HashMap()
        placesNameList = ArrayList()
        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, placesNameList)
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // attaching data adapter to spinner
        placesSpinner.setAdapter(dataAdapter)

        FirebaseDatabase.getInstance().reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val placesSnapshot = dataSnapshot.child(StaticMembers.PLACES)
                placesKeys.clear()
                placesNameList.clear()
                for (i in placesSnapshot.children) {
                    val name = i.child(StaticMembers.NAME).getValue(String::class.java)!!
                    placesKeys.put(i.key!!, name)
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
                        mVRLibrary.onTextureResize(currentBitmap.width.toFloat(), currentBitmap.height.toFloat())
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
            val bitmaps = StaticMembers.splitBitmap(currentBitmap, 5)
        }
    }
    fun uploadBitmap(key:String, bitmap: Bitmap)
    {
        
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
            mVRLibrary.notifyPlayerChanged()
        }
    }


    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, createImagePath(), null)
        return Uri.parse(path)
    }

    private fun createImagePath(): String {
        //create image with date name
        val timeStamp = SimpleDateFormat("yyyy_MM_dd_hh_mm_a", Locale.US).format(System.currentTimeMillis())
        return "JPEG_" + timeStamp + "_"
    }
}
