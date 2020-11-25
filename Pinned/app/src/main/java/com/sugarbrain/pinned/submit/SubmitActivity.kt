package com.sugarbrain.pinned.submit

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sugarbrain.pinned.R
import kotlinx.android.synthetic.main.activity_submit.*

class SubmitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit)
        setSubmitImage()
    }

    private fun setSubmitImage() {
        ivSubmitImage.setImageBitmap(intent.extras?.get(SUBMIT_IMAGE_KEY) as Bitmap?)
    }

    companion object {
        val SUBMIT_IMAGE_KEY = "SUBMIT_IMAGE"
    }
}