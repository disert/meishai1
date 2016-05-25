package com.meishai.app.widget.layout;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.SKUResqData.Blurb;
import com.meishai.entiy.UserInfo;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ImageLoader.ImageListener;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;

/**
 * 个人主页帖子所对应的view
 *
 * @author Administrator yl
 */
public class MeiWuSKUItemLayout extends LinearLayout {

    private ImageLoader mImageLoader;
    private Blurb mItem1;
    private Blurb mItem2;
    private Context mContext;
    private LinearLayout mRoot;
    private int screenWidth;

    public MeiWuSKUItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public MeiWuSKUItemLayout(Context context) {
        this(context, null);
    }

    public void setData(Blurb item1, Blurb item2, ImageLoader imageLoader) {
        mItem1 = item1;
        mItem2 = item2;
        mImageLoader = imageLoader;

        mRoot.removeAllViews();
        initData(mItem1);
        initData(mItem2);
    }

    private void initView() {
        View.inflate(mContext, R.layout.home_page_item, this);
        mRoot = (LinearLayout) findViewById(R.id.home_page_item_root);
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;     // 屏幕宽度（像素）
    }

    private void initData(final Blurb item) {

        LinearLayout.LayoutParams lp = new LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1);

        if (item == null) {
            View view = new View(mContext);
            view.setLayoutParams(lp);
            mRoot.addView(view);
            return;
        }
//		int px = AndroidUtil.dip2px(10);
//		lp.setMargins(px, px, 0, 0);

        View view = View.inflate(mContext, R.layout.meiwu_sku_item, null);
        view.setLayoutParams(lp);

        ImageView mImage = (ImageView) view.findViewById(R.id.meiwu_sku_image);
        int width = (screenWidth - AndroidUtil.dip2px(5) * 3) / 2 - AndroidUtil.dip2px(0.3f) * 2;
        mImage.setLayoutParams(new RelativeLayout.LayoutParams(width, width));
        ImageView mIspick = (ImageView) view.findViewById(R.id.meiwu_sku_ispick);
        ImageView promise = (ImageView) view.findViewById(R.id.meiwu_sku_promise);
        TextView mDesc = (TextView) view.findViewById(R.id.meiwu_sku_desc);
        TextView mPrice = (TextView) view.findViewById(R.id.meiwu_sku_price);
        LinearLayout mLike = (LinearLayout) view.findViewById(R.id.meiwu_sku_like);
        ImageView mLikeIcon = (ImageView) view.findViewById(R.id.meiwu_sku_like_icon);
        LinearLayout mLinked = (LinearLayout) view.findViewById(R.id.meiwu_sku_linked);
        ImageView mLinkedIcon = (ImageView) view.findViewById(R.id.meiwu_sku_linked_icon);
        TextView mLinkedText = (TextView) view.findViewById(R.id.meiwu_sku_linked_tv);

        //今日上新
        if (item.isnew == 1) {
            mIspick.setVisibility(View.VISIBLE);
        } else {
            mIspick.setVisibility(View.GONE);
        }
        //担保
        String space = "      ";
        if (item.ispromise == 1) {
            promise.setVisibility(VISIBLE);
            mDesc.setText(space + item.title);
        } else {
            promise.setVisibility(GONE);
            mDesc.setText(item.title);
        }
        //链接
        if (item.islink == 1) {
            mLinkedText.setText("直达链接");
            mLinkedText.setTextColor(0xFFFFCC00);
            mLinked.setClickable(true);
            mLinkedIcon.setSelected(true);

        } else {
            mLinkedText.setText("暂无链接");
            mLinkedText.setTextColor(0xffcdcdcd);
            mLinked.setClickable(false);
            mLinkedIcon.setSelected(false);
        }
        //收藏
        if (item.isfav == 1) {
            mLikeIcon.setSelected(true);
        } else {
            mLikeIcon.setSelected(false);
        }
        //价格
        mPrice.setText(item.price);
        //图片
        ImageListener listener1 = ImageLoader.getImageListener(mImage,
                R.drawable.place_default, R.drawable.place_default);
        mImageLoader.get(item.image, listener1, width, width);


        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.islink == 1)
                    MeiWuReq.buyReq(mContext, item.pid, item.itemurl, item.istao);
            }
        });
        mLinked.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (item.islink == 1)
                    MeiWuReq.buyReq(mContext, item.pid, item.itemurl, item.istao);
            }
        });
        mLike.setTag(mLikeIcon);
        mLike.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                collect(item, (ImageView) v.getTag());
            }
        });


        mRoot.addView(view);

    }

    /**
     * 收藏
     *
     * @param item
     */
    private void collect(Blurb item, final ImageView view) {

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
        MeiWuReq.collect(item.pid, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                dlgProgress.dismiss();
                DebugLog.w("点赞的返回数据:" + response);
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getInt("success") == 1) {
                            view.setSelected(true);
                            view.setClickable(false);
                        }
                        AndroidUtil.showToast(obj.getString("tips"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
