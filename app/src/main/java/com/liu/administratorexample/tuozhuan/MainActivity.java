package com.liu.administratorexample.tuozhuan;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.View.DragShadowBuilder;

public class MainActivity extends Activity implements OnDragListener{


    private GridLayout mGridLayout;
    private int mIndex;
    private int mMargin = 8;
    private static SparseArray<String> mArr = new SparseArray<String>();
    private View mDragView;

    // 给类进行初始化 随着类的加载而加载
    static {
        // 当开始拖拽View的时候会执行一次
        mArr.put(DragEvent.ACTION_DRAG_STARTED, "STARTED");
        // 在拖拽区域范围内松开手指的时候会执行一次
        mArr.put(DragEvent.ACTION_DROP, "DROP");
        // 在进入拖拽区域的那一瞬间执行一次
        mArr.put(DragEvent.ACTION_DRAG_ENTERED, "ENTERTED");
        // 在拖拽区域拖拽的时候一直执行
        mArr.put(DragEvent.ACTION_DRAG_LOCATION, "LOCATION");
        // 拖拽结束的时候执行一次
        mArr.put(DragEvent.ACTION_DRAG_ENDED, "ENDED");
        // 在出去拖拽区域的一瞬间执行一次
        mArr.put(DragEvent.ACTION_DRAG_EXITED, "EXITED");
    }

    // 将拖拽事件以字符串形式返回
    // private static String getDragEvent(int event) {
    // return mArr.get(event);
    // }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mGridLayout = (GridLayout) findViewById(R.id.gridLayout);
    }
    /**
     * 添加条目
     *
     * @param view
     */
    public void addItem(View view) {
        TextView tv = new TextView(MainActivity.this);
        tv.setText("条目" + mIndex);

        // 设置GridLayout中条目的宽高
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels / 4
                - mMargin * 2;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.setMargins(mMargin, 6, mMargin, 6);
        tv.setLayoutParams(params);


        // 设置条目的背景和条目的内边距
        tv.setBackgroundResource(R.drawable.drag_item_selector);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(6, 6, 6, 6);

        mGridLayout.addView(tv, mIndex);
        mIndex++;
        // 设置条目长按事件
        tv.setOnLongClickListener(longClick);

        // 设置条目的拖拽换位
        mGridLayout.setOnDragListener(this);
    }
    // 条目长按事件
    private View.OnLongClickListener longClick = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            mDragView = v;
            // 开始拖拽
            v.startDrag(null, new DragShadowBuilder(v), null, 0);
            v.setEnabled(false);
            return false;
        }
    };


    // 是否拖拽
    @Override
    public boolean onDrag(View arg0, DragEvent event) {
        // String dragEvent = getDragEvent(arg1.getAction());
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                initRects();
                break;

            // 实时监听拖拽的点是否进入到某一个条目范围内
            case DragEvent.ACTION_DRAG_LOCATION:
                int i = getTouchIndex(event);
                if (i >= 0 && mDragView != null
                        && mGridLayout.getChildAt(i) != mDragView) {
                    mGridLayout.removeView(mDragView);//移除的是被拖拽的view
                    mGridLayout.addView(mDragView, i);//向进入的子条目的位置上添加被拖拽的子view
                }
                break;

            case DragEvent.ACTION_DRAG_ENDED:
                if (mDragView != null)
                    mDragView.setEnabled(true);
                break;
        }

        return true;
    }

    /**
     * 获取到拖拽到的那个条目对应的索引值
     *
     * @param event
     * @return
     */
    private int getTouchIndex(DragEvent event) {
        float dragX = event.getX();
        float dragY = event.getY();
        for (int i = 0; i < mRects.length; i++) {
            if (mRects[i].contains((int) dragX, (int) dragY)) {
                return i;
            }
        }
        return -1;
    }

    private Rect[] mRects;

    // 将每一个条目都封装成他们所对应的矩形并把这些矩形装进矩形数组里面
    private void initRects() {
        mRects = new Rect[mGridLayout.getChildCount()];
        for (int i = 0; i < mRects.length; i++) {
            View childView = mGridLayout.getChildAt(i);
            Rect rect = new Rect(childView.getLeft(), childView.getTop(),
                    childView.getRight(), childView.getBottom());
            mRects[i] = rect;
        }
    }
}
