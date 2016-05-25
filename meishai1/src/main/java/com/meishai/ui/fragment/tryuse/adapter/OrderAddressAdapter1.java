package com.meishai.ui.fragment.tryuse.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.ColorInfo;
import com.meishai.entiy.ConfirmExchange;
import com.meishai.entiy.ExchangeAddress;
import com.meishai.entiy.ExchangeSize;
import com.meishai.ui.fragment.meiwu.adapter.OrderAddressAdapter;
import com.meishai.ui.fragment.meiwu.adapter.PointOrderOtherLayout;
import com.meishai.ui.fragment.tryuse.AddressListAcitivity;
import com.meishai.ui.fragment.usercenter.UserReceiveAddressActivity;

public class OrderAddressAdapter1 extends BaseAdapter {
    private final static int TYPE_ADDRESS = 0;
    private final static int TYPE_OTHER = 1;
    private Context context;
    private ConfirmExchange exchange;
    // 选中的尺码
    private ExchangeSize checkedSize;
    //选中的颜色
    private ColorInfo checkedColor;
    //当前的积分
    private int currentPoint = 0;

    private boolean isrefresh = false;//是否需要刷新,当添加新地址的时候需要


    public OrderAddressAdapter1(Context context) {
        super();
        this.context = context;
    }


    public void setExchange(ConfirmExchange exchange) {
        this.exchange = exchange;
        if (null != exchange && exchange.getGoodsdata() != null) {
            currentPoint = exchange.getGoodsdata().get(0).point;
        }
    }

    public ExchangeSize getCheckedSize() {
        return checkedSize;
    }

    public ColorInfo getCheckedColor() {
        return checkedColor;
    }

    public int getCurrentPoint() {
        return currentPoint;
    }

    public boolean isrefresh() {
        return isrefresh;
    }

    public void setIsrefresh(boolean isrefresh) {
        this.isrefresh = isrefresh;
    }

    @Override
    public int getCount() {
        if (exchange != null) {
            return 2;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return exchange.getUseraddress();
        } else {
            return exchange;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_ADDRESS;
        } else {
            return TYPE_OTHER;
        }
    }

    // 获取选中的地址
    public ExchangeAddress getCheckAddress() {
        if (null != exchange) {
            return exchange.getUseraddress();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Object item = getItem(position);
        switch (getItemViewType(position)) {
            case TYPE_ADDRESS://地址
                AddressHolder holder;
                if (null == convertView) {
                    holder = new AddressHolder();
                    convertView = View.inflate(context, R.layout.dest_address_layout, null);
                    holder.defaultUi = (Button) convertView.findViewById(R.id.address_add);
                    holder.hasDataUi = (LinearLayout) convertView.findViewById(R.id.item_root);
                    holder.address = (TextView) convertView.findViewById(R.id.address);
                    holder.name = (TextView) convertView.findViewById(R.id.realname);
                    holder.phone = (TextView) convertView.findViewById(R.id.phone);
                    convertView.findViewById(R.id.location_icon).setVisibility(View.VISIBLE);
                    convertView.findViewById(R.id.is_default).setVisibility(View.GONE);
                    holder.isSelected = (ImageView) convertView.findViewById(R.id.is_selected);
                    convertView.findViewById(R.id.bottom_line).setVisibility(View.GONE);
                    holder.isSelected.setVisibility(View.VISIBLE);
                    holder.isSelected.setImageResource(R.drawable.more_right_arrow);
                    convertView.setTag(holder);
                } else {
                    holder = (AddressHolder) convertView.getTag();
                }
                if (exchange != null && exchange.getUseraddress() != null && !TextUtils.isEmpty(exchange.getUseraddress().getRealname())) {
                    holder.hasDataUi.setVisibility(View.VISIBLE);
                    holder.defaultUi.setVisibility(View.GONE);
                    String addr = exchange.getUseraddress().getProvince() + " " + exchange.getUseraddress().getCity() + " " + exchange.getUseraddress().getAddress();
                    holder.address.setText("收货地址:" + addr);
                    holder.name.setText("收件人:" + exchange.getUseraddress().getRealname());
                    holder.phone.setText(exchange.getUseraddress().getPhone());
                    holder.aid = exchange.getUseraddress().getAid();

                } else {
                    holder.hasDataUi.setVisibility(View.GONE);
                    holder.defaultUi.setVisibility(View.VISIBLE);
                }
                holder.hasDataUi.setTag(holder.aid);
                holder.hasDataUi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //到地址列表
                        int aid = (Integer) v.getTag();
                        ((Activity) context).startActivityForResult(AddressListAcitivity.newIntent(aid), 1);
                    }
                });
                holder.defaultUi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //没有地址,到哪里?
                        context.startActivity(UserReceiveAddressActivity.newAddIntent());
                        isrefresh = true;
                    }
                });


                break;
            case TYPE_OTHER:
                if (null == convertView) {
                    convertView = new PointOrderOtherLayout(context);
                }
                ((PointOrderOtherLayout) convertView)
                        .setOnDataCheckedListener(new OrderAddressAdapter.OnDataCheckedListener() {

                            @Override
                            public void onChecked(ExchangeSize ckSize) {
                                checkedSize = ckSize;
//							AndroidUtil.showToast(checkedSize.getName());
                            }
                        });
                ((PointOrderOtherLayout) convertView)
                        .setColorCheckedListener(new OrderAddressAdapter.OnColorCheckedListener() {


                            @Override
                            public void onChecked(ColorInfo colorInfo) {
                                checkedColor = colorInfo;
//							AndroidUtil.showToast(checkedColor.getName());
                            }
                        });
                ((PointOrderOtherLayout) convertView)
                        .setOnPointChangeListener(new OrderAddressAdapter.OnPointChangeListener() {
                            @Override
                            public void onPointChange(int point) {
//							AndroidUtil.showToast("积分:"+point);
                                currentPoint = point;
                            }
                        });
                ((PointOrderOtherLayout) convertView).updateDataView(exchange);
                break;
            default:
                break;
        }

        return convertView;
    }


    class AddressHolder {
        TextView name;
        TextView phone;
        TextView address;
        ImageView isSelected;
        int aid;
        LinearLayout hasDataUi;
        Button defaultUi;
    }


}
