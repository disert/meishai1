package com.meishai.ui.fragment.meiwu.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.meishai.app.widget.layout.AutoScrollLayout1;
import com.meishai.app.widget.layout.MeiWuItemLayout;
import com.meishai.entiy.HomeInfo.SlideInfo;
import com.meishai.entiy.StrategyResqData;
import com.meishai.entiy.StrategyResqData.StratData;
import com.meishai.net.volley.toolbox.ImageLoader;

/**
 * 主界面 精选fragment对应的适配器 2.0
 *
 * @author Administrator yl
 */
public class MeiWuStratAdapter extends BaseAdapter {

    private final static int TYPE_SLIDE = 0;// 幻灯片
    private final static int TYPE_POST = 1;// 普通item
//	private final static int TYPE_CATE = 2;// 专题

    private Activity mContext;
    private ImageLoader mImageLoader;


    // 幻灯片的数据
    private SlideInfo mSlides[];


    // 所有数据
    private StrategyResqData mData;

    public MeiWuStratAdapter(Activity context, ImageLoader imageLoader) {
        mContext = context;
        mImageLoader = imageLoader;
    }

    private boolean scrollState = false;

    public void setScrollState(boolean scrollState) {
        this.scrollState = scrollState;
    }

    private void setSlide(List<SlideInfo> items) {
        if (items == null || items.isEmpty()) {
            mSlides = null;
            return;
        }
        mSlides = new SlideInfo[items.size()];
        for (int i = 0; i < items.size(); i++) {
            mSlides[i] = items.get(i);
        }
    }


    /**
     * 初始化数据
     *
     * @param data
     */
    public void setData(StrategyResqData data) {
        mData = data;
        if (mSlides == null) {
            setSlide(mData.slide);
        }
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if (mData == null || mData.list == null || mData.list.size() == 0) {
            return 0;
        }
        return mData.list.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return mSlides;
        }
//		else if(position == 1){
//			return mData.cate;
//		}
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
        switch (position) {
            case 0:
                return TYPE_SLIDE;
//		case 1:
//			return TYPE_CATE;
            default:
                return TYPE_POST;
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Object item = getItem(position);
        switch (getItemViewType(position)) {
            case TYPE_SLIDE://  幻灯片 mSlides 还差后期适配
                if (null == convertView) {
                    convertView = new AutoScrollLayout1(mContext);
                }
                ((AutoScrollLayout1) convertView).setData(mSlides, mImageLoader);
                break;
//		case TYPE_CATE: // 分类,由专场改为分类
//			if (null == convertView) {
////				convertView = new HomeTopicLayout(mContext);
//				convertView = new MeiWuCateLayout(mContext);
//			}
////			((HomeTopicLayout) convertView).setType(HomeTopicLayout.TYPE_SPECAIL);
////			((HomeTopicLayout) convertView).setData((Subject) item,
////					mImageLoader);
//			if(item != null) {
//				convertView.setVisibility(View.VISIBLE);
//				((MeiWuCateLayout) convertView).setData((List<CateData>) item,
//						mImageLoader);
//			}else {
//				convertView.setVisibility(View.GONE);
//
//			}
//			break;
            case TYPE_POST: // item 图片显示还需要适配
                if (null == convertView) {
                    convertView = new MeiWuItemLayout(mContext);
                }
                ((MeiWuItemLayout) convertView).setScrollState(scrollState);
                ((MeiWuItemLayout) convertView).setData((StratData) item,
                        mImageLoader);
                break;
            default:
                break;
        }

        return convertView;
    }

}
