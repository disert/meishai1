package com.meishai.app.widget.layout;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meishai.R;
import com.meishai.app.util.DensityUtils;
import com.meishai.app.widget.CircleImageView;
import com.meishai.app.widget.CustomProgress;
import com.meishai.app.widget.RoundCornerImageView;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.BaseRespData;
import com.meishai.entiy.ItemInfo;
import com.meishai.entiy.PostItem;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ImageLoader.ImageListener;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.ui.fragment.home.HomeData;
import com.meishai.ui.fragment.home.HomePageActivity;
import com.meishai.ui.fragment.home.PostShowActivity;
import com.meishai.ui.fragment.home.TopicShowActivity;
import com.meishai.ui.fragment.home.req.PostReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.StringUtils;

/**
 * 主页普通信息所对应的view
 *
 * @author Administrator yl
 */
public class PostTimeLineLayout1 extends LinearLayout {

    private ImageLoader mImageLoader;// 加载器
    private ItemInfo mData;// 数据

    private LinearLayout post_timeline_layout;// 根布局

    private RoundCornerImageView avatar;// 头像
    private TextView username;// 用户名
    private TextView time; // 发布时间
    private ImageView vip;
    private ImageView attention; // 关注

    private ImageView image;// 图片
    private TextView description;// 描述


    private RelativeLayout lay_loc;// 位置根布局
    private TextView area;// 位置
    private Button mod, del;// 删除,修改
    private View loc_line;// 位置处的分割线

    private LinearLayout mCollectContainer, mLikeContainer, mLinkedContainer;// 浏览,点赞,评论数  喜欢(praisenum_ll),收藏(browsenum_ll)和链接(reviewnum_ll)
    private ImageView mCollectIV, mLikeIV, mLinkedIV;
    private TextView mCollectTV, mLikeTV, mLinkedTV;

    private LinearLayout detailLayout;

    // 点赞列表信息
    private View praise_line;// 分割线
    private LinearLayout praiseLayout;// 头像容器
    private ImageView praiseIcon;// 点赞图标

    private Object tag = new Object();
    private boolean mIsShow;// 构造传入的参数,是否显示

    private OnClickListener mOnDelClickListener;
    private Context mContext;
    private LinearLayout cateContainer;
    private ImageView master;
    private CustomProgress mProgressDialog;

    public PostTimeLineLayout1(Context context, boolean isShow) {
        super(context);
        mIsShow = isShow;
        mContext = context;
        initView(context, isShow);
    }

    private void initView(Context context, boolean isShow) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.post_timeline_item1, this,
                true);
        // 第一行
        avatar = (RoundCornerImageView) convertView
                .findViewById(R.id.avatar_iv);
        master = (ImageView) convertView.findViewById(R.id.master_iv);
        vip = (ImageView) convertView.findViewById(R.id.vip_iv);
        username = (TextView) convertView.findViewById(R.id.nicename_tv);
        time = (TextView) convertView.findViewById(R.id.time_tv);
        attention = (ImageView) convertView.findViewById(R.id.attention_ib);

        // 图片
        image = (ImageView) convertView.findViewById(R.id.home_post_image);

        // 描述文字
        description = (TextView) convertView.findViewById(R.id.desc_tv);

        cateContainer = (LinearLayout) convertView.findViewById(R.id.home_post_cate_container);

        // 地理信息
        lay_loc = (RelativeLayout) convertView.findViewById(R.id.lay_loc);
        area = (TextView) convertView.findViewById(R.id.area_tv);
        mod = (Button) convertView.findViewById(R.id.mod_btn);
        del = (Button) convertView.findViewById(R.id.del_btn);
        // 地理位置下的分割线
        loc_line = convertView.findViewById(R.id.loc_line);

        // 浏览,评论,点赞数信息
        //该部分功能先改为喜欢(praisenum_ll),收藏(browsenum_ll)和链接(reviewnum_ll)
        mCollectContainer = (LinearLayout) convertView.findViewById(R.id.browsenum_ll);
        mLikeContainer = (LinearLayout) convertView.findViewById(R.id.praisenum_ll);
        mLinkedContainer = (LinearLayout) convertView.findViewById(R.id.reviewnum_ll);

        mCollectIV = (ImageView) convertView.findViewById(R.id.browsenum_iv);
        mLikeIV = (ImageView) convertView.findViewById(R.id.praisenum_iv);
        mLinkedIV = (ImageView) convertView.findViewById(R.id.reviewnum_iv);

        mLinkedTV = (TextView) convertView.findViewById(R.id.reviewnum_tv);
        mLikeTV = (TextView) convertView.findViewById(R.id.praisenum_tv);
        mCollectTV = (TextView) convertView.findViewById(R.id.browsenum_tv);

        // 点赞行的信息
        praise_line = convertView.findViewById(R.id.praise_line);
        praiseLayout = (LinearLayout) convertView
                .findViewById(R.id.praise_img_layout);
        praiseIcon = (ImageView) convertView.findViewById(R.id.praise_iv);

        // 根布局
        post_timeline_layout = (LinearLayout) convertView
                .findViewById(R.id.post_timeline_layout);
        post_timeline_layout.setVisibility(View.GONE);

        detailLayout = (LinearLayout) convertView
                .findViewById(R.id.detail_llayout);

        if (isShow) {
            LinearLayout.LayoutParams rootLp = (LayoutParams) post_timeline_layout
                    .getLayoutParams();
            rootLp.setMargins(0, 0, 0, 0);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) detailLayout
                    .getLayoutParams();
            lp.setMargins(0, 0, 0, 0);
            // lp.addRule(RelativeLayout.RIGHT_OF, 0);
            setBackgroundResource(R.color.white);

        } else {
            setBackgroundResource(R.drawable.sel_post_timeline);

        }

        if (isShow) {
            description.setMaxLines(Integer.MAX_VALUE);
        }

        initListener();
    }

    private void initListener() {
        // 自己的点击事件
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 进入帖子的详情页面
                startPostShow(PostShowActivity.FROM_POST);
            }
        });

        // 点赞(喜欢)按钮的监听器
        mLikeContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 没登陆,去登陆
                if (!MeiShaiSP.getInstance().getUserInfo().isLogin()) {
                    getContext().startActivity(LoginActivity.newOtherIntent());
                    return;
                }
                if (mData == null || mData.postdata == null || mData.postdata.iszan == 1) {
                    return;
                } else {
                    like();
                }

            }
        });
        // 收藏的点击事件 browsenum_ll
        mCollectContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MeiShaiSP.getInstance().getUserInfo().isLogin()) {
                    if (mData != null) {
                        fav();
                    }
                } else {
                    getContext().startActivity(LoginActivity.newOtherIntent());
                }
            }
        });
        // 点击链接的事件 reviewnum_ll
        mLinkedContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 没有pid 最后不是跳web就是淘宝的
                //                mContext.startActivity(MeishaiWebviewActivity.newIntent(mData.postdata.link));
                if (mData.postdata.islink == 1) {
                    MeiWuReq.buyReq(mContext, mData.pid, mData.postdata.link, mData.postdata.istao);
                }
            }
        });


        // 头像点击事件
        avatar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HomePageActivity.class);
                intent.putExtra(ConstantSet.USERID, mData.userdata.userid);
                getContext().startActivity(intent);
            }
        });

        // 与头像点击事件一样
        username.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HomePageActivity.class);
                intent.putExtra(ConstantSet.USERID, mData.userdata.userid);
                getContext().startActivity(intent);
            }
        });

        // 描述文本的事件
        description.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startPostShow(PostShowActivity.FROM_POST);
            }
        });

        // 关注事件
        attention.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 发送一个关注请求
                HomeData.getInstance().reqAttention(getContext(),
                        mData.userdata.userid, new Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                // 处理关注请求的结果
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    int success = obj.getInt("success");
                                    String tips = obj.getString("tips");
                                    Toast.makeText(getContext(), tips,
                                            Toast.LENGTH_SHORT).show();

                                    if (HomeData.RET_UNLOGIN == success) {
                                        attention.setClickable(true);

                                        getContext().startActivity(
                                                LoginActivity.newOtherIntent());

                                    } else {
                                        attention
                                                .setImageResource(R.drawable.ic_attention_yes);
                                        attention.setClickable(false);
                                        mData.userdata.isattention = 1;
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }, new ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                DebugLog.d(error.toString());
                                attention.setClickable(true);
                            }

                        });
            }
        });
    }


    public void setData(ItemInfo data, ImageLoader imageLoader) {
        if (null == data) {
            return;
        }

        mData = data;
        mImageLoader = imageLoader;
        updateUI();
    }

    public void setOnDeleteClickListener(OnClickListener l) {
        mOnDelClickListener = l;
    }

    private void updateUI() {
        post_timeline_layout.setVisibility(View.VISIBLE);
        // 加载头像
        avatar.setImageResource(R.drawable.head_default);
        avatar.setTag(mData.userdata.avatar);
        ListImageListener listener = new ListImageListener(avatar,
                R.drawable.head_default, R.drawable.head_default,
                mData.userdata.avatar);
        mImageLoader.get(mData.userdata.avatar, listener);
        if (mData.userdata.isdaren == 1) {
            master.setVisibility(View.VISIBLE);
        } else {
            master.setVisibility(View.INVISIBLE);
        }

        // 设置昵称和发布时间的信息
        time.setText(mData.postdata.addtime);
        username.setText(mData.userdata.username);
        setVipResId(mData.userdata.group_name);

        // 设置描述文本
        description.setText(mData.postdata.description);

        // 设置图片
        int pic_height = mData.postdata.pic_height;
        int pic_width = mData.postdata.pic_width;
        if (pic_height > pic_width) {//如果图片的高度大于宽度设置宽高一样
            DisplayMetrics metric = new DisplayMetrics();
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metric);
            int width = metric.widthPixels;     // 屏幕宽度（像素）
            LinearLayout.LayoutParams layoutParams = new LayoutParams(width, width);
            image.setLayoutParams(layoutParams);
        } else {
            LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            image.setLayoutParams(layoutParams);
        }
        image.setImageResource(R.drawable.place_default);
        ImageListener listener1 = ImageLoader.getImageListener(image,
                R.drawable.place_default, R.drawable.place_default);
        mImageLoader.get(mData.postdata.pic_url, listener1);

        // 设置分类信息
        setCateUI();


        // 设置位置信息

        // 获得两id
        String id = MeiShaiSP.getInstance().getUserInfo().getUserID();
        long uid = Long.MIN_VALUE;
        try {
            uid = Double.valueOf(mData.userdata.userid).longValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        lay_loc.setVisibility(GONE);
        loc_line.setVisibility(GONE);


        // 设置关注按钮
        // 如果用户id存在,并且是自己,隐藏关注
        if (StringUtils.isNotBlank(MeiShaiSP.getInstance().getUserInfo()
                .getUserID())
                && (TextUtils.equals(mData.userdata.userid, MeiShaiSP
                .getInstance().getUserInfo().getUserID()) || MeiShaiSP
                .getInstance().getUserInfo().getUserID()
                .equals(uid + ""))) {
            attention.setVisibility(View.INVISIBLE);

        } else if (mData.userdata.isattention == 1) {// 如果已经关注了
            attention.setVisibility(View.VISIBLE);
            attention.setImageResource(R.drawable.ic_attention_yes);
            attention.setClickable(false);
        } else {// 没关注
            attention.setVisibility(View.VISIBLE);
            attention.setImageResource(R.drawable.ic_attention_no);
            attention.setClickable(true);

        }

        // 设置浏览数量那一排的信息
        //收藏
        mCollectTV.setText(mData.postdata.fav_num == 0 ? "收藏" : mData.postdata.fav_num + "");
        if (mData.postdata.isfav == 1) {
            mCollectContainer.setClickable(false);
            mCollectIV.setSelected(true);

        } else {
            mCollectContainer.setClickable(true);
            mCollectIV.setSelected(false);

        }

        //喜欢
        mLikeTV.setText(String.valueOf(mData.postdata.zan_num == 0 ? "赞" : mData.postdata.zan_num));
        if (mData.postdata.iszan == 1) {
            mLikeContainer.setClickable(false);
            mLikeIV.setSelected(true);
        } else {
            mLikeContainer.setClickable(true);
            mLikeIV.setSelected(false);
        }

        //链接
        if (mData.postdata.islink == 1) {
            mLinkedTV.setText("直达链接");
            mLinkedTV.setTextColor(0xFFFFCC00);
            //            mLinkedContainer.setClickable(true);
            mLinkedIV.setSelected(true);

        } else {
            mLinkedTV.setText("暂无链接");
            mLinkedTV.setTextColor(0xffcdcdcd);
            //            mLinkedContainer.setClickable(false);
            mLinkedIV.setSelected(false);

        }

        // 设置赞那一排的数据
        //		setPraiseImage();
    }

    private void setCateUI() {
        if (mData == null || mData.topicdata == null) {
            return;
        }

        if (mData.topicdata.size() > 0) {
            cateContainer.setVisibility(VISIBLE);
            LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            ImageView tagView = (ImageView) cateContainer.getChildAt(0);
            cateContainer.removeAllViews();
            cateContainer.addView(tagView);
            int padding = AndroidUtil.dip2px(8);
            for (int i = 0; i < mData.topicdata.size(); i++) {
                TextView tv = new TextView(mContext);
                //				tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setTextColor(0xffff5577);
                tv.setTextSize(12);
                tv.setPadding(padding, 0, padding, 0);
                tv.setText(mData.topicdata.get(i).title);
                tv.setLayoutParams(lp);
                final int tid = mData.topicdata.get(i).tid;
                tv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = TopicShowActivity.newIntent(tid);
                        mContext.startActivity(intent);
                    }
                });
                cateContainer.addView(tv);
            }

        } else {
            cateContainer.setVisibility(GONE);
        }
    }

    private void setVipResId(String groupname) {
        if (groupname.equals("V1会员")) {
            vip.setImageResource(R.drawable.v1);
        } else if (groupname.equals("V2会员")) {
            vip.setImageResource(R.drawable.v2);
        } else if (groupname.equals("V3会员")) {
            vip.setImageResource(R.drawable.v3);
        } else if (groupname.equals("V4会员")) {
            vip.setImageResource(R.drawable.v4);
        } else if (groupname.equals("V5会员")) {
            vip.setImageResource(R.drawable.v5);
        } else if (groupname.equals("V6会员")) {
            vip.setImageResource(R.drawable.v6);
        } else if (groupname.equals("V6会员")) {
            vip.setImageResource(R.drawable.v7);
        } else {
            vip.setVisibility(View.INVISIBLE);
            return;
        }
        vip.setVisibility(View.VISIBLE);
    }

    public void setCommentPop(OnClickListener clickListener) {
        mLinkedContainer.setOnClickListener(clickListener);
    }

    private void setPraiseImage() {
        praiseLayout.setTag(null);
        if (null == mData.zuserdata || mData.zuserdata.size() == 0) {
            praise_line.setVisibility(View.GONE);
            praiseLayout.setVisibility(View.GONE);
        } else {
            praise_line.setVisibility(View.VISIBLE);
            praiseLayout.setVisibility(View.VISIBLE);
        }

        if (null == mData || null == mData.zuserdata) {
            return;
        }
        praiseLayout.setTag(tag);
        // 先清空一下view
        View v = praiseLayout.getChildAt(0);
        praiseLayout.removeAllViews();
        praiseLayout.addView(v);

        int gap = 5;
        int dia = DensityUtils.dp2px(getContext(), 25);

        LayoutParams lp = new LayoutParams(dia, dia);
        lp.setMargins(gap, 0, gap, 0);

        // 初始化赞
        for (int i = 0; i < mData.zuserdata.size(); i++) {
            CircleImageView image = new CircleImageView(getContext());
            image.setLayoutParams(lp);
            final String usrid = mData.zuserdata.get(i).userid;
            image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(),
                            HomePageActivity.class);
                    intent.putExtra(ConstantSet.USERID, usrid);
                    getContext().startActivity(intent);
                }
            });

            praiseLayout.addView(image);
            // 加载图片
            image.setTag(mData.zuserdata.get(i).avatar);
            ListImageListener listener = new ListImageListener(image, 0, 0,
                    mData.zuserdata.get(i).avatar);
            if (mImageLoader == null) {
                throw new RuntimeException("mImageLoader is null");
            }

            if (mData.zuserdata == null) {
                throw new RuntimeException("zuser is null");
            }
            if (mData.zuserdata.get(i).avatar == null) {
                throw new RuntimeException("avater is null:" + i);
            }

            mImageLoader.get(mData.zuserdata.get(i).avatar, listener);

        }

    }

    private void startPostShow(int from) {
        PostItem postItem = new PostItem();
        postItem.pid = mData.pid;
        getContext().startActivity(PostShowActivity.newIntent(postItem, from));

    }


    /**
     * 收藏的请求
     */
    private void fav() {
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress.show(mContext,
                    mContext.getString(R.string.network_wait), true, null);
        } else {
            mProgressDialog.show();
        }

        PostReq.fav1(mContext, mData.pid, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                DebugLog.d("收藏:" + response);
                if (response != null) {
                    BaseRespData respData = GsonHelper.parseObject(response, BaseRespData.class);
                    if (respData != null) {
                        if (respData.success == 1) {
                            mCollectTV.setText(String
                                    .valueOf(++mData.postdata.fav_num));
                            mCollectIV.setSelected(true);
                            mCollectTV.setTextColor(0xff999999);
                            mCollectContainer.setClickable(false);
                        }
                        AndroidUtil.showToast(respData.tips);
                    }
                } else {
                    AndroidUtil.showToast("返回数据为null");
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    /**
     * 喜欢的请求
     */
    private void like() {
        // 弹出等待对话框
        mLikeContainer.setClickable(false);
        String msg = getContext().getResources().getString(
                R.string.network_wait);
        final CustomProgress dlgProgress = CustomProgress.show(
                getContext(), msg, true, null);

        HomeData.getInstance().reqZan(mData.pid,
                new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // 关闭进度条
                        dlgProgress.dismiss();

                        DebugLog.d(response);
                        // 对点赞的响应进行处理
                        if (response != null) {
                            BaseRespData respData = GsonHelper.parseObject(response, BaseRespData.class);
                            if (respData != null) {
                                if (respData.success == 1) {
                                    mLikeTV.setText(String
                                            .valueOf(++mData.postdata.zan_num));
                                    mLikeIV.setSelected(true);
                                    mLikeTV.setTextColor(0xff999999);
                                    mLikeContainer.setClickable(false);
                                }
                                AndroidUtil.showToast(respData.tips);
                            }
                        } else {
                            AndroidUtil.showToast("返回数据为null");
                        }
                    }
                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dlgProgress.dismiss();

                        DebugLog.d(error.toString());
                        mLikeContainer.setClickable(true);
                    }

                });
    }
}
