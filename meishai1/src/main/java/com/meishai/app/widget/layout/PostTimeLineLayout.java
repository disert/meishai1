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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.meishai.app.widget.ScrollGridView;
import com.meishai.app.widget.ScrollGridView.OnTouchInvalidPositionListener;
import com.meishai.app.widget.gallery.GalleryAnimationActivity;
import com.meishai.app.widget.layout.PicAdapter.PicViewHolder;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.PostItem;
import com.meishai.entiy.PostItem.ZanUserInfo;
import com.meishai.lib.photoview.AnimationRect;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.camera.CameraActivity;
import com.meishai.ui.fragment.home.HomeData;
import com.meishai.ui.fragment.home.HomePageActivity;
import com.meishai.ui.fragment.home.PostShowActivity;
import com.meishai.ui.fragment.home.TopicShowActivity;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.DebugLog;
import com.meishai.util.StringUtils;

public class PostTimeLineLayout extends LinearLayout {

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
    private ScrollGridView picGrid;
    private RelativeLayout lay_loc;
    private TextView area;
    private Button mod, del;
    private View loc_line;
    private Button browsenum, praisenum, reviewnum;

    private View praise_line;
    private LinearLayout praiseLayout;
    private ImageView praiseIv;

    private boolean mIsShow;

    private OnClickListener mOnDelClickListener;

    public PostTimeLineLayout(Context context, boolean isShow) {
        super(context);
        mIsShow = isShow;
        initView(context, isShow);
    }

    private void initView(Context context, boolean isShow) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.post_timeline_item, this,
                true);
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

        picGrid = (ScrollGridView) convertView.findViewById(R.id.pic_gridview);

        lay_loc = (RelativeLayout) convertView.findViewById(R.id.lay_loc);
        area = (TextView) convertView.findViewById(R.id.area_tv);
        mod = (Button) convertView.findViewById(R.id.mod_btn);
        del = (Button) convertView.findViewById(R.id.del_btn);
        loc_line = convertView.findViewById(R.id.loc_line);
        browsenum = (Button) convertView.findViewById(R.id.browsenum_btn);
        praisenum = (Button) convertView.findViewById(R.id.praisenum_btn);
        reviewnum = (Button) convertView.findViewById(R.id.reviewnum_btn);

        praise_line = convertView.findViewById(R.id.praise_line);
        praiseLayout = (LinearLayout) convertView
                .findViewById(R.id.praise_img_layout);
        praiseIv = (ImageView) convertView.findViewById(R.id.praise_iv);

        initListener();
    }

    private void initListener() {
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startPostShow(PostShowActivity.FROM_POST);
            }
        });

        picGrid.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                DebugLog.d("click:" + position);
            }
        });

        picGrid.setOnTouchInvalidPositionListener(new OnTouchInvalidPositionListener() {

            @Override
            public boolean onTouchInvalidPosition(int motionEvent) {
                DebugLog.d("onTouchInvalidPosition");
                return false;
            }
        });

        praisenum.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!MeiShaiSP.getInstance().getUserInfo().isLogin()) {
                    getContext().startActivity(LoginActivity.newOtherIntent());
                    return;
                }

                praisenum.setClickable(false);
                String msg = getContext().getResources().getString(
                        R.string.network_wait);
                final CustomProgress dlgProgress = CustomProgress.show(
                        getContext(), msg, true, null);

                HomeData.getInstance().reqZan(mData.pid,
                        new Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                dlgProgress.dismiss();

                                DebugLog.d(response);
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    int success = obj.getInt("success");
                                    String tips = obj.getString("tips");
                                    Toast.makeText(getContext(), tips,
                                            Toast.LENGTH_SHORT).show();
                                    if (HomeData.RET_ERROR == success) {
//										String tips = obj.getString("tips");
//										Toast.makeText(getContext(), tips,
//												Toast.LENGTH_SHORT).show();

                                    } else if (HomeData.RET_UNLOGIN == success) {
//										String tips = obj.getString("tips");
//										Toast.makeText(getContext(), tips,
//												Toast.LENGTH_SHORT).show();
                                        praisenum.setClickable(true);

                                        getContext().startActivity(
                                                LoginActivity.newOtherIntent());

                                    } else {
                                        mData.zan_num++;
                                        ZanUserInfo info = new ZanUserInfo();
                                        info.userid = obj.getString("userid");
                                        info.avatar = obj.getString("avatar");
                                        if (info.avatar == null) {
                                            throw new RuntimeException(
                                                    "avater is null");
                                        }

                                        if (null == mData.zuser) {
                                            mData.zuser = new ArrayList<PostItem.ZanUserInfo>();
                                        }
                                        mData.zuser.add(0, info);

                                        praisenum.setText(String
                                                .valueOf(mData.zan_num));
                                        praisenum
                                                .setCompoundDrawablesWithIntrinsicBounds(
                                                        R.drawable.ic_praised,
                                                        0, 0, 0);
                                        praisenum.setTextColor(getContext()
                                                .getResources().getColor(
                                                        R.color.txt_color));
                                        praiseLayout.setTag(null);
                                        setPraiseImage();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    praisenum.setClickable(true);
                                }
                            }
                        }, new ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dlgProgress.dismiss();

                                DebugLog.d(error.toString());
                                praisenum.setClickable(true);
                            }

                        });

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

            description.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    startPostShow(PostShowActivity.FROM_POST);
                }
            });

        } else {
            description.setText(mData.description);
        }

        if (mIsShow) {
            int px = DensityUtils.dp2px(getContext(), 5);
            //详情列表图 间隔
            picGrid.setVerticalSpacing(px);
            picGrid.setNumColumns(1);
        } else {

            int size = mData.pics.size();
            switch (size) {
                case 1:
                case 2:
                case 4:
                    picGrid.setNumColumns(2);
                    break;
                default:
                    picGrid.setNumColumns(3);
                    break;
            }
        }

        final PicAdapter picAdapter = new PicAdapter(getContext(), mImageLoader, mData.pics);
        if (mIsShow) {
            picAdapter.setListShow();
        }
        picGrid.setAdapter(picAdapter);
        picGrid.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                ArrayList<AnimationRect> list = new ArrayList<AnimationRect>();
                for (int i = 0; i < picAdapter.getCount(); i++) {
                    View v = picGrid.getChildAt(i);
                    PicViewHolder holder = (PicViewHolder) v.getTag();
                    AnimationRect rect = AnimationRect
                            .buildFromImageView(holder.p_pic);
                    list.add(rect);
                }

                if (list.size() != picAdapter.getCount()) {
                    return;
                }

                Intent intent = GalleryAnimationActivity.newIntent(mData.pid,
                        list, position);
                getContext().startActivity(intent);
            }
        });

        if (mIsShow) {
//			AndroidUtil.setGridHeight(picGrid, 1);
        } else {
        }

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
                            CameraActivity.newPostIntent(mData.pid));
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

        browsenum.setText(String.valueOf(mData.view_num));
        praisenum.setText(String.valueOf(mData.zan_num));
        if (mData.iszan == 1) {
            praisenum.setClickable(false);
            praisenum.setTextColor(getContext().getResources().getColor(
                    R.color.txt_color));
            praisenum.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_praised, 0, 0, 0);
        } else {
            praisenum.setClickable(true);
            praisenum.setTextColor(0xff606060);
            praisenum.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.btn_praise_selector, 0, 0, 0);
        }

        reviewnum.setText(String.valueOf(mData.com_num));

        reviewnum.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MeiShaiSP.getInstance().getUserInfo().isLogin()) {
                    startPostShow(PostShowActivity.FROM_REVIEW);
                } else {
                    getContext().startActivity(LoginActivity.newOtherIntent());
                }
            }
        });

        setPraiseImage();
    }

    public void setCommentPop(OnClickListener clickListener) {
        reviewnum.setOnClickListener(clickListener);
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

    private void startPostShow(int from) {
        getContext().startActivity(PostShowActivity.newIntent(mData, from));
    }

}
