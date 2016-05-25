package com.meishai.ui.fragment.home.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.meishai.app.widget.layout.AutoScrollLayout;
import com.meishai.app.widget.layout.HomeCateLayout;
import com.meishai.app.widget.layout.PostTimeLineLayout;
import com.meishai.entiy.CateInfo;
import com.meishai.entiy.PostItem;
import com.meishai.entiy.SlideItem;
import com.meishai.net.volley.toolbox.ImageLoader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class PostListAdapter extends BaseAdapter {

    private final static int TYPE_SLIDE = 0;
    private final static int TYPE_CATE = 1;
    private final static int TYPE_POST = 2;

    //private LayoutInflater mInflater;
    private Context mContext;
    private ImageLoader mImageLoader;

    private List<PostItem> mList;

    private List<CateInfo> mCateList;

    private SlideItem mSlides[];


    public PostListAdapter(Context context, ImageLoader imageLoader) {
        //mInflater = LayoutInflater.from(context);
        mContext = context;
        mImageLoader = imageLoader;
        mList = new ArrayList<PostItem>();
        mCateList = new ArrayList<CateInfo>();
        //mSlides = new SlideItem[0];
    }

    public void setSlide(SlideItem[] items) {
        mSlides = items;
        notifyDataSetChanged();
    }

    public void setCate(List<CateInfo> list) {
        mCateList = list;
        notifyDataSetChanged();
    }

    public void addCollection(Collection<PostItem> items) {
        mList.addAll(items);
        notifyDataSetChanged();
    }

    public void clearPost() {
        mList.clear();
    }


    @Override
    public int getCount() {
        return mList.size() + 2;
    }

    @Override
    public Object getItem(int position) {
        if (0 == position) {
            return mSlides;

        } else if (1 == position) {
            return mCateList;

        } else {
            return mList.get(position - 2);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getViewTypeCount() {
        //return super.getViewTypeCount();
        return 3;
    }


    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        switch (position) {
            case 0:
                return TYPE_SLIDE;
            case 1:
                return TYPE_CATE;
            default:
                return TYPE_POST;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Object item = getItem(position);
        switch (getItemViewType(position)) {
            case TYPE_SLIDE:
                if (null == convertView) {
                    convertView = new AutoScrollLayout(mContext);
                }
                ((AutoScrollLayout) convertView).setData((SlideItem[]) item, mImageLoader);
                break;
            case TYPE_CATE:
                if (null == convertView) {
                    convertView = new HomeCateLayout(mContext);
                }
                ((HomeCateLayout) convertView).setData((List<CateInfo>) item, mImageLoader);
                break;
            case TYPE_POST:
                if (null == convertView) {
                    convertView = new PostTimeLineLayout(mContext, false);
                }
                ((PostTimeLineLayout) convertView).setData((PostItem) item, mImageLoader);
                break;
            default:
                break;
        }

        return convertView;
    }


}
