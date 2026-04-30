package `in`.gov.mahapocra.mahavistaarai.ui.screens.newui.dashboard

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val horizontalSpacing: Int,
    private val verticalSpacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        // Horizontal spacing
        outRect.left = horizontalSpacing / 2
        outRect.right = horizontalSpacing / 2

        // Vertical spacing (THIS controls gap between rows)
        outRect.top = verticalSpacing / 2
        outRect.bottom = verticalSpacing / 2
    }
}