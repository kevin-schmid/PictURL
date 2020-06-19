package at.fhjoanneum.picturl.ui.adapter

import android.content.Context
import at.fhjoanneum.picturl.model.PictUrlImage

interface ImageClickListener {
    fun onItemClicked(item: PictUrlImage)
    fun getContext(): Context
}