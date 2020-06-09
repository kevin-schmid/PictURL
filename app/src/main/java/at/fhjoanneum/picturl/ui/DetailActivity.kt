package at.fhjoanneum.picturl.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import at.fhjoanneum.picturl.EXTRA_IMAGE_URL
import at.fhjoanneum.picturl.R

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)!!
        val imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL)!!
        findViewById<ImageView>(R.id.detailImageView).setImageURI(imageUri)
        findViewById<TextView>(R.id.detailTextView).text = imageUrl
    }
}