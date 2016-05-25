package com.meishai.app.widget.layout;

import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.FreeTrialRespData;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ImageLoader.ImageListener;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.ui.fragment.tryuse.req.FuLiSheReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.ImageAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 免费试用头部所对应的view
 *
 * @author Administrator yl
 */
public class FreeTrialHeader extends LinearLayout {

    private ImageLoader mImageLoader;
    private List<FreeTrialRespData.Brack> mData;

    private Context mContext;
    private LinearLayout mRoot;
    private Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject obj = new JSONObject(response);
                int success = obj.getInt("success");
                //				AndroidUtil.showToast(obj.getString(""));
                if (success == 1) {//请求到了链接
                    mContext.startActivity(MeishaiWebviewActivity.newIntent(obj.getString("url")));
                } else if (success == -1) {//没登陆
                    mContext.startActivity(LoginActivity.newIntent());

                } else {//发生错误
                    AndroidUtil.showToast("错误");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                AndroidUtil.showToast("数据格式错误");
            }
        }
    };
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            AndroidUtil.showToast("网络异常");
        }
    };

    public FreeTrialHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    public FreeTrialHeader(Context context) {
        this(context, null);
    }


    private void initView(Context context) {
        View.inflate(mContext, R.layout.free_trial_header, this);
        mRoot = (LinearLayout) findViewById(R.id.free_trial_head_root);
    }


    /**
     * 为该view设置数据
     *
     * @param userinfo
     * @param imageLoader
     */
    public void setData(List<FreeTrialRespData.Brack> userinfo, ImageLoader imageLoader) {
        mData = userinfo;
        mImageLoader = imageLoader;
        updateUI();
    }

    private void updateUI() {
        if (mData == null || mData.isEmpty()) {
            setVisibility(GONE);
            return;
        }
        mRoot.removeAllViews();
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        int size = mData.size();
        for (int i = 0; i < size / 2; i++) {
            LinearLayout ll = new LinearLayout(mContext);
            ll.setLayoutParams(lp);

            if (i * 2 + 1 < mData.size()) {
                addChild(ll, mData.get(i * 2), mData.get(i * 2 + 1));
            } else {
                addChild(ll, mData.get(i * 2), null);
            }


            mRoot.addView(ll);
        }
    }

    private void addChild(LinearLayout ll, final FreeTrialRespData.Brack child1, final FreeTrialRespData.Brack child2) {
        int dip = AndroidUtil.dip2px(7);
        Point point = ImageAdapter.getViewRealWH(2, dip, 750, 380);
        LayoutParams lp = new LayoutParams(point.x, point.y);
        lp.setMargins(0, dip, dip, 0);

        //添加第一个
        ImageView image = new ImageView(mContext);
//        image.setPadding(0, dip, dip, 0);
        image.setLayoutParams(lp);
        image.setAdjustViewBounds(true);
        if (!TextUtils.isEmpty(child1.image)) {
            image.setTag(child1.image);
            ImageListener listener1 = ImageLoader.getImageListener(
                    image, R.drawable.place_default,
                    R.drawable.place_default);
            mImageLoader.get(child1.image, listener1);
        } else {
            image.setImageResource(R.drawable.place_default);
        }
        image.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PublicReq.h5Req(child1.controller, child1.action, listener, errorListener);
            }
        });
        ll.addView(image);

        if (child2 == null) {
            TextView tv = new TextView(mContext);
            image.setPadding(0, dip, dip, 0);
            tv.setLayoutParams(lp);
            return;
        }

        //添加第二个
        ImageView image2 = new ImageView(mContext);
//        image2.setPadding(0, dip, dip, 0);
        image2.setLayoutParams(lp);
        image2.setAdjustViewBounds(true);
        if (!TextUtils.isEmpty(child2.image)) {
            image2.setTag(child2.image);
            ImageListener listener2 = ImageLoader.getImageListener(
                    image2, R.drawable.place_default,
                    R.drawable.place_default);
            mImageLoader.get(child2.image, listener2);
        } else {
            image2.setImageResource(R.drawable.place_default);
        }
        image2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PublicReq.h5Req(child2.controller, child2.action, listener, errorListener);
            }
        });


        ll.addView(image2);
    }


}
