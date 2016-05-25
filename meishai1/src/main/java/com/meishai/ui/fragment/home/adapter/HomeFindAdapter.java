package com.meishai.ui.fragment.home.adapter;

import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.CircleImageView;
import com.meishai.app.widget.RoundCornerImageView;
import com.meishai.app.widget.layout.HomeDouFuKuaiLayout;
import com.meishai.app.widget.layout.HomePageItem;
import com.meishai.entiy.HomeFindRespData;
import com.meishai.entiy.HomePageDatas;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.fragment.home.HomePageActivity;
import com.meishai.ui.fragment.home.MoreStarActivity;
import com.meishai.ui.fragment.home.MoreTempidActivity;
import com.meishai.ui.fragment.home.TopicShowActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.ImageAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件名：
 * 描    述：晒晒 - 发现 - 适配器
 * 作    者： yl
 * 时    间：2016/2/16
 * 版    权：
 */
public class HomeFindAdapter extends BaseAdapter {

    private final static int TYPE_DAREN = 0;//达人之星
    private final static int TYPE_TOPIC = 1;//专题活动
    private final static int TYPE_AREA = 2;//发现全球
    private final static int TYPE_NUTRALIFE = 3;//品质生活
    private final static int TYPE_TAG = 4;//编辑精选
    private final static int TYPE_BRANK = 5;//热门品牌
    private final static int TYPE_ITEM = 6;//普通item
    private final Point mPoint;
    /**
     * 发现全球
     */
    public HomeFindRespData.DataInfo<HomeFindRespData.DatasItem> mArea;//	Object
    /**
     * 热门品牌
     */
    public HomeFindRespData.DataInfo<HomeFindRespData.DatasItem> mBrand;//	Object
    /**
     * 品质生活
     */
    public HomeFindRespData.DataInfo<HomeFindRespData.DatasItem> mCatalog;//	Object
    /**
     * 专题活动
     */
    public HomeFindRespData.DataInfo<HomeFindRespData.DatasItem> mTopic;//	Object
    /**
     * 编辑精选
     */
    public HomeFindRespData.DataInfo<HomeFindRespData.DatasItem> mTag;//	Object
    /**
     * 达人之星
     */
    public HomeFindRespData.DataInfo<HomeFindRespData.DarenItem> mDaren;//	Object

    private Context mContext;
    private ImageLoader mImageLoader;

    //数据类型与位置的对应关系的集合
    private Map<Integer, TypeData> mDataTypes;
    private int mPosition = 0;

    class TypeData {
        public TypeData(int type, Object obj) {
            this.type = type;
            this.obj = obj;
        }

        public int type;
        public Object obj;
    }


    //所有数据
    private HomeFindRespData mData;
    private List<HomePageDatas.PostInfo> mList;

    private int mPadding;
    private final AbsListView.LayoutParams mParams;


    public HomeFindAdapter(Context context, ImageLoader imageLoader) {
        //mInflater = LayoutInflater.from(context);
        mContext = context;
        mImageLoader = imageLoader;
        mDataTypes = new HashMap<Integer, TypeData>();
        mList = new ArrayList<HomePageDatas.PostInfo>();

        mPadding = AndroidUtil.dip2px(4);
        mPoint = ImageAdapter.getViewRealWH(2, mPadding, 750, 380);
        mParams = new AbsListView.LayoutParams(mPoint.x + mPadding, mPoint.y + mPadding);
//		mParams.
    }

    /**
     * 初始化数据
     *
     * @param homeInfo
     */
    public void setData(HomeFindRespData homeInfo) {
        mData = homeInfo;
        //第一页清空数据
        if (mData.page == 1) {
            mDataTypes.clear();
            mList.clear();
            mPosition = 0;
        }

        //达人之星
        if (mData.daren != null) {
            mDaren = mData.daren;
            if (mDaren != null)
                mDataTypes.put(mPosition++, new TypeData(TYPE_DAREN, mData.daren));
        }

        //专题活动
        if (mData.topic != null) {
            mTopic = mData.topic;
            if (mTopic != null)
                mDataTypes.put(mPosition++, new TypeData(TYPE_TOPIC, mData.topic));
        }

        //全球发现
        if (mData.area != null) {
            mArea = mData.area;
            if (mArea != null)
                mDataTypes.put(mPosition++, new TypeData(TYPE_AREA, mData.area));
        }

        //品质生活
        if (mData.catalog != null) {
            mCatalog = mData.catalog;
            if (mCatalog != null)
                mDataTypes.put(mPosition++, new TypeData(TYPE_NUTRALIFE, mData.catalog));
        }
        //编辑精选
        if (mData.tag != null) {
            mTag = mData.tag;
            if (mTag != null)
                mDataTypes.put(mPosition++, new TypeData(TYPE_TAG, mData.tag));
        }
        //热门品牌
        if (mData.brand != null) {
            mBrand = mData.brand;
            if (mBrand != null)
                mDataTypes.put(mPosition++, new TypeData(TYPE_BRANK, mData.brand));
        }

        //晒晒列表
        addCollection(mData.list);

        notifyDataSetChanged();
    }

    public void addCollection(List<HomePageDatas.PostInfo> items) {
        if (items == null || items.isEmpty()) {
            //没有数据
            return;
        }
        if (mList == null) {
            mList = items;
        } else {
            if (mList.containsAll(items)) {
                return;
            }
            mList.addAll(items);
        }
        for (int i = 0; i < items.size(); i += 2) {
            List<HomePageDatas.PostInfo> infos = new ArrayList<HomePageDatas.PostInfo>();
            infos.add(items.get(i));
            if (i + 1 < items.size()) {
                infos.add(items.get(i + 1));
            }
            mDataTypes.put(mPosition++, new TypeData(TYPE_ITEM, infos));
        }

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
        return mDataTypes.get(position).type;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case TYPE_DAREN://达人之星
                if (convertView == null) {
                    convertView = new HomeDouFuKuaiLayout<HomeFindRespData.DarenItem>(mContext) {
                        //该方法会在setdata方法调用后别执行
                        @Override
                        public void addViewToLayout(LinearLayout layout, HomeFindRespData.DataInfo<HomeFindRespData.DarenItem> data, ImageLoader imageLoader) {
                            layout.removeAllViews();
                            LinearLayout.LayoutParams layoutParams = new LayoutParams(AndroidUtil.dip2px(60), ViewGroup.LayoutParams.WRAP_CONTENT);
                            List<HomeFindRespData.DarenItem> items = data.datas;
                            for (final HomeFindRespData.DarenItem item : items) {
                                //初始化view
                                View view = View.inflate(mContext, R.layout.cir_item_layout, null);
                                CircleImageView image = (CircleImageView) view.findViewById(R.id.image);
                                TextView title = (TextView) view.findViewById(R.id.title);
                                //设置数据
                                title.setText(item.username);
                                image.setTag(item.avatar);
                                ListImageListener listener = new ListImageListener(image, R.drawable.place_default, R.drawable.place_default, item.avatar);
                                imageLoader.get(item.avatar, listener);
                                view.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mContext.startActivity(HomePageActivity.newIntent(item.userid + ""));
                                    }
                                });

                                //把view添加到布局中
                                layout.addView(view, layoutParams);
                            }
                        }
                    };
                }
                ((HomeDouFuKuaiLayout) convertView).setContextType(HomeDouFuKuaiLayout.CONTENT_TYPE_LAYOUT);
                ((HomeDouFuKuaiLayout) convertView).setCenterDivideVisibility(View.VISIBLE);
                ((HomeDouFuKuaiLayout) convertView).setMoreType(HomeDouFuKuaiLayout.MORE_TYPE_RIGHT_TOP);
                ((HomeDouFuKuaiLayout) convertView).setMoreOnclickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(MoreStarActivity.newIntent(mDaren.title));
                    }
                });
                ((HomeDouFuKuaiLayout) convertView).setData(mDaren, mImageLoader);

                break;
            case TYPE_TOPIC://专题活动
                if (convertView == null) {
                    convertView = new HomeDouFuKuaiLayout<HomeFindRespData.DatasItem>(mContext) {
                        //该方法会在setdata方法调用后别执行
                        @Override
                        public void addViewToLayout(LinearLayout layout, HomeFindRespData.DataInfo<HomeFindRespData.DatasItem> data, ImageLoader imageLoader) {
                            layout.removeAllViews();
                            List<HomeFindRespData.DatasItem> items = data.datas;
                            for (final HomeFindRespData.DatasItem item : items) {
                                View view = View.inflate(mContext,
                                        R.layout.home_topic_layout_item, null);

                                view.setPadding(AndroidUtil.dip2px(6), 0, 0, 0);
                                RoundCornerImageView image = (RoundCornerImageView) view
                                        .findViewById(R.id.htli_iv);

                                image.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mContext.startActivity(TopicShowActivity.newIntent(item.tid));
                                    }
                                });
                                // 专题的图片加载
                                image.setTag(item.image);
                                ListImageListener listener = new ListImageListener(image,
                                        R.drawable.place_default, R.drawable.place_default,
                                        item.image);
                                int wh = AndroidUtil.dip2px(100);
                                mImageLoader.get(item.image, listener, wh, wh);

                                layout.addView(view);
                            }
                        }
                    };
                }
                ((HomeDouFuKuaiLayout) convertView).setCenterDivideVisibility(View.VISIBLE);
                ((HomeDouFuKuaiLayout) convertView).setContextType(HomeDouFuKuaiLayout.CONTENT_TYPE_LAYOUT);
                ((HomeDouFuKuaiLayout) convertView).setMoreType(HomeDouFuKuaiLayout.MORE_TYPE_RIGHT_TOP);
                ((HomeDouFuKuaiLayout) convertView).setMoreOnclickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(MoreTempidActivity.newIntent(mTopic.tempid, mTopic.cid, mTopic.title));
                    }
                });
                ((HomeDouFuKuaiLayout) convertView).setData(mTopic, mImageLoader);

                break;
            case TYPE_AREA://发现全球
                if (convertView == null) {
                    convertView = getHomeDouFuKuaiLayout();
                }
                ((HomeDouFuKuaiLayout) convertView).setCenterDivideVisibility(View.GONE);
                ((HomeDouFuKuaiLayout) convertView).setContextType(HomeDouFuKuaiLayout.CONTENT_TYPE_GRID);
                ((HomeDouFuKuaiLayout) convertView).setMoreType(HomeDouFuKuaiLayout.MORE_TYPE_BOTTON);
                ((HomeDouFuKuaiLayout) convertView).setMoreOnclickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(MoreTempidActivity.newIntent(mArea.tempid, mArea.cid, mArea.title));
                    }
                });
                ((HomeDouFuKuaiLayout) convertView).setData(mArea, mImageLoader);
                break;
            case TYPE_NUTRALIFE://品质生活
                if (convertView == null) {
                    convertView = getHomeDouFuKuaiLayout();
                }
                ((HomeDouFuKuaiLayout) convertView).setCenterDivideVisibility(View.GONE);
                ((HomeDouFuKuaiLayout) convertView).setContextType(HomeDouFuKuaiLayout.CONTENT_TYPE_GRID);
                ((HomeDouFuKuaiLayout) convertView).setMoreType(HomeDouFuKuaiLayout.MORE_TYPE_BOTTON);
                ((HomeDouFuKuaiLayout) convertView).setMoreOnclickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(MoreTempidActivity.newIntent(mCatalog.tempid, mCatalog.cid, mCatalog.title));
                    }
                });
                ((HomeDouFuKuaiLayout) convertView).setData(mCatalog, mImageLoader);

                break;
            case TYPE_TAG://编辑精选
                if (convertView == null) {
                    convertView = getHomeDouFuKuaiLayout();
                }
                ((HomeDouFuKuaiLayout) convertView).setCenterDivideVisibility(View.GONE);
                ((HomeDouFuKuaiLayout) convertView).setContextType(HomeDouFuKuaiLayout.CONTENT_TYPE_GRID);
                ((HomeDouFuKuaiLayout) convertView).setMoreType(HomeDouFuKuaiLayout.MORE_TYPE_BOTTON);
                ((HomeDouFuKuaiLayout) convertView).setMoreOnclickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(MoreTempidActivity.newIntent(mTag.tempid, mTag.cid, mTag.title));
                    }
                });
                ((HomeDouFuKuaiLayout) convertView).setData(mTag, mImageLoader);

                break;
            case TYPE_BRANK://热门品牌
                if (convertView == null) {
                    convertView = new HomeDouFuKuaiLayout<HomeFindRespData.DatasItem>(mContext) {
                        @Override
                        public void addViewToLayout(LinearLayout layout, HomeFindRespData.DataInfo<HomeFindRespData.DatasItem> data, ImageLoader imageLoader) {

                        }

                        @Override
                        protected View getConvertView(int position, View convertView, ViewGroup parent, final HomeFindRespData.DatasItem item) {

                            Holder holder;
                            if (convertView == null) {
                                holder = new Holder();
                                convertView = View.inflate(mContext, R.layout.layout_pic_tt_lt, null);
                                holder.image = (ImageView) convertView.findViewById(R.id.image);
                                holder.bitTitle = (TextView) convertView.findViewById(R.id.title);
                                holder.smallTitle = (TextView) convertView.findViewById(R.id.desc);

                                convertView.setTag(holder);
//							convertView.setLayoutParams(mParams);
//							convertView.setPadding(0, mPadding, mPadding, 0);
                            } else {
                                holder = (Holder) convertView.getTag();
                            }


                            holder.bitTitle.setText(item.name);
                            holder.smallTitle.setText(item.text);
                            holder.image.setTag(item.image);
                            ListImageListener listener = new ListImageListener(holder.image, R.drawable.place_default, R.drawable.place_default, item.image);
                            mImageLoader.get(item.image, listener);

                            convertView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mContext.startActivity(TopicShowActivity.newIntent(item.tid));
                                }
                            });
                            return convertView;
                        }
                    };
                }
                ((HomeDouFuKuaiLayout) convertView).setCenterDivideVisibility(View.VISIBLE);
                ((HomeDouFuKuaiLayout) convertView).setContextType(HomeDouFuKuaiLayout.CONTENT_TYPE_GRID);
                ((HomeDouFuKuaiLayout) convertView).setMoreType(HomeDouFuKuaiLayout.MORE_TYPE_BOTTON);
                ((HomeDouFuKuaiLayout) convertView).setMoreOnclickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(MoreTempidActivity.newIntent(mBrand.tempid, mBrand.cid, mBrand.title));
                    }
                });
                ((HomeDouFuKuaiLayout) convertView).setData(mBrand, mImageLoader);
                break;
            default://晒晒列表
                if (convertView == null) {
                    convertView = new HomePageItem(mContext);
                }
                TypeData data = (TypeData) getItem(position);
                ((HomePageItem) convertView).setData((List<HomePageDatas.PostInfo>) data.obj, mImageLoader);
                break;
        }

        return convertView;
    }

    /**
     * 相似模块的抽取,在type=TYPE_TAG时 不显示朦层
     *
     * @return
     */
    private View getHomeDouFuKuaiLayout() {
        return new HomeDouFuKuaiLayout<HomeFindRespData.DatasItem>(mContext) {
            //该方法会在setdata方法调用后别执行
            @Override
            public void addViewToLayout(LinearLayout layout, HomeFindRespData.DataInfo<HomeFindRespData.DatasItem> data, ImageLoader imageLoader) {
            }

            @Override
            protected View getConvertView(int position, View convertView, ViewGroup parent, final HomeFindRespData.DatasItem item) {
                Holder holder;
                if (convertView == null) {
                    holder = new Holder();
                    convertView = View.inflate(mContext, R.layout.layout_pic_text, null);
                    holder.image = (ImageView) convertView.findViewById(R.id.image);
                    holder.line = convertView.findViewById(R.id.padding_top);
                    holder.bitTitle = (TextView) convertView.findViewById(R.id.big_title);
                    holder.smallTitle = (TextView) convertView.findViewById(R.id.small_title);
                    holder.layer = convertView.findViewById(R.id.layer);

                    convertView.setTag(holder);
                    convertView.setLayoutParams(mParams);
                    convertView.setPadding(0, mPadding, mPadding, 0);
                } else {
                    holder = (Holder) convertView.getTag();
                }

                //如果标题没有文字的话,就不显示朦层和标题,否则才显示
                if (TextUtils.isEmpty(item.name)) {
                    holder.bitTitle.setVisibility(GONE);
                    holder.smallTitle.setVisibility(GONE);
                    holder.layer.setVisibility(GONE);
                } else {
                    holder.bitTitle.setVisibility(VISIBLE);
                    holder.smallTitle.setVisibility(VISIBLE);
                    holder.layer.setVisibility(VISIBLE);
                    holder.smallTitle.setText(item.text);
                    holder.bitTitle.setText(item.name);
                }
//				holder.bitTitle.setText(item.name);
//				holder.smallTitle.setText(item.text);
                holder.image.setTag(item.image);
                ListImageListener listener = new ListImageListener(holder.image, R.drawable.place_default, R.drawable.place_default, item.image);
                mImageLoader.get(item.image, listener, mPoint.x, mPoint.y);

                convertView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(TopicShowActivity.newIntent(item.tid));
                    }
                });
                return convertView;
            }
        };

    }

    class Holder {
        ImageView image;
        View line;
        TextView bitTitle;
        TextView smallTitle;
        View layer;
    }


}
