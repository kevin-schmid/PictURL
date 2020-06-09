package at.fhjoanneum.picturl.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.fhjoanneum.picturl.R
import at.fhjoanneum.picturl.model.PictUrlImage

class ImagesListAdapter(private var images: List<PictUrlImage>, private var clickListener: ImageClickListener) :
    RecyclerView.Adapter<ImagesListAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View, private var clickListener: ImageClickListener) : RecyclerView.ViewHolder(itemView) {
        private val view: View = itemView.findViewById(R.id.imageListItemCardView)
        private val title: TextView = itemView.findViewById(R.id.imageListItemTitle)
        private val link: TextView = itemView.findViewById(R.id.imageListItemLink)
        private val tags: TextView = itemView.findViewById(R.id.imageListItemTags)
        private val imageView: ImageView = itemView.findViewById(R.id.imageListItemImageView)

        fun bind(image: PictUrlImage) {
            title.text = image.title
            link.text = image.link
            imageView.setImageURI(image.localUri)
            view.setOnClickListener { clickListener.onItemClicked(image) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
        ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.image_list_item, parent, false),
            clickListener
        )

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) =
        holder.bind(images[position])

}