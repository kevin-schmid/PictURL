package at.fhjoanneum.picturl.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import at.fhjoanneum.picturl.R
import at.fhjoanneum.picturl.db.PictUrlDatabase
import at.fhjoanneum.picturl.model.PictUrlImage
import at.fhjoanneum.picturl.service.UploadDto
import at.fhjoanneum.picturl.service.UploadService
import kotlinx.coroutines.*

class UploadActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    var localImageUri: Uri = Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        if (imageUri == null) {
            showError()
        } else {
            val imageBinary =
                this.contentResolver.openInputStream(imageUri)?.buffered()?.use { it.readBytes() }!!
            localImageUri = createThumbnail(imageBinary)
            findViewById<ImageView>(R.id.uploadImageView).setImageURI(localImageUri)
            findViewById<View>(R.id.uploadImageButton).setOnClickListener {
                if(findViewById<EditText>(R.id.uploadEditText).text.toString().isEmpty()){
                    Toast.makeText(applicationContext,"Enter Title", Toast.LENGTH_SHORT).show()
                }
                else{
                    uploadImage(imageUri)
                }
            }
        }
    }

    private fun uploadImage(imageUri: Uri) {
        findViewById<View>(R.id.uploadMainLayout).visibility = View.GONE
        findViewById<View>(R.id.uploadProgressBar).visibility = View.VISIBLE

        val imageBinary =
            this.contentResolver.openInputStream(imageUri)?.buffered()?.use { it.readBytes() }!!
        val imageTitle = findViewById<EditText>(R.id.uploadEditText).text.toString()
        GlobalScope.launch(Dispatchers.Main) {
            val response = UploadService.upload(UploadDto().apply {
                title = imageTitle
                image = imageBinary
            })
            val pictUrlImage = PictUrlImage.from(response.data!!).apply {
                this.localUri = localImageUri
            }
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("link", pictUrlImage.link)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(applicationContext,"Copied to Clipboard", Toast.LENGTH_SHORT).show()
            PictUrlDatabase.getDatabase(this@UploadActivity).imageDao().insert(pictUrlImage)
            startActivity(DetailActivity.createIntent(this@UploadActivity, pictUrlImage))
        }
    }

    private fun createThumbnail(imageBinary: ByteArray): Uri {
        val imgBitmap = BitmapFactory.decodeByteArray(imageBinary, 0, imageBinary.size)
        val thumbnail = ThumbnailUtils.extractThumbnail(imgBitmap, 500, 500)
        val fileName = System.currentTimeMillis().toString()
        this@UploadActivity.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        return Uri.fromFile(this.filesDir).buildUpon().appendPath(fileName).build()
    }

    private fun showError() {
        TODO("Not yet implemented")
    }


}