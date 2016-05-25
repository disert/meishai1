package com.meishai.app.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.HomeFindRespData;
import com.meishai.net.volley.toolbox.ImageLoader;

/**
 * 文件名：
 * 描    述：晒晒 - 豆腐块的抽象view,子view需要实现其addViewToLayout方法来为期添加view
 * 作    者：
 * 时    间：2016/2/16
 * 版    权：
 */
public abstract class HomeDouFuKuaiLayout<T> extends LinearLayout {

    private Context mContext;
    //标题部分
    private LinearLayout mTilteRoot;
    private TextView mTitleMore;
    private TextView mTitleText;

    //标题与内容间的分割线
    private View mCenterLine;
    //内容部分
    private LinearLayout mContentLayout;
    private GridView mContentGrid;

    //底部的更多部分
    private LinearLayout mMoreRoot;
    private TextView mMoreText;

    //底部分隔线
    private LinearLayout mDivideLine;

    //数据
    private HomeFindRespData.DataInfo<T> mData;

    //加载网络图片用到的图片加载器,如果不加载网络图片可以传入null
    private ImageLoader mImageLoader;

    //更多按钮的显示样式
    private int mMoreType = MORE_TYPE_RIGHT_TOP;
    public static final int MORE_TYPE_RIGHT_TOP = 0;
    public static final int MORE_TYPE_BOTTON = 1;

    //内容部分的显示样式
    private int mContextType = CONTENT_TYPE_LAYOUT;
    public static final int CONTENT_TYPE_LAYOUT = 0;
    public static final int CONTENT_TYPE_GRID = 1;

    public HomeDouFuKuaiLayout(Context context) {
        this(context, null);
    }

    public HomeDouFuKuaiLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.view_doufukuai_layout, this);

        mContext = context;
        mTilteRoot = (LinearLayout) findViewById(R.id.title_root);
        mTitleMore = (TextView) findViewById(R.id.title_more);
        mTitleText = (TextView) findViewById(R.id.title_text);

        mCenterLine = findViewById(R.id.line_center);

        mContentLayout = (LinearLayout) findViewById(R.id.content_layout);
        mContentGrid = (GridView) findViewById(R.id.content_grid);

        mMoreText = (TextView) findViewById(R.id.more_text);
        mMoreRoot = (LinearLayout) findViewById(R.id.more_container);

        mDivideLine = (LinearLayout) findViewById(R.id.divide_line);
    }

    public void setData(HomeFindRespData.DataInfo<T> dataInfo, ImageLoader imageLoader) {
        if (mData == dataInfo) return;//优化,当数据一样的情况下就不去处理了
        mData = dataInfo;
        mImageLoader = imageLoader;
        initData();
    }

    private void initData() {
        if (mData == null || mData.datas == null || mData.datas.isEmpty()) {
            return;
        }
        mTitleText.setText(mData.title);
        //设置更多的显示
        if (mMoreType == MORE_TYPE_RIGHT_TOP) {
            mTitleMore.setVisibility(VISIBLE);
            mTitleMore.setText(mData.more_text);
            mMoreRoot.setVisibility(GONE);
        } else {
            mTitleMore.setVisibility(GONE);
            mMoreRoot.setVisibility(VISIBLE);
            mMoreText.setText(mData.more_text);
        }
        //        mTitleMore.setText(mData.more_text);

        initItem();
    }

    private void initItem() {

        if (mContextType == CONTENT_TYPE_LAYOUT) {
            mContentGrid.setVisibility(GONE);
            mContentLayout.setVisibility(VISIBLE);
            addViewToLayout(mContentLayout, mData, mImageLoader);
        } else {
            mContentGrid.setVisibility(VISIBLE);
            mContentLayout.setVisibility(GONE);
            mContentGrid.setAdapter(new MyAdapter());
        }
    }

    /**
     * 设置更多按钮的点击事件监听器 注意 如果之后再设置更多按钮的显示样式的话会使其失效!
     *
     * @param listener
     */
    public void setMoreOnclickListener(OnClickListener listener) {
        if (mMoreType == MORE_TYPE_BOTTON) {
            mMoreRoot.setOnClickListener(listener);
        } else {
            mTitleMore.setOnClickListener(listener);
        }
    }

    /**
     * 设置标题的显示样式 默认是 visibility
     *
     * @param visibility
     */
    public void setTitleVisibility(int visibility) {
        mTilteRoot.setVisibility(visibility);
    }

    /**
     * 设置底部分割线的显示样式 默认是 GONE
     *
     * @param visibility
     */
    public void setDivideVisibility(int visibility) {
        mDivideLine.setVisibility(visibility);
    }

    /**
     * 设置中间分割线的显示样式 默认是 visibility
     *
     * @param visibility
     */
    public void setCenterDivideVisibility(int visibility) {
        mCenterLine.setVisibility(visibility);
    }

    /**
     * 设置更多按钮的显示样式,需在调用setdata之前调用,否则无效
     *
     * @param moreType 只能是 MORE_TYPE_RIGHT_TOP | MORE_TYPE_BOTTON
     */
    public void setMoreType(int moreType) {
        if (moreType == MORE_TYPE_RIGHT_TOP || moreType == MORE_TYPE_BOTTON) {
            mMoreType = moreType;
        } else {
            throw new RuntimeException("moreType 的值只能是 MORE_TYPE_RIGHT_TOP | MORE_TYPE_BOTTON");
        }
    }

    /**
     * 设置内容的显示样式,需在调用setdata之前调用,否则无效
     *
     * @param contextType 值只能是 CONTENT_TYPE_GRID | CONTENT_TYPE_LAYOUT"
     */
    public void setContextType(int contextType) {
        if (contextType == CONTENT_TYPE_GRID || contextType == CONTENT_TYPE_LAYOUT) {
            mContextType = contextType;
        } else {
            throw new RuntimeException("contextType 的值只能是 CONTENT_TYPE_GRID | CONTENT_TYPE_LAYOUT");
        }
    }

    public GridView getContentGrid() {
        return mContentGrid;
    }

    /**
     * 添加view的具体方法,由子类实现
     *
     * @param layout      view添加到哪个布局
     * @param data        view所需要的数据
     * @param imageLoader view加载图片所要用到的加载器
     */
    public abstract void addViewToLayout(LinearLayout layout, HomeFindRespData.DataInfo<T> data, ImageLoader imageLoader);

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mData == null || mData.datas == null || mData.datas.isEmpty()) {
                return 0;
            }
            return mData.datas.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            T item = (T) getItem(position);
            return getConvertView(position, convertView, parent, item);
        }
    }

    /**
     * 显示的view的具体实现 当使用gridview显示的时候会调用该方法
     *
     * @param position
     * @param convertView
     * @param parent
     * @param item
     * @return
     */
    protected View getConvertView(int position, View convertView, ViewGroup parent, T item) {
        return new View(mContext);
    }
}
