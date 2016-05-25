package com.meishai.app.widget.layout;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meishai.R;
import com.meishai.app.util.DensityUtils;
import com.meishai.app.widget.CircleImageView;
import com.meishai.app.widget.CustomProgress;
import com.meishai.app.widget.RoundCornerImageView;
import com.meishai.app.widget.gallery.GalleryAnimationActivity;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.BaseRespData;
import com.meishai.entiy.PostItem;
import com.meishai.lib.photoview.AnimationRect;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.camera.ReleaseActivity1;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.ui.fragment.home.HomeData;
import com.meishai.ui.fragment.home.HomePageActivity;
import com.meishai.ui.fragment.home.TopicShowActivity;
import com.meishai.ui.fragment.home.req.PostReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.StringUtils;

public class PostShowLinearLayout extends LinearLayout {

    private ImageLoader mImageLoader;
    private LinearLayout post_timeline_layout;
    private PostItem mData;

    private RoundCornerImageView avatar;
    private TextView username;
    private ImageView master;
    private ImageView vip;
    private TextView own;
    private TextView time;
    private ImageButton attention;

    private LinearLayout detailLayout;

    private TextView description;

    private LinearLayout picContainer;

    private RelativeLayout lay_loc;
    private TextView area;
    private Button mod, del;
    private View loc_line;
    private LinearLayout mCollectContainer, mLikeContainer, mLinkedContainer;// 浏览,点赞,评论数  喜欢(praisenum_ll),收藏(browsenum_ll)和链接(reviewnum_ll)
    private ImageView mCollectIV, mLikeIV, mLinkedIV;
    private TextView mCollectTV, mLikeTV, mLinkedTV;

    private LinearLayout cateContainer;

    private View praise_line;
    private LinearLayout praiseLayout;
    private ImageView praiseIv;

    private boolean mIsShow;

    private OnClickListener mOnDelClickListener;
    private Context mContext;
    private CustomProgress mProgressDialog;

    public PostShowLinearLayout(Context context, boolean isShow) {
        super(context);
        mIsShow = isShow;
        initView(context, isShow);
    }

    private void initView(Context context, boolean isShow) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mContext = context;
        View convertView = inflater.inflate(R.layout.post_timeline_show_item, this, true);
        convertView.findViewById(R.id.line).setVisibility(GONE);
        post_timeline_layout = (LinearLayout) convertView
                .findViewById(R.id.post_timeline_layout);
        post_timeline_layout.setVisibility(View.GONE);
        avatar = (RoundCornerImageView) convertView
                .findViewById(R.id.avatar_iv);
        username = (TextView) convertView.findViewById(R.id.username_tv);
        master = (ImageView) convertView.findViewById(R.id.master_iv);
        vip = (ImageView) convertView.findViewById(R.id.vip_iv);
        own = (TextView) convertView.findViewById(R.id.own_tv);
        time = (TextView) convertView.findViewById(R.id.time_tv);
        attention = (ImageButton) convertView.findViewById(R.id.attention_ib);

        detailLayout = (LinearLayout) convertView.findViewById(R.id.detail_llayout);

        if (isShow) {
            LinearLayout rootLl = (LinearLayout) convertView.findViewById(R.id.post_timeline_layout);
            LinearLayout.LayoutParams rootLp = (LinearLayout.LayoutParams) rootLl.getLayoutParams();
            rootLp.setMargins(0, 0, 0, 0);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) detailLayout.getLayoutParams();
            lp.setMargins(0, 0, 0, 0);
            lp.addRule(RelativeLayout.RIGHT_OF, 0);
            setBackgroundResource(R.color.white);


        } else {
            setBackgroundResource(R.drawable.sel_post_timeline);

        }

        description = (TextView) convertView.findViewById(R.id.desc_tv);
        if (isShow) {
            description.setMaxLines(Integer.MAX_VALUE);
        }

        picContainer = (LinearLayout) convertView.findViewById(R.id.ll_pic);

        cateContainer = (LinearLayout) convertView.findViewById(R.id.home_post_cate_container);

        lay_loc = (RelativeLayout) convertView.findViewById(R.id.lay_loc);
        area = (TextView) convertView.findViewById(R.id.area_tv);
        mod = (Button) convertView.findViewById(R.id.mod_btn);
        del = (Button) convertView.findViewById(R.id.del_btn);
        loc_line = convertView.findViewById(R.id.loc_line);

        mCollectContainer = (LinearLayout) convertView.findViewById(R.id.browsenum_ll);
        mLikeContainer = (LinearLayout) convertView.findViewById(R.id.praisenum_ll);
        mLinkedContainer = (LinearLayout) convertView.findViewById(R.id.reviewnum_ll);

        mCollectIV = (ImageView) convertView.findViewById(R.id.browsenum_iv);
        mLikeIV = (ImageView) convertView.findViewById(R.id.praisenum_iv);
        mLinkedIV = (ImageView) convertView.findViewById(R.id.reviewnum_iv);

        mLinkedTV = (TextView) convertView.findViewById(R.id.reviewnum_tv);
        mLikeTV = (TextView) convertView.findViewById(R.id.praisenum_tv);
        mCollectTV = (TextView) convertView.findViewById(R.id.browsenum_tv);

        praise_line = convertView.findViewById(R.id.praise_line);
        praiseLayout = (LinearLayout) convertView
                .findViewById(R.id.praise_img_layout);
        praiseIv = (ImageView) convertView.findViewById(R.id.praise_iv);

        initListener();
    }

    private void initListener() {


        // 点赞(喜欢)按钮的监听器
        mLikeContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 没登陆,去登陆
                if (!MeiShaiSP.getInstance().getUserInfo().isLogin()) {
                    getContext().startActivity(LoginActivity.newOtherIntent());
                    return;
                }
                if (mData == null || mData.iszan == 1) {
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
//				mContext.startActivity(MeishaiWebviewActivity.newIntent(mData.link));
                MeiWuReq.buyReq(mContext, mData.pid, mData.link, mData.istao);
            }
        });


    }

    public void setData(PostItem data, ImageLoader imageLoader) {
        if (null == data) {
            return;
        }

        mData = data;
        mImageLoader = imageLoader;
        updateUI();
    }

    /**
     * 设置删除帖子的监听
     *
     * @param l
     */
    public void setOnDeleteClickListener(OnClickListener l) {
        mOnDelClickListener = l;
    }

    private void updateUI() {
        post_timeline_layout.setVisibility(View.VISIBLE);
        avatar.setTag(mData.avatar);
        ListImageListener listener = new ListImageListener(avatar,
                R.drawable.head_default, R.drawable.head_default, mData.avatar);
        mImageLoader.get(mData.avatar, listener);

        avatar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HomePageActivity.class);
                intent.putExtra(ConstantSet.USERID, mData.userid);
                getContext().startActivity(intent);
            }
        });

        username.setText(mData.username);
        username.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HomePageActivity.class);
                intent.putExtra(ConstantSet.USERID, mData.userid);
                getContext().startActivity(intent);
            }
        });

        if (mData.isdaren == 1) {
            master.setVisibility(View.VISIBLE);
        } else {
            master.setVisibility(View.INVISIBLE);
        }

        if (mData.iswon == 1) {
            own.setVisibility(View.VISIBLE);
        } else {
            own.setVisibility(View.INVISIBLE);
        }

        setVipResId(mData.groupid);

        time.setText(mData.addtime);

        if (!TextUtils.isEmpty(mData.title)) {
            String des = "<font color='#FF5577'>#" + mData.title
                    + "#</font>&nbsp;&nbsp;" + mData.description;
            ClickableSpan clickSpan = new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    getContext().startActivity(
                            TopicShowActivity.newIntent(mData.tid));
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ds.linkColor);
                    ds.setUnderlineText(false);
                }
            };

            SpannableString sp = new SpannableString(Html.fromHtml(des));
            sp.setSpan(clickSpan, 0, mData.title.length() + 2,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            description.setLinkTextColor(0xffff5577);
            description.setMovementMethod(LinkMovementMethod.getInstance());
            description.setText(sp);

        } else {
            description.setText(mData.content);
        }

        addPictures();

        // 设置分类信息
        setCateUI();


        String id = MeiShaiSP.getInstance().getUserInfo().getUserID();

        long uid = Long.MIN_VALUE;
        try {
            uid = Double.valueOf(mData.userid).longValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if ((mData.isarea == 1 && !TextUtils.isEmpty(mData.areaname))
                || (null != id && (id.equals(mData.userid) || id.equals(uid
                + "")))) {
            loc_line.setVisibility(VISIBLE);
            lay_loc.setVisibility(VISIBLE);
        } else {
            loc_line.setVisibility(GONE);
            lay_loc.setVisibility(GONE);
        }

        if (mData.isarea == 1 && !TextUtils.isEmpty(mData.areaname)) {
            area.setText(mData.areaname);
            area.setVisibility(VISIBLE);
        } else {
            area.setVisibility(GONE);
            area.setText("");
        }

        if (null != id && (id.equals(mData.userid) || id.equals(uid + ""))) {
            mod.setVisibility(View.VISIBLE);
            mod.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    getContext().startActivity(
                            //TODO 修改帖子时调用的发布界面
//							CameraActivity1.newPostIntent(mData.pid));
                            ReleaseActivity1.newPostIntent(mData.pid));
                }
            });
            del.setVisibility(View.VISIBLE);
            del.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mOnDelClickListener != null) {
                        mOnDelClickListener.onClick(v);
                    }
                }
            });

        } else {
            mod.setVisibility(View.GONE);
            del.setVisibility(View.GONE);
        }

        if (StringUtils.isNotBlank(MeiShaiSP.getInstance().getUserInfo()
                .getUserID())
                && (TextUtils.equals(mData.userid, MeiShaiSP.getInstance()
                .getUserInfo().getUserID()) || MeiShaiSP.getInstance()
                .getUserInfo().getUserID().equals(uid + ""))) {
            attention.setVisibility(View.INVISIBLE);

        } else if (mData.isattention == 1) {
            attention.setVisibility(View.VISIBLE);
            attention.setImageResource(R.drawable.ic_attention_yes);
            attention.setClickable(false);
        } else {
            attention.setVisibility(View.VISIBLE);
            attention.setImageResource(R.drawable.ic_attention_no);
            attention.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    HomeData.getInstance().reqAttention(getContext(),
                            mData.userid, new Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject obj = new JSONObject(
                                                response);
                                        int success = obj.getInt("success");
                                        if (HomeData.RET_UNLOGIN == success) {
                                            String tips = obj.getString("tips");
                                            Toast.makeText(getContext(), tips,
                                                    Toast.LENGTH_SHORT).show();
                                            attention.setClickable(true);

                                            getContext().startActivity(
                                                    LoginActivity
                                                            .newOtherIntent());

                                        } else {
                                            attention
                                                    .setImageResource(R.drawable.ic_attention_yes);
                                            attention.setClickable(false);
                                            mData.isattention = 1;
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

        //TODO

        //收藏
        mCollectTV.setText(mData.fav_num == 0 ? "收藏" : mData.fav_num + "");
        if (mData.isfav == 1) {
            mCollectContainer.setClickable(false);
            mCollectIV.setSelected(true);

        } else {
            mCollectContainer.setClickable(true);
            mCollectIV.setSelected(false);

        }

        //喜欢
        mLikeTV.setText(String.valueOf(mData.zan_num == 0 ? "赞" : mData.zan_num));
        if (mData.iszan == 1) {
            mLikeContainer.setClickable(false);
            mLikeIV.setSelected(true);
        } else {
            mLikeContainer.setClickable(true);
            mLikeIV.setSelected(false);
        }

        //链接
        if (mData.islink == 1) {
            mLinkedTV.setText("直达链接");
            mLinkedTV.setTextColor(0xFFFFCC00);
            mLinkedContainer.setClickable(true);
            mLinkedIV.setSelected(true);

        } else {
            mLinkedTV.setText("暂无链接");
            mLinkedTV.setTextColor(0xffcdcdcd);
            mLinkedContainer.setClickable(false);
            mLinkedIV.setSelected(false);

        }

        setPraiseImage();
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
            for (int i = 0; i < mData.topicdata.size(); i++) {
                TextView tv = new TextView(mContext);
                //				tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setTextColor(0xffff5577);
                tv.setTextSize(12);
                tv.setPadding(AndroidUtil.dip2px(8), 0, AndroidUtil.dip2px(12), 0);
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

    public void setCommentPop(OnClickListener clickListener) {
//		reviewnum.setOnClickListener(clickListener);
    }

    private Object tag = new Object();

    private void setPraiseImage() {
        praiseLayout.setTag(null);
        if (null == mData.zuser || mData.zuser.size() == 0) {
            praise_line.setVisibility(View.GONE);
            praiseLayout.setVisibility(View.GONE);
        } else {
            praise_line.setVisibility(View.VISIBLE);
            praiseLayout.setVisibility(View.VISIBLE);
        }

        if (null == mData || null == mData.zuser) {
            return;
        }
        praiseLayout.setTag(tag);
        View v = praiseLayout.getChildAt(0);
        praiseLayout.removeAllViews();
        praiseLayout.addView(v);

        int gap = 5;
        int dia = DensityUtils.dp2px(getContext(), 25);
        ;

        LayoutParams lp = new LayoutParams(dia, dia);
        lp.setMargins(gap, 0, gap, 0);

        for (int i = 0; i < mData.zuser.size(); i++) {
            CircleImageView image = new CircleImageView(getContext());
            image.setLayoutParams(lp);
            final String usrid = mData.zuser.get(i).userid;
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

            image.setTag(mData.zuser.get(i).avatar);
            ListImageListener listener = new ListImageListener(image, 0, 0,
                    mData.zuser.get(i).avatar);
            if (mImageLoader == null) {
                throw new RuntimeException("mImageLoader is null");
            }

            if (mData.zuser == null) {
                throw new RuntimeException("zuser is null");
            }
            if (mData.zuser.get(i).avatar == null) {
                throw new RuntimeException("avater is null:" + i);
            }

            mImageLoader.get(mData.zuser.get(i).avatar, listener);

        }

        //
        // ViewTreeObserver vto2 = praiseIv.getViewTreeObserver();
        // vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
        // @Override
        // synchronized public void onGlobalLayout() {
        // if(praiseLayout.getTag() != tag) {
        // return;
        // }
        //
        // praiseLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        // int gap = 5;
        // int dia = DensityUtils.dp2px(getContext(), 25);;
        // int width = praiseLayout.getWidth();
        //
        // LayoutParams lp = new LayoutParams(dia, dia);
        // lp.setMargins(gap, 0, gap, 0);
        // int left = width - praiseIv.getWidth() - gap;
        //
        // // DebugLog.d("layout:" + width + ",image:" + praiseIv.getWidth());
        //
        // if(null != mData.zuser) {
        // for(int i = 0; i < mData.zuser.size(); i++) {
        // if(left < dia + gap) {
        // break;
        // }
        // CircleImageView image = new CircleImageView(getContext());
        // image.setLayoutParams(lp);
        // final String usrid = mData.zuser.get(i).userid;
        // image.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // Intent intent = new Intent(getContext(), HomePageActivity.class);
        // intent.putExtra(ConstantSet.USERID, usrid);
        // getContext().startActivity(intent);
        // }
        // });
        //
        // praiseLayout.addView(image);
        //
        // image.setTag(mData.zuser.get(i).avatar);
        // ListImageListener listener = new ListImageListener(
        // image, 0,
        // 0, mData.zuser.get(i).avatar);
        // if(mImageLoader == null) {
        // throw new RuntimeException("mImageLoader is null");
        // }
        //
        // if(mData.zuser == null) {
        // throw new RuntimeException("zuser is null");
        // }
        // if(mData.zuser.get(i).avatar == null) {
        // throw new RuntimeException("avater is null:" + i);
        // }
        //
        // mImageLoader.get(mData.zuser.get(i).avatar, listener);
        //
        // left -= (dia + 2 * gap);
        // }
        // }
        // }
        // });

    }

    private void setVipResId(int groupId) {
        switch (groupId) {
            case 1:
                vip.setImageResource(R.drawable.v1);
                break;
            case 2:
                vip.setImageResource(R.drawable.v2);
                break;
            case 3:
                vip.setImageResource(R.drawable.v3);
                break;
            case 4:
                vip.setImageResource(R.drawable.v4);
                break;
            case 5:
                vip.setImageResource(R.drawable.v5);
                break;
            case 6:
                vip.setImageResource(R.drawable.v6);
                break;
            case 7:
                vip.setImageResource(R.drawable.v7);
                break;
            default:
                vip.setVisibility(View.INVISIBLE);
                return;
        }
        vip.setVisibility(View.VISIBLE);
    }

    private void addPictures() {
        picContainer.removeAllViews();
        if (mData.pics == null) {
            return;
        }

        for (int i = 0; i < mData.pics.size(); i++) {
            SampleRoundImageView view = new SampleRoundImageView(getContext());
            ImageView iv = view.getImageView();
            iv.setAdjustViewBounds(true);
            iv.setScaleType(ScaleType.CENTER_CROP);
            iv.setBackgroundColor(getResources().getColor(R.color.post_pic_back));
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//			lp.topMargin = AndroidUtil.dip2px(2);
            lp.bottomMargin = AndroidUtil.dip2px(5);
            view.setLayoutParams(lp);

            iv.setTag(mData.pics.get(i).url);
            ListImageListener listener = new ListImageListener(iv, R.drawable.place_default, R.drawable.place_default, mData.pics.get(i).url);
            mImageLoader.get(mData.pics.get(i).url, listener);

            final int index = i;
            iv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ArrayList<AnimationRect> list = new ArrayList<AnimationRect>();
                    for (int i = 0; i < picContainer.getChildCount(); i++) {
                        ImageView image = ((SampleRoundImageView) picContainer.getChildAt(i)).getImageView();
                        AnimationRect rect = AnimationRect.buildFromImageView(image);
                        list.add(rect);
                    }

                    Intent intent = GalleryAnimationActivity.newIntent(mData.pid, list, index);
                    getContext().startActivity(intent);

                }
            });

            picContainer.addView(view);
        }
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
                                    .valueOf(++mData.fav_num));
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
                                            .valueOf(++mData.zan_num));
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

