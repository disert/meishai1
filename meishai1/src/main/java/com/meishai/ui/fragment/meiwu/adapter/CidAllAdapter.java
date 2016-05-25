package com.meishai.ui.fragment.meiwu.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.meishai.app.widget.layout.MeiWuItemLayout;
import com.meishai.app.widget.layout.PicListLayout;
import com.meishai.entiy.HomeInfo;
import com.meishai.entiy.StrategyResqData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;

import java.util.List;

/**
 * 文件名：CidAllAdapter
 * 描    述：美物,当cid是0时的适配器 (全部)
 * 作    者：
 * 时    间：2016/1/20
 * 版    权：
 */
public class CidAllAdapter extends MeiwuAdapter {

    private static final int TYPE_SLIDE = 0;
    private static final int TYPE_POST = 1;
    private StrategyResqData mData;
    private HomeInfo.SlideInfo[] mSlides;

    public CidAllAdapter(Activity context, ImageLoader imageLoader) {
        super(context, imageLoader);
    }

    @Override
    protected void initData(String data) {
        if (!TextUtils.isEmpty(data)) {
            StrategyResqData resqData = GsonHelper.parseObject(data,
                    StrategyResqData.class);
            if (resqData == null) {
                AndroidUtil.showToast("数据解析错误!");
                DebugLog.w("数据解析错误:" + data);
                return;
            }
            if (resqData.page < 1) {
                return;
            } else if (resqData.page == 1) {
                mData = resqData;
                if (mData.slide != null) {
                    setSlide(mData.slide);
                } else {
                    mSlides = null;
                }
            } else {
                if (mData == null || mData.list == null) {
                    return;
                }
                if (!mData.list.containsAll(resqData.list)) {
                    mData.list.addAll(resqData.list);
                }
                mData.page = resqData.page;
            }
        }
    }

    private void setSlide(List<HomeInfo.SlideInfo> items) {
        if (items == null || items.isEmpty()) {
            mSlides = null;
            return;
        }
        mSlides = new HomeInfo.SlideInfo[items.size()];
        for (int i = 0; i < items.size(); i++) {
            mSlides[i] = items.get(i);
        }
    }

    @Override
    public int getCount() {
        if (mData == null || mData.list == null || mData.list.isEmpty()) {
            return 0;
        }
        if (mSlides == null) {
            return mData.list.size();
        }
        return mData.list.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0 && mSlides != null) {
            return mSlides;
        } else if (getCount() == mData.list.size()) {
            return mData.list.get(position);
        }
        return mData.list.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mSlides != null) {
            return TYPE_SLIDE;
        } else {
            return TYPE_POST;
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Object item = getItem(position);
        switch (getItemViewType(position)) {
            case TYPE_SLIDE://  幻灯片 mSlides 现在需要更改为4张图片
                if (null == convertView) {
                    convertView = new PicListLayout(mContext);
                }
                ((PicListLayout) convertView).setData(mSlides, mImageLoader);
//            ((PicListLayout) convertView).setOnPicClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                }
//            });
                break;

            case TYPE_POST: // item 图片显示还需要适配
                if (null == convertView) {
                    convertView = new MeiWuItemLayout(mContext);
                }
                ((MeiWuItemLayout) convertView).setData((StrategyResqData.StratData) item,
                        mImageLoader);
                break;
            default:
                break;
        }

        return convertView;
    }
}
