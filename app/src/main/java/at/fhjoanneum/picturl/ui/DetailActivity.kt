package at.fhjoanneum.picturl.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import at.fhjoanneum.picturl.R
import at.fhjoanneum.picturl.db.PictUrlDatabase
import at.fhjoanneum.picturl.model.PictUrlImage
import at.fhjoanneum.picturl.service.UploadService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)!!
        val imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL)!!
        val imageId = intent.getStringExtra(EXTRA_IMAGE_ID)!!
        val imageHash = intent.getStringExtra(EXTRA_IMAGE_DELETE_HASH)!!
        val imageTitle = intent.getStringExtra(EXTRA_IMAGE_TITLE)

        findViewById<ImageView>(R.id.detailImageView).setImageURI(imageUri)
        findViewById<Button>(R.id.detailButton).text = imageUrl
        findViewById<TextView>(R.id.detailTitleTextView).text = imageTitle

        findViewById<FloatingActionButton>(R.id.detailActionButton).setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                UploadService.delete(imageHash)
                PictUrlDatabase.getDatabase(this@DetailActivity).imageDao().delete(imageId)
                startActivity(Intent(this@DetailActivity, MainActivity::class.java))
            }
        }
    }

    companion object {
        const val EXTRA_IMAGE_URL = "Extra_Image_URL"
        const val EXTRA_IMAGE_ID = "Extra_Image_ID"
        const val EXTRA_IMAGE_DELETE_HASH = "Extra_Image_DeleteHash"
        const val EXTRA_IMAGE_TITLE = "Extra_Image_Title"

        fun createIntent(context: Context, image: PictUrlImage) =
            Intent(context, DetailActivity::class.java)
                .putExtra(Intent.EXTRA_STREAM, image.localUri)
                .putExtra(EXTRA_IMAGE_URL, image.link)
                .putExtra(EXTRA_IMAGE_ID, image.id)
                .putExtra(EXTRA_IMAGE_DELETE_HASH, image.deleteHash)
                .putExtra(EXTRA_IMAGE_TITLE, image.title)
    }

    fun toClipboard(v: View){
        val tv = findViewById<Button>(R.id.detailButton)
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("link", tv.text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this,"Copied to Clipboard", Toast.LENGTH_SHORT).show();
    }
}