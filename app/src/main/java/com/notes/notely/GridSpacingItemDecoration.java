package com.notes.notely;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int spanCount;  // Number of columns
    private int spacing;    // Space between items in pixels

    public GridSpacingItemDecoration(Context context, int spanCount, int spacingDp) {
        this.spanCount = spanCount;
        // Convert dp to pixels
        this.spacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, spacingDp, context.getResources().getDisplayMetrics());
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // Item position
        int column = position % spanCount; // Column index (0-based)

        // Apply left and right spacing for all columns
        Log.d("GridSpacing", "Position: " + position + ", Column: " + column);
        if (column < spanCount - 1) {
            outRect.right = spacing; // Right space between columns
        } else {
            outRect.right = 0; // No space for the last column
        }

        // Apply bottom spacing for all items
        outRect.bottom = spacing; // Bottom space between rows

        // Apply left spacing to the first column
        if (column == 0) {
            outRect.left = spacing; // Left space between columns
        }
    }

}
