package at.fhjoanneum.picturl.ui

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.fhjoanneum.picturl.MAIN_ACTIVITY_INTENT_EXTRA_DELETED
import at.fhjoanneum.picturl.MAIN_ACTIVITY_RESULT_PICK_IMAGE
import at.fhjoanneum.picturl.MAIN_ACTIVITY_RESULT_TAKE_PICTURE
import at.fhjoanneum.picturl.R
import at.fhjoanneum.picturl.db.PictUrlDatabase
import at.fhjoanneum.picturl.model.PictUrlImage
import at.fhjoanneum.picturl.service.UploadService
import at.fhjoanneum.picturl.ui.adapter.ImageClickListener
import at.fhjoanneum.picturl.ui.adapter.ImageSwipeController
import at.fhjoanneum.picturl.ui.adapter.ImageSwipeListener
import at.fhjoanneum.picturl.ui.adapter.ImagesListAdapter
import at.fhjoanneum.picturl.util.CheckConnectionUtil
import com.google.android.material.snackbar.Snackbar
import com.leinardi.android.speeddial.SpeedDialView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), ImageClickListener, ImageSwipeListener {
    private lateinit var recyclerView: RecyclerView
    private var photoURI: Uri = Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.mainRecyclerView)
        ItemTouchHelper(ImageSwipeController(this, this)).attachToRecyclerView(recyclerView)
        val speedDialView = findViewById<SpeedDialView>(R.id.mainActionButton)
        speedDialView.inflate(R.menu.menu_main_action)
        speedDialView.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.actionFilePick -> dispatchPickImageIntent()
                R.id.actionCamera -> dispatchTakePictureIntent()
            }
            false
        }
        loadImages()
    }

    private fun dispatchPickImageIntent() {
        val pickPhoto = Intent(
            Intent.ACTION_OPEN_DOCUMENT,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).setType("image/*")
        startActivityForResult(pickPhoto, MAIN_ACTIVITY_RESULT_PICK_IMAGE)
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Log.e("err", "err", ex)
                    null
                }
                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(
                        this,
                        "at.fhjoanneum.picturl",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, MAIN_ACTIVITY_RESULT_TAKE_PICTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val searchItem = menu?.findItem(R.id.menu_search)
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    if (newText!!.isNotEmpty()) {
                        loadFilteredImages(newText.toLowerCase())
                    } else {
                        loadImages()
                    }
                    return true
                }
            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        loadImages()
    }

    private fun loadFilteredImages(title: String) {
        GlobalScope.launch(Dispatchers.Main) {
            recyclerView.adapter = ImagesListAdapter(
                PictUrlDatabase.getDatabase(this@MainActivity).imageDao().getFiltered("%$title%")
                    .toMutableList(),
                this@MainActivity
            )
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun loadImages() {
        GlobalScope.launch(Dispatchers.Main) {
            recyclerView.adapter = ImagesListAdapter(
                PictUrlDatabase.getDatabase(this@MainActivity).imageDao().getAll().toMutableList(),
                this@MainActivity
            )
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            if (intent.hasExtra(MAIN_ACTIVITY_INTENT_EXTRA_DELETED)) {
                deleteItem(intent.getIntExtra(MAIN_ACTIVITY_INTENT_EXTRA_DELETED, -1))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }
        var uri = photoURI
        if (requestCode == MAIN_ACTIVITY_RESULT_PICK_IMAGE) {
            uri = data?.data!!
        }
        startActivity(
            Intent(this, UploadActivity::class.java).putExtra(
                Intent.EXTRA_STREAM,
                uri
            )
        )
    }

    override fun onItemClicked(position: Int, item: PictUrlImage) =
        startActivity(DetailActivity.createIntent(this, item, position))

    override fun onItemLongClicked(item: PictUrlImage) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("link", item.link)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied to Clipboard", Toast.LENGTH_SHORT).show()
    }

    override fun getContext(): Context = this

    override fun onRightSwipe(position: Int) = deleteItem(position)

    private fun deleteItem(position: Int) {
        if (position == -1) {
            return
        }
        if (!CheckConnectionUtil.isConnected(this)) {
            Toast.makeText(applicationContext, "Offline", Toast.LENGTH_SHORT).show()
            return
        }
        val imageListAdapter = recyclerView.adapter as ImagesListAdapter
        val image = imageListAdapter.getPictUrlImage(position)
        imageListAdapter.removeAt(position)
        imageListAdapter.notifyItemRemoved(position)

        Snackbar
            .make(recyclerView, "Image removed", Snackbar.LENGTH_LONG)
            .setAction("UNDO") {
                imageListAdapter.insertAt(position, image)
                imageListAdapter.notifyItemInserted(position)
            }.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(snackbar: Snackbar, event: Int) {
                    if (event == DISMISS_EVENT_TIMEOUT)
                        GlobalScope.launch(Dispatchers.Main) {
                            UploadService.delete(image.deleteHash)
                            PictUrlDatabase.getDatabase(this@MainActivity).imageDao()
                                .delete(image.id)
                        }
                }
            }).show()
    }
}