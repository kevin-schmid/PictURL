package at.fhjoanneum.picturl.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import android.os.Handler
import android.view.MotionEvent
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import at.fhjoanneum.picturl.db.PictUrlDatabase
import at.fhjoanneum.picturl.service.UploadService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ImageSwipeController(private var context: Context, private var swipeListener: ImageSwipeListener): ItemTouchHelper.Callback() {
    var swipeBack = false
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val image = (recyclerView.adapter as ImagesListAdapter).getPictUrlImage(viewHolder.adapterPosition)
        val dialogClickListener =
            DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        GlobalScope.launch(Dispatchers.Main) {
                            UploadService.delete(image.deleteHash)
                            PictUrlDatabase.getDatabase(context).imageDao().delete(image.id)
                            swipeListener.onRightSwipe(viewHolder.adapterPosition)
                        }
                    }
                }
            }

        if (actionState == ACTION_STATE_SWIPE) {
            recyclerView.setOnTouchListener { _, event ->
                swipeBack =
                    event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
                if(swipeBack && dX > c.width/2) {
                    AlertDialog.Builder(context)
                        .setMessage("Do you want to delete the image?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show()
                }
                false
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if(swipeBack) {
            swipeBack = false
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }
}