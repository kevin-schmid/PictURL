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
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import at.fhjoanneum.picturl.R
import at.fhjoanneum.picturl.db.PictUrlDatabase
import at.fhjoanneum.picturl.model.PictUrlImage
import at.fhjoanneum.picturl.service.UploadDto
import at.fhjoanneum.picturl.service.UploadService
import at.fhjoanneum.picturl.util.CheckConnectionUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*

class UploadActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    var localImageUri: Uri = Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)

        findViewById<FloatingActionButton>(R.id.uploadActionButton).setOnClickListener {
            findViewById<FrameLayout>(R.id.uploadFrameLayout).visibility = View.VISIBLE
        }

        findViewById<EditText>(R.id.uploadEditTextDescr).setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                findViewById<FrameLayout>(R.id.uploadFrameLayout).visibility = View.GONE
                findViewById<TextView>(R.id.uploadItemDescr).text =
                    findViewById<EditText>(R.id.uploadEditTextDescr).text.toString()
                hideKeyboard(v)
                true
            } else {
                false
            }
        }

        if (imageUri == null) {
            showError()
        } else {
            val imageBinary =
                this.contentResolver.openInputStream(imageUri)?.buffered()?.use { it.readBytes() }!!
            localImageUri = createThumbnail(imageBinary)
            findViewById<ImageView>(R.id.uploadImageView).setImageURI(localImageUri)
            findViewById<View>(R.id.uploadImageButton).setOnClickListener {
                if (!CheckConnectionUtil.isConnected(this)) {
                    Toast.makeText(applicationContext, "Offline", Toast.LENGTH_SHORT).show()
                } else if (findViewById<EditText>(R.id.uploadEditText).text.toString().isEmpty()) {
                    Toast.makeText(applicationContext, "Enter Title", Toast.LENGTH_SHORT).show()
                } else {
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
                this.descr = findViewById<TextView>(R.id.uploadItemDescr).text.toString()
                this.localUri = localImageUri
            }
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("link", pictUrlImage.link)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(applicationContext, "Copied to Clipboard", Toast.LENGTH_SHORT).show()
            val imageDao = PictUrlDatabase.getDatabase(this@UploadActivity).imageDao()
            val insertPosition = imageDao.getCount()
            imageDao.insert(pictUrlImage)
            startActivity(
                DetailActivity.createIntent(
                    this@UploadActivity,
                    pictUrlImage,
                    insertPosition
                )
            )
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
        Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard(view: View) {
        view?.apply {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}