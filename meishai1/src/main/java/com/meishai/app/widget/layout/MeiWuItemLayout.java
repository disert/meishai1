package com.meishai.app.widget.layout;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.StrategyResqData;
import com.meishai.entiy.StrategyResqData.StratData;
import com.meishai.entiy.UserInfo;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.meiwu.MeiWuCateDetailActivity;
import com.meishai.ui.fragment.meiwu.MeiWuGoodsDetailActivity;
import com.meishai.ui.fragment.meiwu.MeiWuSpecialShowActivity;
import com.meishai.ui.fragment.meiwu.MeiWuStratDetailActivity1;
import com.meishai.ui.fragment.meiwu.MeiWuStratListActivity;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.ImageAdapter;
import com.meishai.util.SkipUtils;

/**
 * 美物攻略-item 美物-专场详情-header
 *
 * @author Administrator yl
 */
public class MeiWuItemLayout extends RelativeLayout {

    public final static int TYPE_OTHER = 1;// 其他的,两者主页区别在于是显示喜欢按钮还是显示文字信息
    public final static int TYPE_DEFAULT = 0;// 默认的
    private int type;

    private ImageLoader mImageLoader;
    private Context mContext;
    private StratData mData;
    private ImageView mImage;
    private ImageView mIcon;
    private boolean scrollState;

    private LinearLayout mCenterContainer;
    private TextView mDesc;
    private TextView mSubTitle;
    private LinearLayout mTags;

    private LinearLayout mLike;
    private ImageView mLikeIcon;
    private TextView mLikeNum;

    private LinearLayout mbrowse;
    private ImageView mbrowseIcon;
    private TextView mbrowseNum;

    private ImageView mArrow;
    private LinearLayout mPics;
    private View mFramge;
    private View root;

    public MeiWuItemLayout(Context context) {
        this(context, null);
    }

    public MeiWuItemLayout(Context context, int type) {
        this(context);
        this.type = type;
    }

    public MeiWuItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeiWuItemLayout(Context context, AttributeSet attrs,
                           int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(context);
        initListener();
    }

    private void initListener() {
        // 美物-攻略-攻略详情
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                        tempid=1，是当前的攻略文章模式
//                        tempid=2，是当前的专场方块模式
//                        tempid=3，是新增的列表模式
//                        tempid=99，直接跳转到h5页面模式
//                SkipUtils.skipMeiwu(mContext,mData.tempid,mData.tid);
                switch (mData.tempid) {
                    case 99:
                        SkipUtils.skip(mContext, "h5", "", 0, mData.h5data);
                        break;
                    default:
                        SkipUtils.skipMeiwu(mContext, mData.tempid, mData.tid);
                        break;
                }

            }
        });

        // 关注按钮
        mLike.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 关注
                if (mData.isfollow == 1) {
                    unAttention();
                } else {
                    attention();
                }
            }
        });

    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView;

        // 美物-热门攻略-item部分
        convertView = inflater.inflate(R.layout.meiwu_item, this, true);

        root = convertView.findViewById(R.id.meiwu_item_root);
        root.setPadding(0, AndroidUtil.dip2px(4), 0, 0);
        mImage = (ImageView) convertView.findViewById(R.id.meiwu_item_image);
        Point point = ImageAdapter.getViewRealWH(1, 0, 750, 380);
        mImage.setLayoutParams(new FrameLayout.LayoutParams(point.x, point.y));
        mFramge = convertView.findViewById(R.id.meiwu_item_frame);
        mFramge.setLayoutParams(new FrameLayout.LayoutParams(point.x, point.y));
        mIcon = (ImageView) convertView.findViewById(R.id.meiwu_item_icon);

        mCenterContainer = (LinearLayout) convertView.findViewById(R.id.meiwu_item_center);
        mDesc = (TextView) convertView.findViewById(R.id.meiwu_item_desc);
        mSubTitle = (TextView) convertView.findViewById(R.id.meiwu_item_sub_title);
        mTags = (LinearLayout) convertView.findViewById(R.id.meiwu_item_tags);

        mLike = (LinearLayout) convertView.findViewById(R.id.meiwu_item_like);
        mLikeIcon = (ImageView) convertView.findViewById(R.id.meiwu_item_like_icon);
        mLikeNum = (TextView) convertView.findViewById(R.id.meiwu_item_like_num);

        mbrowse = (LinearLayout) convertView.findViewById(R.id.meiwu_item_browse);
        mbrowseIcon = (ImageView) convertView.findViewById(R.id.meiwu_item_browse_icon);
        mbrowseNum = (TextView) convertView.findViewById(R.id.meiwu_item_browse_num);

        mArrow = (ImageView) convertView.findViewById(R.id.meiwu_item_arraw);

        mPics = (LinearLayout) convertView.findViewById(R.id.meiwu_item_pics);

    }

    public void setRootPadding(int left, int top, int right, int bottom) {
        root.setPadding(left, top, right, bottom);
    }

    public void setData(StratData data, ImageLoader imageLoader) {
        if (mData == data) return;
        mImageLoader = imageLoader;
        mData = data;
        // 初始化数据


        //-----------icon-------------
        if (!TextUtils.isEmpty(mData.icon)) {//是否是最新
            mIcon.setVisibility(View.VISIBLE);
            mIcon.setTag(mData.icon);
            ListImageListener listener = new ListImageListener(mIcon, R.drawable.place_default, R.drawable.place_default, mData.icon);
            imageLoader.get(mData.icon, listener);
        } else {
            mIcon.setVisibility(View.GONE);
        }


        //-------------title----------
        if (TextUtils.isEmpty(mData.title)) {
            mCenterContainer.setVisibility(GONE);
            mArrow.setVisibility(GONE);
            mFramge.setVisibility(GONE);
        } else {
            mCenterContainer.setVisibility(VISIBLE);
            mFramge.setVisibility(VISIBLE);
            mDesc.setVisibility(VISIBLE);
            mDesc.setText(mData.title);

            if (TextUtils.isEmpty(mData.sub_title)) {
                mSubTitle.setVisibility(GONE);
            } else {
                mSubTitle.setVisibility(VISIBLE);
                mSubTitle.setText(mData.sub_title);
            }
            if (!TextUtils.isEmpty(mData.sub_title_color))
                mSubTitle.setTextColor(Color.parseColor("#" + mData.sub_title_color));

            //tags
            initTags();

        }

        //浏览和点赞
        if (mData.follow_num < 1) {
            mLike.setVisibility(INVISIBLE);
        } else {
            mLike.setVisibility(VISIBLE);
            mLikeNum.setText(mData.follow_num + "");//喜欢的数量
//            if (mData.isfollow == 1) {//喜欢状态
//                mLikeIcon.setImageResource(R.drawable.like_yellow);
//            } else {
//                mLikeIcon.setImageResource(R.drawable.like_white_);
//            }
        }
        if (mData.view_num < 1) {
            mbrowse.setVisibility(INVISIBLE);
        } else {
            mbrowse.setVisibility(VISIBLE);
            mbrowseNum.setText(mData.view_num + "");
        }
        //image
        //		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
        //				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        //		mImage.setLayoutParams(lp);
        mImage.setTag(mData.image);
        ListImageListener listener = new ListImageListener(mImage,
                R.drawable.place_default, R.drawable.place_default,
                mData.image);
        mImageLoader.get(mData.image, listener);

        //下面的图片
        initPics();


    }


    private void initPics() {
        if (mData.postlist == null || mData.postlist.isEmpty()) {
            mPics.setVisibility(GONE);
            mArrow.setVisibility(GONE);
        } else {
            mArrow.setVisibility(VISIBLE);
            mPics.setVisibility(VISIBLE);
            mPics.removeAllViews();
            if (mPics.getChildCount() == 0) {
                int padding = AndroidUtil.dip2px(5);
                int wh = ImageAdapter.getViewRealWH(3, padding);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(wh + padding, wh);
                for (int i = 0; i < mData.postlist.size(); i++) {
                    final StrategyResqData.PostPic pic = mData.postlist.get(i);
                    ImageView imageView = new ImageView(mContext);
                    imageView.setLayoutParams(params);
                    imageView.setPadding(padding, 0, 0, 0);


                    imageView.setTag(pic.pic_path);
                    ListImageListener listener = new ListImageListener(imageView, R.drawable.place_default, R.drawable.place_default, pic.pic_path);
                    mImageLoader.get(pic.pic_path, listener);
                    imageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mContext.startActivity(MeiWuGoodsDetailActivity.newIntent(pic.pid));
                        }
                    });

                    mPics.addView(imageView);
                }
            } else {

            }
        }
    }

    private void initTags() {
        if (mData.catlist != null && !mData.catlist.isEmpty()) {
            mTags.setVisibility(VISIBLE);
            mTags.removeAllViews();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int margin = AndroidUtil.dip2px(7);
            layoutParams.setMargins(margin, 0, margin, 0);
            for (int i = 0; i < mData.catlist.size(); i++) {
                final StrategyResqData.CateData cateData = mData.catlist.get(i);
                TextView tag = new TextView(mContext);
                tag.setBackgroundResource(R.drawable.round_back_selector);
                tag.setText(cateData.catname);
                tag.setTextColor(Color.WHITE);
                tag.setTextSize(10);
                tag.setLayoutParams(layoutParams);
                tag.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(MeiWuCateDetailActivity.newIntent(cateData.cid));
                    }
                });
                mTags.addView(tag);
            }
        } else {
            mTags.setVisibility(GONE);
        }

    }

    public void setScrollState(boolean scrollState) {
        this.scrollState = scrollState;
    }

    /**
     * 关注话题
     */
    private void attention() {

        // 没登陆,去登陆
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        if (!userInfo.isLogin()) {
            mContext.startActivity(LoginActivity.newOtherIntent());
            return;
        }
        // 弹出等待对话框
        String msg = mContext.getResources().getString(R.string.network_wait);
        final CustomProgress dlgProgress = CustomProgress.show(mContext, msg,
                true, null);
        MeiWuReq.attention(mData.tid, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                dlgProgress.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");
                    String tips = obj.getString("tips");
                    Toast.makeText(getContext(), tips, Toast.LENGTH_SHORT)
                            .show();
                    if (1 == success) {
                        mLikeIcon.setImageResource(R.drawable.like_yellow);
                        mData.isfollow = 1;
                        mData.follow_num++;
                        mLikeNum.setText(mData.follow_num + "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dlgProgress.dismiss();

                DebugLog.w(error.toString());
            }
        });

    }

    /**
     * 取消关注话题
     */
    private void unAttention() {

        // 没登陆,去登陆
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        if (!userInfo.isLogin()) {
            getContext().startActivity(LoginActivity.newOtherIntent());
            return;
        }
        // 弹出等待对话框
        String msg = getContext().getResources().getString(
                R.string.network_wait);
        final CustomProgress dlgProgress = CustomProgress.show(getContext(),
                msg, true, null);
        MeiWuReq.unAttention(mData.tid, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                dlgProgress.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");
                    String tips = obj.getString("tips");
                    Toast.makeText(getContext(), tips, Toast.LENGTH_SHORT)
                            .show();
                    if (1 == success) {
                        mLikeIcon.setImageResource(R.drawable.like_white_);
                        mData.isfollow = 0;
                        mData.follow_num--;
                        mLikeNum.setText(mData.follow_num + "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dlgProgress.dismiss();

                DebugLog.w(error.toString());
            }
        });

    }

}
