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

    // Register permission request callback
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadImagesFromStorage()
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
            loadImagesFromStorage()
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
            uri?.let { loadSelectedImage(it) }
        }
    }

    private fun loadSelectedImage(imageUri: Uri) {
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

        Glide.with(this).load(imageUri).into(imageView)
        frameLayout.addView(imageView)
        imageContainer.addView(frameLayout)
    }

    private fun loadImagesFromStorage() {
        val images = getImagesFromDevice()

        if (images.isEmpty()) {
            showDefaultImages()
            Toast.makeText(this, "No images found, loading default images.", Toast.LENGTH_LONG).show()
            return
        }

        for (imagePath in images) {
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

            Glide.with(this).load(imagePath).into(imageView)
            frameLayout.addView(imageView)
            imageContainer.addView(frameLayout)
        }
    }

    private fun showDefaultImages() {
        val defaultImages = listOf(
            R.drawable.default_image1,
            R.drawable.default_image2,
            R.drawable.default_image3
        )

        for (imageRes in defaultImages) {
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

            Glide.with(this).load(imageRes).into(imageView)
            frameLayout.addView(imageView)
            imageContainer.addView(frameLayout)
        }
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
