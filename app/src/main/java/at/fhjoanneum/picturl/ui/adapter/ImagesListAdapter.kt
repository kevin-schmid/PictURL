package at.fhjoanneum.picturl.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.fhjoanneum.picturl.R
import at.fhjoanneum.picturl.model.PictUrlImage

class ImagesListAdapter(
    private var images: MutableList<PictUrlImage>,
    private var clickListener: ImageClickListener
) :
    RecyclerView.Adapter<ImagesListAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val view: View = itemView.findViewById(R.id.imageListItemCardView)
        private val title: TextView = itemView.findViewById(R.id.imageListItemTitle)
        private val link: TextView = itemView.findViewById(R.id.imageListItemLink)
        private val desc: TextView = itemView.findViewById(R.id.imageListItemDescr)
        private val imageView: ImageView = itemView.findViewById(R.id.imageListItemImageView)

        fun bind(image: PictUrlImage) {
            title.text = image.title
            link.text = image.link
            desc.text = image.descr
            imageView.setImageURI(image.localUri)
            view.setOnClickListener { clickListener.onItemClicked(adapterPosition, image) }
            view.setOnLongClickListener {
                clickListener.onItemLongClicked(image)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.image_list_item, parent, false)
        )

    override fun getItemCount() = images.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) =
        holder.bind(images[position])

    fun getPictUrlImage(index: Int): PictUrlImage = images[index]

    fun removeAt(index: Int) = images.removeAt(index)

    fun insertAt(index: Int, element: PictUrlImage) = images.add(index, element)
}