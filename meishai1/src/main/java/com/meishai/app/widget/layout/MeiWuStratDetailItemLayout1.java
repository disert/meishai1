package com.meishai.app.widget.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.StratDetailRespData.Picture;
import com.meishai.entiy.StratDetailRespData1;
import com.meishai.entiy.UserInfo;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.fragment.meiwu.MeiWuGoodsDetailActivity;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;

/**
 * 美物-攻略详情-item 新版本迭代
 * <p/>
 * 2015年11月18日09:44:25
 *
 * @author Administrator yl
 */
public class MeiWuStratDetailItemLayout1 extends RelativeLayout {

    private ImageLoader mImageLoader;
    private Context mContext;
    private StratDetailRespData1.stratDetailItem mData;

    private TextView mTitle;
    private TextView mDesc;
    private LinearLayout mPics;
    private TextView mPrice;
    private TextView mDetail;
    private View mDownLine;
    private View mUpLine;
    private RelativeLayout mBuyContainer;
    private View mBottomLine;
    private ImageView mCollect;
    private LinearLayout mDetailContainer;
    private ImageView mTaste;
    private int screenWidth;
    private TextView mTips;

    public MeiWuStratDetailItemLayout1(Context context) {
        this(context, null);
    }

    public MeiWuStratDetailItemLayout1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeiWuStratDetailItemLayout1(Context context, AttributeSet attrs,
                                       int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(context);
        initListenner();
    }

    private void initView(Context context) {

        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;     // 屏幕宽度（像素）

        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.meiwu_strat_detail_item1,
                this, true);

        mTitle = (TextView) convertView
                .findViewById(R.id.meiwu_strat_detail_item_title);
        mDownLine = convertView
                .findViewById(R.id.meiwu_strat_detail_item_title_down_line);

        mUpLine = convertView
                .findViewById(R.id.meiwu_strat_detail_item_title_up_line);

        mDesc = (TextView) convertView
                .findViewById(R.id.meiwu_strat_detail_item_desc);
        mPics = (LinearLayout) convertView
                .findViewById(R.id.meiwu_strat_detail_item_pics);

        mTips = (TextView) convertView
                .findViewById(R.id.meiwu_strat_detail_item_tips);

        mBuyContainer = (RelativeLayout) convertView
                .findViewById(R.id.meiwu_strat_detail_item_buy_container);

        mPrice = (TextView) convertView
                .findViewById(R.id.meiwu_strat_detail_item_price);

        mCollect = (ImageView) convertView
                .findViewById(R.id.meiwu_strat_detail_item_collect);

        mDetail = (TextView) convertView
                .findViewById(R.id.meiwu_strat_detail_item_detail);
        mDetailContainer = (LinearLayout) convertView
                .findViewById(R.id.meiwu_strat_detail_item_buy);

        mTaste = (ImageView) convertView
                .findViewById(R.id.meiwu_strat_detail_item_taste);

        mBottomLine = convertView
                .findViewById(R.id.meiwu_strat_detail_item_bottom_line);

    }

    private void initListenner() {
        // 自己的
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mData.itemurl)) {
                    buyReq();
                }
            }
        });
        // 点购买的
        mDetailContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //商品详情页
//				Intent intent  = MeiWuGoodsDetailActivity.newIntent(mData.pid);
//				mContext.startActivity(intent);
                if (!TextUtils.isEmpty(mData.itemurl)) {
                    buyReq();
                }
            }
        });

        //点收藏的
        mCollect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                collect();
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

    public void setData(StratDetailRespData1.stratDetailItem data, ImageLoader imageLoader) {
        mImageLoader = imageLoader;
        mData = data;
        if (mData == null) {
            DebugLog.w("没有数据!");
            return;
        }
        // 初始化数据
        //标题
        if (TextUtils.isEmpty(mData.title)) {
            mTitle.setVisibility(GONE);
            mDownLine.setVisibility(GONE);
            mUpLine.setVisibility(GONE);
        } else {
            mTitle.setVisibility(VISIBLE);
            mTitle.setText(mData.title);
            //标题居中
            if (mData.title_is_center == 1) {
                mTitle.setGravity(Gravity.CENTER);
            } else {
                mTitle.setGravity(Gravity.CENTER_VERTICAL);
            }
            //标题的线
            if (mData.title_is_line == 1) {
                mDownLine.setVisibility(VISIBLE);
                mUpLine.setVisibility(VISIBLE);
            } else {
                mDownLine.setVisibility(GONE);
                mUpLine.setVisibility(GONE);
            }
            //标题粗体
            TextPaint tp = mTitle.getPaint();
            if (mData.title_is_strong == 0) {
                tp.setFakeBoldText(false);
            } else {
                tp.setFakeBoldText(true);
            }
            //标题颜色
            if (!TextUtils.isEmpty(mData.title_color)) {
                mTitle.setTextColor(Color.parseColor("#FF" + mData.title_color));
            } else {
                mTitle.setTextColor(0xff333333);
            }

        }
        //文本
        if (TextUtils.isEmpty(mData.content)) {
            mDesc.setVisibility(GONE);
        } else {
            mDesc.setVisibility(VISIBLE);
            mDesc.setText(mData.content);
            //文本居中
            if (mData.content_is_center == 1) {
                mDesc.setGravity(Gravity.CENTER);
            } else {
                mDesc.setGravity(Gravity.LEFT);
            }
            //文本粗体
            TextPaint tp = mDesc.getPaint();
            if (mData.content_is_strong == 0) {
                tp.setFakeBoldText(false);
            } else {
                tp.setFakeBoldText(true);
            }
            //文本颜色
            if (!TextUtils.isEmpty(mData.content_color)) {
                //content_color
                mDesc.setTextColor(Color.parseColor("#FF" + mData.content_color));
            } else {
                mDesc.setTextColor(0xff333333);
            }
        }
        //提示
        if (TextUtils.isEmpty(mData.tips_text) || mData.tips_is_display != 1) {
            mTips.setVisibility(GONE);
        } else {
            mTips.setVisibility(VISIBLE);
            mTips.setTextColor(Color.parseColor("#FF" + mData.tips_color));
            mTips.setText(mData.tips_text);
            if (mData.tips_is_center == 1) {
                mTips.setGravity(Gravity.CENTER);
            } else {
                mTips.setGravity(Gravity.CENTER_VERTICAL);
            }
        }
        //购买行
        if (mData.isitem == 1) {
            mBuyContainer.setVisibility(VISIBLE);
            mPrice.setText(mData.price);
            mDetail.setText(mData.button_text);
            if (TextUtils.isEmpty(mData.itemurl)) {
                //没有商品链接
                mDetailContainer.setBackgroundResource(R.drawable.btn_cir_back_blue);
                mTaste.setVisibility(GONE);
                mDetailContainer.setClickable(false);
            } else {
                //有商品链接
                mDetailContainer.setBackgroundResource(R.drawable.btn_cir_back_orange);
                mDetailContainer.setClickable(true);
                if (mData.ispromise == 1) {
                    mTaste.setVisibility(VISIBLE);
                } else {
                    mTaste.setVisibility(GONE);
                }
            }
            if (mData.isfav == 1) {
                mCollect.setImageResource(R.drawable.collected_icon);
                mCollect.setClickable(false);
            } else {
                mCollect.setImageResource(R.drawable.uncollect_icon);
                mCollect.setClickable(true);
            }
        } else {
            mBuyContainer.setVisibility(GONE);
        }

        //图片
        setPics();

        //底部的线
        if (mData.isline == 1) {
            mBottomLine.setVisibility(VISIBLE);
        } else {
            mBottomLine.setVisibility(GONE);
        }

    }

    private void setPics() {
        if (mData.pics == null || mData.pics.size() == 0) {
            DebugLog.w("没有图片");
            mPics.removeAllViews();
            return;
        }
        mPics.removeAllViews();


        LinearLayout.LayoutParams pl = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int px = AndroidUtil.dip2px(5);
        pl.setMargins(0, px, 0, 0);
        for (Picture pic : mData.pics) {

            SampleRoundImageView view = new SampleRoundImageView(mContext);
            ImageView iv = view.getImageView();
//			iv.setAdjustViewBounds(true);
//			iv.setLayoutParams(pl);
            //width是图片显示时所具有的宽度,12是距离左右两边的距离,是在xml文件中定义的
            double width = screenWidth - AndroidUtil.dip2px(8) * 2;
            double widthScale = width / pic.pic_width;
            int height = (int) (widthScale * pic.pic_height);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) width, height);
            layoutParams.setMargins(0, AndroidUtil.dip2px(5), 0, 0);
            view.setLayoutParams(layoutParams);

            iv.setTag(pic.pic_path);
            ListImageListener listener = new ListImageListener(iv,
                    R.drawable.image_back_default, R.drawable.image_back_default,
                    pic.pic_path);
            mImageLoader.get(pic.pic_path, listener);

            mPics.addView(view);
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
                DebugLog.w("点赞的返回数据:" + response);
                mCollect.setImageResource(R.drawable.collected_icon);
                mCollect.setClickable(false);
//				if(TextUtils.isEmpty(response)){
//					mCollect.setImageResource(R.drawable.collected_icon);
//					mCollect.setClickable(false);
//					return;
//				}
//				try {
//					JSONObject obj = new JSONObject(response);
//					int success = obj.getInt("success");
//					String tips = obj.getString("tips");
////					Toast.makeText(getContext(), tips, Toast.LENGTH_SHORT).show();
////					AndroidUtil.showToast("点赞的返回数据:" + response);
//
//					if (1 == success) {
//						mCollect.setImageResource(R.drawable.collected_icon);
//						mCollect.setClickable(false);
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
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
