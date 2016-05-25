package com.meishai.app.widget.layout;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meishai.R;
import com.meishai.app.util.StringUtil;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserInfo;
import com.meishai.entiy.StratDetailRespData.Picture;
import com.meishai.entiy.StratDetailRespData.SpecialItem;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;

/**
 * 美物-攻略详情-item
 *
 * @author Administrator yl
 */
public class MeiWuStratDetailItemLayout extends RelativeLayout {

    private ImageLoader mImageLoader;
    private Context mContext;
    private SpecialItem mData;

    private TextView mNum;
    private TextView mTitle;
    private TextView mDesc;
    private LinearLayout mPics;
    private TextView mGuardTitle;
    private TextView mPrice;
    private TextView mDetail;
    private LinearLayout mCollect;
    private ImageView mCollectIcon;
    private TextView mCollectText;
    private LinearLayout mGuard;

    public MeiWuStratDetailItemLayout(Context context) {
        this(context, null);
    }

    public MeiWuStratDetailItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeiWuStratDetailItemLayout(Context context, AttributeSet attrs,
                                      int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(context);
        initListenner();
    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.meiwu_strat_detail_item,
                this, true);

        mNum = (TextView) convertView
                .findViewById(R.id.meiwu_strat_detail_item_num);
        mTitle = (TextView) convertView
                .findViewById(R.id.meiwu_strat_detail_item_title);
        mDesc = (TextView) convertView
                .findViewById(R.id.meiwu_strat_detail_item_desc);
        mPics = (LinearLayout) convertView
                .findViewById(R.id.meiwu_strat_detail_item_pics);
        mGuard = (LinearLayout) convertView.findViewById(R.id.meiwu_strat_detail_item_guard);
        mGuardTitle = (TextView) convertView
                .findViewById(R.id.meiwu_strat_detail_item_guard_title);
        mPrice = (TextView) convertView
                .findViewById(R.id.meiwu_strat_detail_item_price);
        mCollect = (LinearLayout) convertView
                .findViewById(R.id.meiwu_strat_detail_item_collect);
        mCollectIcon = (ImageView) convertView
                .findViewById(R.id.meiwu_strat_detail_item_collect_icon);
        mCollectText = (TextView) convertView
                .findViewById(R.id.meiwu_strat_detail_item_collect_text);
        mDetail = (TextView) convertView
                .findViewById(R.id.meiwu_strat_detail_item_detail);

    }

    private void initListenner() {
        // 自己的
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//				Intent intent = MeiWuGoodsDetailActivity.newIntent(mData.pid);
//				mContext.startActivity(intent);
                buyReq();
            }
        });
        // 点收藏的
        mCollect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                collect();
            }
        });
        // 点购买的
        mDetail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                buyReq();
            }
        });
        //点击保障文字的
        mGuardTitle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = MeishaiWebviewActivity.newIntent(mData.promise_url);
                mContext.startActivity(intent);
            }
        });
    }

    protected void buyReq() {
        // 发送购买的请求.在请求中有URL直接跳转到淘宝
        MeiWuReq.buyReq(mContext, mData.pid, mData.itemurl, mData.istao);
//		MeiWuReq.buy(mData.pid, new Listener<String>() {
//
//			@Override
//			public void onResponse(String response) {
//				try {
//					JSONObject json = new JSONObject(response);
//					int success = json.getInt("success");
//					String tips = json.getString("tips");
//					if(success == 1){
//						if(!TextUtils.isEmpty(tips)){
//							AndroidUtil.showToast(tips);
//						}
//						Intent intent = MeishaiWebviewActivity.newIntent(mData.itemurl);
//						mContext.startActivity(intent);
//						
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		}, new ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				AndroidUtil.showToast("购物请求发送失败");
//				error.printStackTrace();
//			}
//		});

    }

    public void setData(SpecialItem data, ImageLoader imageLoader) {
        mImageLoader = imageLoader;
        mData = data;
        if (mData == null) {
            DebugLog.w("没有数据!");
            return;
        }
        // 初始化数据
        mNum.setText(mData.number + "");
        mTitle.setText(mData.title);
        mDesc.setText(mData.content);
        if (1 == mData.isfav) {
            mCollectIcon.setImageResource(R.drawable.has_collect);
            mCollectText.setTextColor(0xff999999);
            mCollectText.setText("已收藏");
            mCollect.setClickable(false);
        } else {
            mCollectIcon.setImageResource(R.drawable.not_collect);
            mCollectText.setTextColor(0xff555555);
            mCollectText.setText("加收藏");
            mCollect.setClickable(true);
        }

        if (StringUtil.isEmpty(mData.promise_text)) {
            mGuardTitle.setVisibility(View.INVISIBLE);
            mGuard.setVisibility(View.INVISIBLE);
        } else {
            mGuard.setVisibility(View.VISIBLE);
            mGuardTitle.setVisibility(View.VISIBLE);
            mGuardTitle.setText(mData.promise_text);
        }
        mPrice.setText(mData.price);


        mDetail.setText(mData.button_text);


        setPics();

    }

    private void setPics() {
        if (mData.pics == null || mData.pics.size() == 0) {
            DebugLog.w("没有图片");
            return;
        }
        mPics.removeAllViews();

        LinearLayout.LayoutParams pl = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int px = AndroidUtil.dip2px(10);
        pl.setMargins(0, px, 0, 0);
        for (Picture pic : mData.pics) {
            ImageView iv = new ImageView(mContext);
            iv.setAdjustViewBounds(true);
            iv.setLayoutParams(pl);

            iv.setTag(pic.pic_path);
            ListImageListener listener = new ListImageListener(iv,
                    R.drawable.image_back_default, R.drawable.image_back_default,
                    pic.pic_path);
            mImageLoader.get(pic.pic_path, listener);

            mPics.addView(iv);
        }
    }


    /**
     * 收藏
     */
    private void collect() {

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
        MeiWuReq.collect(mData.pid, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                dlgProgress.dismiss();
                if (TextUtils.isEmpty(response)) {
                    mCollectIcon.setImageResource(R.drawable.has_collect);
                    mCollectText.setTextColor(0xff999999);
                    mCollectText.setText("已收藏");
                    mCollect.setClickable(false);
                    return;
                }
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");
                    String tips = obj.getString("tips");
                    Toast.makeText(getContext(), tips, Toast.LENGTH_SHORT)
                            .show();
                    if (1 == success) {
                        mCollectIcon.setImageResource(R.drawable.has_collect);
                        mCollectText.setTextColor(0xff999999);
                        mCollectText.setText("已收藏");
                        mCollect.setClickable(false);
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
