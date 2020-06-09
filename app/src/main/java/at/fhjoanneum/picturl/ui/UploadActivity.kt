package at.fhjoanneum.picturl.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import at.fhjoanneum.picturl.EXTRA_IMAGE_URL
import at.fhjoanneum.picturl.R
import at.fhjoanneum.picturl.db.PictUrlDatabase
import at.fhjoanneum.picturl.model.PictUrlImage
import at.fhjoanneum.picturl.service.UploadService
import kotlinx.coroutines.*

class UploadActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        if (imageUri == null) {
            showError()
        } else {
            findViewById<ImageView>(R.id.uploadImageView).setImageURI(imageUri)
            findViewById<View>(R.id.uploadImageButton).setOnClickListener { uploadImage(imageUri) }
        }
    }

    private fun uploadImage(imageUri: Uri) {
        val imageBinary = this.contentResolver.openInputStream(imageUri)?.buffered()?.use { it.readBytes() }!!
        GlobalScope.launch(Dispatchers.Main) {
            val response = UploadService.upload(imageBinary)
            val image = PictUrlImage.from(response.data!!)
            image.localUri = imageUri
            PictUrlDatabase.getDatabase(this@UploadActivity).imageDao().insert(image)
            val successIntent = Intent(this@UploadActivity, DetailActivity::class.java)
                .putExtra(Intent.EXTRA_STREAM, imageUri)
                .putExtra(EXTRA_IMAGE_URL, image.link)
            startActivity(successIntent)
        }
    }

    private fun showError() {
        TODO("Not yet implemented")
    }
}