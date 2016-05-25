package com.meishai.ui.fragment.meiwu.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.util.DensityUtils;
import com.meishai.app.widget.layout.FlowLayout;
import com.meishai.entiy.ColorInfo;
import com.meishai.entiy.ConfirmExchange;
import com.meishai.entiy.ExchangeSize;
import com.meishai.entiy.FuLiGoodsInfo;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;
import com.meishai.ui.fragment.meiwu.adapter.OrderAddressAdapter.OnColorCheckedListener;
import com.meishai.ui.fragment.meiwu.adapter.OrderAddressAdapter.OnDataCheckedListener;
import com.meishai.util.AndroidUtil;

public class PointOrderOtherLayout extends LinearLayout {

    private Context mContext;
    //    private Button btn_address;
    private ImageLoader imageLoader = null;
    private NetworkImageView thumb;
    private TextView title;
    private TextView price;
    private TextView groupid;
    private TextView lowpoint;

    // 尺码区域
    private LinearLayout mLaySizeContent;
    // 尺码数据区域
    private FlowLayout mLaySize;
    // 选中的尺码
    private ExchangeSize exchangeSize;
    // 选中的TextView
    private TextView tvCheckSize;
    // 颜色数据区域
    private FlowLayout mLayColor;
    // 选中的颜色
    private ColorInfo exchangeColor;
    // 选中的TextView
    private TextView tvCheckColor;
    // 小编提醒区域
    private LinearLayout mLayTip;
    private TextView tv_tip;

    private OnDataCheckedListener sizeCheckedListener;
    private OnColorCheckedListener colorCheckedListener;
    private OrderAddressAdapter.OnPointChangeListener pointChangeListener;

    private LinearLayout mLayColorContent;
    private RelativeLayout layPoint;
    private TextView lowpointText;

    private RelativeLayout layFreight;
    private TextView freightNum;
    private TextView freightText;
    private RelativeLayout layTotal;
    private TextView totalNum;
    private TextView totalText;
    private TextView totalUnit;
    private RelativeLayout layBidding;
    private ImageView biddingSub;
    private ImageView biddingPlus;
    private EditText biddingEdit;
    private TextView biddingText;
    private ConfirmExchange confirmExchange;

    public PointOrderOtherLayout(Context context) {
        super(context);
        mContext = context;
        imageLoader = VolleyHelper.getImageLoader(context);
        initView();
    }

    public void setColorCheckedListener(OnColorCheckedListener colorCheckedListener) {
        this.colorCheckedListener = colorCheckedListener;
    }

    public void setOnDataCheckedListener(OnDataCheckedListener checkedListener) {
        this.sizeCheckedListener = checkedListener;
    }

    public void setOnPointChangeListener(OrderAddressAdapter.OnPointChangeListener pointChangeListener) {
        this.pointChangeListener = pointChangeListener;
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View convertView = inflater.inflate(R.layout.find_point_order_other,
                this, true);
//        btn_address = (Button) convertView.findViewById(R.id.btn_address);
        thumb = (NetworkImageView) convertView.findViewById(R.id.thumb);
        title = (TextView) convertView.findViewById(R.id.title);
        price = (TextView) convertView.findViewById(R.id.price);
        groupid = (TextView) convertView.findViewById(R.id.groupid);
        lowpoint = (TextView) convertView.findViewById(R.id.lowpoint);
        lowpointText = (TextView) convertView.findViewById(R.id.lowpoint_text);
        layPoint = (RelativeLayout) convertView.findViewById(R.id.layout_right);

        layFreight = (RelativeLayout) convertView.findViewById(R.id.lay_freight);
        freightNum = (TextView) convertView.findViewById(R.id.freight_number);
        freightText = (TextView) convertView.findViewById(R.id.freight_text);
        layTotal = (RelativeLayout) convertView.findViewById(R.id.lay_total);
        totalNum = (TextView) convertView.findViewById(R.id.total_number);
        totalText = (TextView) convertView.findViewById(R.id.total_text);
        totalUnit = (TextView) convertView.findViewById(R.id.total_unit);
        layBidding = (RelativeLayout) convertView.findViewById(R.id.lay_bidding);
        biddingText = (TextView) convertView.findViewById(R.id.bidding_text);
        biddingSub = (ImageView) convertView.findViewById(R.id.btn_sub);
        biddingPlus = (ImageView) convertView.findViewById(R.id.btn_plus);
        biddingEdit = (EditText) convertView.findViewById(R.id.point);

        mLaySizeContent = (LinearLayout) this
                .findViewById(R.id.lay_size_content);
        mLaySize = (FlowLayout) this.findViewById(R.id.lay_size);
        mLayColorContent = (LinearLayout) this
                .findViewById(R.id.lay_color_content);
        mLayColor = (FlowLayout) this.findViewById(R.id.lay_color);
        mLayTip = (LinearLayout) this.findViewById(R.id.lay_tip);
        tv_tip = (TextView) convertView.findViewById(R.id.tv_tip);
//        btn_address.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                //TODO 要等待返回结果的,点击跳转到编辑地址界面,这里是直接把数据提交到服务器,再通过去服务器取
//                mContext.startActivity(UserReceiveAddressActivity.newAddIntent());
//            }
//        });

    }

    public void updateDataView(ConfirmExchange confirmExchange) {
        this.confirmExchange = confirmExchange;
        if (null != confirmExchange) {
            //--------------------商品区域----------------------
            if (null != confirmExchange.getGoodsdata()
                    && !confirmExchange.getGoodsdata().isEmpty()) {
                FuLiGoodsInfo detail = confirmExchange.getGoodsdata().get(0);
                thumb.setErrorImageResId(R.drawable.ob_db);
                thumb.setDefaultImageResId(R.drawable.ob_db);
                thumb.setImageUrl(detail.thumb, imageLoader);
                title.setText(detail.title == null ? "" : detail.title);
                price.setText(detail.text1 == null ? "" : detail.text1);
                groupid.setText(detail.text2 == null ? "" : detail.text2);

                if (detail.point > 0) {
                    layBidding.setVisibility(VISIBLE);
                    biddingText.setText(detail.point_text);
                    biddingEdit.setText(detail.point + "");
                    setPointOperListener(detail.addpoint);
                } else {
                    layBidding.setVisibility(GONE);
                }
                freightText.setText(detail.fee_text);
                freightNum.setText(detail.fee_price);

                totalText.setText(detail.price_text);
                if (detail.price_value == 0) {
                    totalNum.setVisibility(GONE);
                } else {
                    totalNum.setVisibility(VISIBLE);
                    totalNum.setText(detail.price_value + "");
                }
                totalUnit.setText(detail.price_unit);


            }

            //----------------------------尺寸区域-------------------------
            if (null != confirmExchange.getSizedata()
                    && !confirmExchange.getSizedata().isEmpty()) {
                mLaySize.removeAllViews();
                int heigth = DensityUtils.dp2px(mContext, 30);
                int rightMargin = DensityUtils.dp2px(mContext, 10);
                int bottomMargin = DensityUtils.dp2px(mContext, 8);
                for (ExchangeSize size : confirmExchange.getSizedata()) {
                    TextView tv_size = new TextView(mContext);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, heigth);
                    lp.rightMargin = rightMargin;
                    lp.bottomMargin = bottomMargin;
                    // 默认样式
                    setDefTextStyle(tv_size);
                    tv_size.setText(size.getName());
                    mLaySize.addView(tv_size, lp);
                    tv_size.setTag(size);
                    tv_size.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            ExchangeSize es = (ExchangeSize) v.getTag();
                            if (null != exchangeSize
                                    && es.getKid() == exchangeSize.getKid()) {
                                return;
                            }
                            exchangeSize = es;
                            if (null != tvCheckSize) {
                                setDefTextStyle(tvCheckSize);
                            }
                            TextView textView = (TextView) v;
                            tvCheckSize = textView;
                            setCheckedTextStyle(textView);
                            if (null != sizeCheckedListener) {
                                sizeCheckedListener.onChecked(exchangeSize);
                            }
                        }
                    });
                }
                mLaySizeContent.setVisibility(View.VISIBLE);
                mLaySize.setVisibility(View.VISIBLE);
            } else {
                mLaySizeContent.setVisibility(View.GONE);
                mLaySize.setVisibility(View.GONE);
            }

            //---------------------颜色区域-----------------------
            if (null != confirmExchange.getColordata()
                    && !confirmExchange.getColordata().isEmpty()) {
                mLayColor.removeAllViews();
                int heigth = DensityUtils.dp2px(mContext, 30);
                int rightMargin = DensityUtils.dp2px(mContext, 10);
                int bottomMargin = DensityUtils.dp2px(mContext, 8);
                for (ColorInfo color : confirmExchange.getColordata()) {
                    TextView tv_color = new TextView(mContext);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, heigth);
                    lp.rightMargin = rightMargin;
                    lp.bottomMargin = bottomMargin;
                    // 默认样式
                    setDefTextStyle(tv_color);
                    tv_color.setText(color.getName());
                    mLayColor.addView(tv_color, lp);
                    tv_color.setTag(color);
                    tv_color.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            ColorInfo color = (ColorInfo) v.getTag();
                            if (null != exchangeColor
                                    && color.getColorid() == exchangeColor.getColorid()) {
                                return;
                            }
                            exchangeColor = color;
                            if (null != tvCheckColor) {
                                setDefTextStyle(tvCheckColor);
                            }
                            TextView textView = (TextView) v;
                            tvCheckColor = textView;
                            setCheckedTextStyle(textView);
                            if (null != colorCheckedListener) {
                                colorCheckedListener.onChecked(exchangeColor);
                            }
                        }
                    });
                }
                mLayColorContent.setVisibility(View.VISIBLE);
                mLayColor.setVisibility(View.VISIBLE);

            } else {
                mLayColorContent.setVisibility(View.GONE);
                mLayColor.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置默认样式
     *
     * @param tv_size
     */
    private void setDefTextStyle(TextView tv_size) {
        tv_size.setClickable(true);
        tv_size.setGravity(Gravity.CENTER);
        tv_size.setBackgroundResource(R.drawable.size_unfocused);
        tv_size.setTextColor(mContext.getResources().getColor(
                R.color.size_def_color));
        tv_size.setTextSize(14f);
    }

    /**
     * 设置选择样式
     *
     * @param tv_size
     */
    private void setCheckedTextStyle(TextView tv_size) {
        tv_size.setClickable(true);
        tv_size.setGravity(Gravity.CENTER);
        tv_size.setBackgroundResource(R.drawable.size_focused);
        tv_size.setTextColor(mContext.getResources().getColor(R.color.white));
        tv_size.setTextSize(14f);

    }

    private void setPointOperListener(final int addPoint) {
        biddingSub.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int p = Integer.parseInt(biddingEdit.getText().toString());
                if (p >= addPoint) {
                    int s = p - addPoint;
//  TODO  边界判断
                    if (confirmExchange != null) {
                        FuLiGoodsInfo info = confirmExchange.getGoodsdata().get(0);
                        if (s < info.point) {
                            AndroidUtil.showToast("不能更低了");
                            biddingSub.setClickable(false);
                            return;
                        }
                        if (s < info.userpoint) {
                            biddingPlus.setClickable(true);
                        }
                    }
                    biddingEdit.setText(s + "");
                    totalNum.setText(s + "");

                    if (pointChangeListener != null) {
                        pointChangeListener.onPointChange(s);
                    }
                }
            }
        });
        biddingPlus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int p = Integer.parseInt(biddingEdit.getText().toString());
                int s = p + addPoint;
                if (confirmExchange != null) {
                    FuLiGoodsInfo info = confirmExchange.getGoodsdata().get(0);
                    if (s > info.userpoint) {
                        AndroidUtil.showToast("您的积分不足");
                        biddingPlus.setClickable(false);
                        return;
                    }
                    if (s > info.point) {
                        biddingSub.setClickable(true);
                    }
                }

                biddingEdit.setText(s + "");
                totalNum.setText(s + "");

                if (pointChangeListener != null) {
                    pointChangeListener.onPointChange(s);
                }
            }
        });

    }
}
