package at.fhjoanneum.picturl.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.fhjoanneum.picturl.R
import at.fhjoanneum.picturl.db.PictUrlDatabase
import at.fhjoanneum.picturl.model.PictUrlImage
import at.fhjoanneum.picturl.ui.adapter.ImageClickListener
import at.fhjoanneum.picturl.ui.adapter.ImagesListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), ImageClickListener {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.mainRecyclerView)
        findViewById<FloatingActionButton>(R.id.mainActionButton).setOnClickListener {
            val pickPhoto = Intent(
                Intent.ACTION_OPEN_DOCUMENT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ).setType("image/*")
            startActivityForResult(pickPhoto, 1)
        }
        loadImages()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main,menu)
        val searchItem = menu?.findItem(R.id.menu_search)
        if(searchItem!=null){
            val searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    if(newText!!.isNotEmpty()){
                        loadFilteredImages(newText.toLowerCase())
                    }else{
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
                PictUrlDatabase.getDatabase(this@MainActivity).imageDao().getFiltered("%$title%"),
                this@MainActivity
            )
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun loadImages() {
        GlobalScope.launch(Dispatchers.Main) {
            recyclerView.adapter = ImagesListAdapter(
                PictUrlDatabase.getDatabase(this@MainActivity).imageDao().getAll(),
                this@MainActivity
            )
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data?.data != null) {
            startActivity(
                Intent(this, UploadActivity::class.java).putExtra(
                    Intent.EXTRA_STREAM,
                    data.data!!
                )
            )
        }
    }

    override fun onItemClicked(item: PictUrlImage) =
        startActivity(DetailActivity.createIntent(this, item))

    override fun getContext(): Context = this
}