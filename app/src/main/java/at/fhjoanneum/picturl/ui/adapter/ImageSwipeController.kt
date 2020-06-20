package at.fhjoanneum.picturl.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import at.fhjoanneum.picturl.R


class ImageSwipeController(context: Context, private var swipeListener: ImageSwipeListener): ItemTouchHelper.Callback() {
    var swipeBack = false
    private val icon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_24)!!

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
        if (actionState == ACTION_STATE_SWIPE) {
            recyclerView.setOnTouchListener { _, event ->
                swipeBack =
                    event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
                if(swipeBack && dX > c.width*0.75) {
                    swipeListener.onRightSwipe(viewHolder.adapterPosition)
                }
                false
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val p = Paint()
        val corners = 16f
        val deleteRect = RectF(
            viewHolder.itemView.left.toFloat(),
            viewHolder.itemView.top.toFloat(),
            viewHolder.itemView.right.toFloat(),
            viewHolder.itemView.bottom.toFloat()
        )
        p.color = Color.rgb(240, 85, 69)
        c.drawRoundRect(deleteRect, corners, corners, p)
        p.color = Color.BLACK
        icon.setBounds(150,
            deleteRect.centerY().toInt()-50,
            250,
            deleteRect.centerY().toInt()+50)
        icon.draw(c)
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if(swipeBack) {
            swipeBack = false
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }
}