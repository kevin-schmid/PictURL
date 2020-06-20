package at.fhjoanneum.picturl.ui.adapter

import android.content.Context
import at.fhjoanneum.picturl.model.PictUrlImage

interface ImageClickListener {
    fun onItemClicked(position: Int, item: PictUrlImage)
    fun onItemLongClicked(item: PictUrlImage)
    fun getContext(): Context
}