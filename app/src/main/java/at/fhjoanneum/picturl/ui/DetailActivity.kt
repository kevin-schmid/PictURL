package at.fhjoanneum.picturl.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import at.fhjoanneum.picturl.MAIN_ACTIVITY_INTENT_EXTRA_DELETED
import at.fhjoanneum.picturl.R
import at.fhjoanneum.picturl.model.PictUrlImage
import at.fhjoanneum.picturl.util.CheckConnectionUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)!!
        val imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL)!!
        val imageTitle = intent.getStringExtra(EXTRA_IMAGE_TITLE)
        val imageDesc = intent.getStringExtra(EXTRA_IMAGE_DESC)
        val position = intent.getIntExtra(EXTRA_IMAGE_POSITION, -1)

        findViewById<ImageView>(R.id.detailImageView).setImageURI(imageUri)
        findViewById<Button>(R.id.detailButton).text = imageUrl
        findViewById<TextView>(R.id.detailTitleTextView).text = imageTitle
        findViewById<TextView>(R.id.detailItemDescr).text = imageDesc

        findViewById<FloatingActionButton>(R.id.detailActionButton).setOnClickListener {
            if (!CheckConnectionUtil.isConnected(this)) {
                Toast.makeText(applicationContext, "Offline", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(
                    Intent(this@DetailActivity, MainActivity::class.java)
                        .putExtra(MAIN_ACTIVITY_INTENT_EXTRA_DELETED, position)
                )
            }
        }

        findViewById<Button>(R.id.detailButton).setOnLongClickListener {
            val tv = findViewById<Button>(R.id.detailButton)
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(tv.text.toString())
            startActivity(openURL)
            true
        }

    }

    companion object {
        const val EXTRA_IMAGE_URL = "Extra_Image_URL"
        const val EXTRA_IMAGE_TITLE = "Extra_Image_Title"
        const val EXTRA_IMAGE_DESC = "Extra_Image_Desc"
        const val EXTRA_IMAGE_POSITION = "Extra_Image_Position"

        fun createIntent(context: Context, image: PictUrlImage, position: Int) =
            Intent(context, DetailActivity::class.java)
                .putExtra(Intent.EXTRA_STREAM, image.localUri)
                .putExtra(EXTRA_IMAGE_URL, image.link)
                .putExtra(EXTRA_IMAGE_TITLE, image.title)
                .putExtra(EXTRA_IMAGE_DESC, image.descr)
                .putExtra(EXTRA_IMAGE_POSITION, position)
    }

    fun toClipboard() {
        val tv = findViewById<Button>(R.id.detailButton)
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("link", tv.text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
    }
}