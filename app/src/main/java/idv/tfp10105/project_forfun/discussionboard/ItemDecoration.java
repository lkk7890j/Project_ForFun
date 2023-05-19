package idv.tfp10105.project_forfun.discussionboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemDecoration extends RecyclerView.ItemDecoration {
    private int space;//定義2个Item之間的距離
    private  Activity activity;

    public ItemDecoration(int space, Context mContext) {
        this.space = dip2px(space,mContext);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int totalCount = parent.getAdapter().getItemCount();


        if (position == 0) {//第一個
            outRect.top = 0;
            outRect.bottom = space / 2;
        } else if (position == totalCount - 1) {//最後一個
            outRect.top = space / 2;
            outRect.bottom = 0;
        } else {//中間其它的
            outRect.top = space / 2;
            outRect.bottom = space / 2;
        }
    }

    public int dip2px(float dpValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
