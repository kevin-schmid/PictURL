package at.fhjoanneum.picturl.ui.adapter

import at.fhjoanneum.picturl.model.PictUrlImage

interface ImageClickListener {
    fun onItemClicked(item: PictUrlImage)
}