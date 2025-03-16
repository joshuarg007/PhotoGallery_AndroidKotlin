package com.example.photogallery_androidkotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide

class MainActivity : ComponentActivity() {

    private lateinit var imageContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageContainer = findViewById(R.id.imageContainer)

        // Request storage permission when the app starts
        requestStoragePermission()
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadImages()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                requestPhotoPicker()
            } else {
                Toast.makeText(this, "Permission Denied! Grant access to view photos.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun requestStoragePermission() {
        val permission = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> Manifest.permission.READ_MEDIA_IMAGES
            else -> Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            loadImages()
        } else {
            permissionLauncher.launch(permission)
        }
    }

    private fun requestPhotoPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            pickImageLauncher.launch(intent)
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let { addImageToContainer(it) }
        }
    }

    private fun loadImages() {
        val localImages = getImagesFromDevice().take(3) // Load only 3 local images
        val onlineImages = listOf(
            "https://cdn.pixabay.com/photo/2015/12/03/16/21/icon-1075208_1280.png",
            "https://cdn.pixabay.com/photo/2015/11/06/11/47/internet-1026472_1280.jpg",
            "https://cdn.pixabay.com/photo/2015/04/18/07/40/shopping-cart-728407_1280.png"
        )

        if (localImages.isNotEmpty()) {
            addHeader("Local Images")
            for (imagePath in localImages) {
                addImageToContainer(imagePath)
            }
        } else {
            showDefaultImages()
        }

        addHeader("Online Images")
        for (url in onlineImages) {
            addImageToContainer(url)
        }
    }

    private fun showDefaultImages() {
        val defaultImages = listOf(
            R.drawable.default_image1,
            R.drawable.default_image2,
            R.drawable.default_image3
        )

        addHeader("Local Images")
        for (imageRes in defaultImages) {
            addImageToContainer(imageRes)
        }
    }

    private fun addHeader(title: String) {
        val textView = TextView(this).apply {
            text = title
            textSize = 20f
            setPadding(20, 30, 20, 10)
            setTextColor(resources.getColor(android.R.color.black, null))
        }
        imageContainer.addView(textView)
    }

    private fun addImageToContainer(imageSource: Any) {
        val frameLayout = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                550
            ).apply {
                setMargins(20, 20, 20, 20)
            }
            setBackgroundResource(R.drawable.image_border)
        }

        val imageView = ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        Glide.with(this).load(imageSource).into(imageView)
        frameLayout.addView(imageView)
        imageContainer.addView(frameLayout)
    }

    private fun getImagesFromDevice(): List<String> {
        val imagePaths = mutableListOf<String>()
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Images.Media.DATE_ADDED + " DESC"
        )

        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            while (it.moveToNext()) {
                imagePaths.add(it.getString(columnIndex))
            }
        }

        return imagePaths
    }
}
