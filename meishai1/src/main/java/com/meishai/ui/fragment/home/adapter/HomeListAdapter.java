package com.meishai.ui.fragment.home.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.meishai.app.widget.layout.AutoScrollLayout;
import com.meishai.app.widget.layout.AutoScrollLayout1;
import com.meishai.app.widget.layout.DaRenRecLayout;
import com.meishai.app.widget.layout.HomeCateLayout1;
import com.meishai.app.widget.layout.HomeHeadLayout;
import com.meishai.app.widget.layout.HomeTopicLayout;
import com.meishai.app.widget.layout.MeiWuRecLayout;
import com.meishai.app.widget.layout.MeiWuRecView;
import com.meishai.app.widget.layout.PostTimeLineLayout1;
import com.meishai.entiy.HomeInfo;
import com.meishai.entiy.HomeInfo.CateInfo;
import com.meishai.entiy.HomeInfo.DaRen;
import com.meishai.entiy.HomeInfo.MeiWu;
import com.meishai.entiy.HomeInfo.SlideInfo;
import com.meishai.entiy.HomeInfo.Subject;
import com.meishai.entiy.ItemInfo;
import com.meishai.net.volley.toolbox.ImageLoader;

/**
 * 主界面 精选fragment对应的适配器 2.0
 *
 * @author Administrator yl
 */
public class HomeListAdapter extends BaseAdapter {

    private final static int TYPE_SLIDE = 0;//幻灯片
    private final static int TYPE_CATE = 1;//分类
    private final static int TYPE_TOPIC = 2;//专题
    private final static int TYPE_MEIWU = 3;//美物推荐
    private final static int TYPE_DAREN = 4;//达人推荐
    private final static int TYPE_POST = 5;//普通item
    private final static int TYPE_MW_PIC = 6;//美物图片

    //private LayoutInflater mInflater;
    private Context mContext;
    private ImageLoader mImageLoader;

    //item数据
    private List<ItemInfo> mList;

    //分类的数据
    private List<CateInfo> mCateList;

    //幻灯片的数据
    private SlideInfo mSlides[];

    //专题的数据
    private Subject mTopic;


    //数据类型与位置的对应关系的集合
    private Map<Integer, Object> mDataTypes;
    private int mPosition = 0;

    //所有数据
    private HomeInfo mHomeInfo;
    private View mRoot;


    public HomeListAdapter(Context context, ImageLoader imageLoader) {
        //mInflater = LayoutInflater.from(context);
        mContext = context;
        mImageLoader = imageLoader;
        mDataTypes = new HashMap<Integer, Object>();
        mList = new ArrayList<ItemInfo>();
    }

    private void setSlide(List<SlideInfo> items) {
        mSlides = new SlideInfo[items.size()];
        for (int i = 0; i < items.size(); i++) {
            mSlides[i] = items.get(i);
        }

    }

    private void setCate(List<CateInfo> list) {
        mCateList = list;

    }

    private void setTopic(Subject topic) {
        mTopic = topic;
    }

    public void addCollection(List<ItemInfo> items) {

        if (items == null || items.isEmpty()) {
            //没有数据
            return;
        }
        if (mList == null) {
            mList = items;
        }
        if (mList.containsAll(items)) {
            return;
        }
        mList.addAll(items);
        for (ItemInfo itemInfo : items) {
            mDataTypes.put(mPosition++, itemInfo);
        }
        notifyDataSetChanged();
    }

    /**
     * 初始化数据
     *
     * @param homeInfo
     */
    public void setData(HomeInfo homeInfo) {
        mHomeInfo = homeInfo;
        if (mHomeInfo.page == 1) {
            mDataTypes.clear();
            mList.clear();
            mPosition = 0;
        }


        if (mHomeInfo.slide != null) {
            setSlide(mHomeInfo.slide);
            mDataTypes.put(mPosition++, mHomeInfo.slide);
        }

        if (mHomeInfo.application != null) {
            setCate(mHomeInfo.application);
            mDataTypes.put(mPosition++, mHomeInfo.application);
        }

        if (mHomeInfo.topic != null) {
            setTopic(mHomeInfo.topic);
            mDataTypes.put(mPosition++, mHomeInfo.topic);
        }
        if (null != mHomeInfo.daren) {
//			setMeiWu(mHomeInfo.item);
            mDataTypes.put(mPosition++, mHomeInfo.daren);
        }
        if (null != mHomeInfo.item) {
//			setDaRen(mHomeInfo.daren);
            mDataTypes.put(mPosition++, mHomeInfo.item);
        }
        if (null != mHomeInfo.meiwu) {
            mDataTypes.put(mPosition++, mHomeInfo.meiwu);
        }

        addCollection(mHomeInfo.list);
    }


    @Override
    public int getCount() {

        return mDataTypes.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getViewTypeCount() {
        return 7;
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = mDataTypes.get(position);
        if (obj instanceof Subject) {//专题
            return TYPE_TOPIC;
        } else if (obj instanceof MeiWu) {//美物推荐
            return TYPE_MEIWU;
        } else if (obj instanceof DaRen) {//达人推荐
            return TYPE_DAREN;
        } else if (obj instanceof ArrayList) {
            Object o = ((ArrayList<?>) obj).get(0);
            if (o instanceof SlideInfo) {//幻灯片
                return TYPE_SLIDE;
            } else if (o instanceof CateInfo) {//分类
                return TYPE_CATE;
            } else if (o instanceof HomeInfo.MeiWuItem) {//美物图片
                return TYPE_MW_PIC;
            }

        }
        return TYPE_POST;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //TODO 主页的5种不同的item
        Object item = getItem(position);
        switch (getItemViewType(position)) {
            case TYPE_SLIDE:
                if (null == convertView) {
                    convertView = new HomeHeadLayout(mContext);
                }
                ((HomeHeadLayout) convertView).setData(mSlides, mImageLoader);
                break;
            case TYPE_CATE: //分类 mCateList 还差后期适配
                if (null == convertView) {
                    convertView = new HomeCateLayout1(mContext);
                }
//			((HomeCateLayout1)convertView).setRootView(mRoot);
                ((HomeCateLayout1) convertView).setData((List<CateInfo>) item, mHomeInfo.sharedata, mImageLoader);
                break;
            case TYPE_TOPIC: //专题 mTopic 还得适配
                if (null == convertView) {
                    convertView = new HomeTopicLayout(mContext);

                }
                ((HomeTopicLayout) convertView).setData((Subject) item, mImageLoader);
                break;
            case TYPE_POST: //item  图片显示还需要适配
                if (null == convertView) {
                    convertView = new PostTimeLineLayout1(mContext, false);
                }
                ((PostTimeLineLayout1) convertView).setData((ItemInfo) item, mImageLoader);
                break;
            case TYPE_MEIWU: //美物推荐
                if (null == convertView) {
                    convertView = new MeiWuRecLayout(mContext);
                }
                ((MeiWuRecLayout) convertView).setData((MeiWu) item, mImageLoader);
                break;
            case TYPE_MW_PIC: //美物图片
                if (null == convertView) {
                    convertView = new MeiWuRecView(mContext);
                }
                ((MeiWuRecView) convertView).setData((List<HomeInfo.MeiWuItem>) item, mImageLoader);
                break;
            case TYPE_DAREN: //达人推荐 搞定
                if (null == convertView) {
                    convertView = new DaRenRecLayout(mContext);
                }
                ((DaRenRecLayout) convertView).setData((DaRen) item, mImageLoader);
                break;
            default:
                break;
        }

        return convertView;
    }


}
