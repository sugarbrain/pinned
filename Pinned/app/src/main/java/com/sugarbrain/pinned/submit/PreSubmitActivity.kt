package com.sugarbrain.pinned.submit

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.sugarbrain.pinned.R

class PreSubmitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_submit)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
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
    }
}