package com.sugarbrain.pinned.submit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.sugarbrain.pinned.R
import kotlinx.android.synthetic.main.activity_pre_submit.*

class PreSubmitActivity : AppCompatActivity() {
    private lateinit var placesClient: PlacesClient
    private var currentPlace: Place? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_submit)

        Places.initialize(this, "AIzaSyBTvsa8eN9hEzwfeFHdXE2xt-G2oeJVxgk")
        placesClient = Places.createClient(this)

        setupLocationAccess()
        setupCameraAccess()
    }

    private fun setupLocationAccess() {
        val location = Manifest.permission.ACCESS_FINE_LOCATION;

        if (ContextCompat.checkSelfPermission(this, location) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "has location permission")
            getNearestPlace()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getNearestPlace()
                } else {
                    Toast.makeText(
                        this, "A localização do dispositivo é necessária", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getNearestPlace() {
        Log.i(TAG, "getNearestPlace")

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            val placeFields: List<Place.Field> = listOf(
                Place.Field.NAME,
                Place.Field.PHOTO_METADATAS,
                Place.Field.ADDRESS
            )
            val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)
            val placeResponse = placesClient.findCurrentPlace(request)

            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentPlace = task.result?.placeLikelihoods?.first()?.place
                    updateLayout()
                } else {
                    val exception = task.exception
                    if (exception is ApiException) {
                        Log.e(TAG, "Place not found: ${exception.statusCode}")
                    }
                }
            }
        }
    }

    private fun updateLayout() {
        tvPlaceName.text = currentPlace?.name
        tvAddress.text = currentPlace?.address
        val metada = currentPlace?.photoMetadatas
        if (metada == null || metada.isEmpty()) {
            Log.w(TAG, "No photo metadata.")
            return
        }

        val photoMetadata = metada.first()

        val photoRequest = FetchPhotoRequest.builder(photoMetadata)
            .setMaxWidth(200) // Optional.
            .setMaxHeight(200) // Optional.
            .build()

        placesClient.fetchPhoto(photoRequest)
            .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                val bitmap = fetchPhotoResponse.bitmap
                placeImage.setImageBitmap(bitmap)
            }.addOnFailureListener { exception: Exception ->
                if (exception is ApiException) {
                    Log.e(TAG, "Place not found: " + exception.message)
                }
            }
    }

    private fun setupCameraAccess() {
        cameraButton.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (cameraIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CAPTURE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val image = data?.extras?.get("data") as Bitmap
                Log.i(TAG, "image captured -> $image")
                val submitIntent = Intent(this, SubmitActivity::class.java);
                submitIntent.putExtra(SubmitActivity.SUBMIT_IMAGE_KEY, image)
                startActivity(submitIntent)
            } else {
                Toast.makeText(this, "It was not possible to capture the photo", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    companion object {
        var IMAGE_CAPTURE_CODE = 666
        var TAG = "PreSubmitActivity"
        var LOCATION_REQUEST_CODE = 123
    }
}