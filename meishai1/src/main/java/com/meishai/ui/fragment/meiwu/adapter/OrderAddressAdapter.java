package com.meishai.ui.fragment.meiwu.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.ColorInfo;
import com.meishai.entiy.ConfirmExchange;
import com.meishai.entiy.ExchangeAddress;
import com.meishai.entiy.ExchangeSize;
import com.meishai.ui.fragment.usercenter.UserReceiveAddressActivity;
import com.meishai.util.AndroidUtil;

public class OrderAddressAdapter extends BaseAdapter {
    private final static int TYPE_ADDRESS = 0;
    private final static int TYPE_OTHER = 1;
    private Context context;
    private List<ExchangeAddress> addresss;
    private ConfirmExchange exchange;
    // 当前选中的索引位置
    private int CURRENT_CHECKED = 0;
    // 选中的尺码
    private ExchangeSize checkedSize;
    //选中的颜色
    private ColorInfo checkedColor;
    //当前的积分
    private int currentPoint = 0;


    public OrderAddressAdapter(Context context, List<ExchangeAddress> addresss) {
        super();
        this.context = context;
        this.addresss = addresss;
    }

    public void setAddresss(List<ExchangeAddress> addresss) {
        this.addresss = addresss;
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

    @Override
    public int getCount() {
        return addresss.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position < addresss.size()) {
            return addresss.get(position);
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
        if (position < addresss.size()) {
            return TYPE_ADDRESS;
        } else {
            return TYPE_OTHER;
        }
    }

    // 获取选中的地址
    public ExchangeAddress getCheckAddress() {
        if (null != this.addresss && !this.addresss.isEmpty()
                && this.addresss.size() > CURRENT_CHECKED) {
            return this.addresss.get(CURRENT_CHECKED);
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
                AddressViewHolder holder = null;
                if (null == convertView) {
                    holder = new AddressViewHolder();
                    LayoutInflater inflater = LayoutInflater.from(context);
                    convertView = inflater.inflate(R.layout.find_point_order_item,
                            null);
                    holder.lay_line = convertView
                            .findViewById(R.id.lay_line);
                    holder.rb_check = (RadioButton) convertView
                            .findViewById(R.id.rb_check);
                    holder.realname = (TextView) convertView
                            .findViewById(R.id.realname);
                    holder.phone = (TextView) convertView.findViewById(R.id.phone);
                    holder.modAddress = (ImageView) convertView.findViewById(R.id.mod_address);
                    holder.address = (TextView) convertView
                            .findViewById(R.id.address);
                    convertView.setTag(holder);
                } else {
                    holder = (AddressViewHolder) convertView.getTag();
                }
                if (0 == position) {
                    holder.lay_line.setVisibility(View.VISIBLE);
                } else {
                    holder.lay_line.setVisibility(View.GONE);
                }
                ExchangeAddress ads = addresss.get(position);
                if (null == ads) {
                    break;
                }
                if (ads.isDef()) {
                    holder.rb_check.setChecked(true);
                    CURRENT_CHECKED = position;
                } else {
                    holder.rb_check.setChecked(false);
                }
                holder.realname.setText(ads.getRealname());
                holder.phone.setText(ads.getPhone());
                holder.address.setText(ads.getProvince() + ads.getCity()
                        + ads.getAddress());
                holder.modAddress.setTag(ads);
                holder.modAddress.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ExchangeAddress data = (ExchangeAddress) v.getTag();
                        context.startActivity(UserReceiveAddressActivity.newModIntent(data));
                    }
                });
                holder.rb_check.setTag(position);
                convertView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int p = (Integer) ((AddressViewHolder) v.getTag()).rb_check.getTag();
                        if (CURRENT_CHECKED != p) {
                            setDefaultByPosition(p);
                            notifyDataSetChanged();
                        }
                    }
                });
//			holder.rb_check.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					int p = (Integer)v.getTag();
//					if (CURRENT_CHECKED != p) {
//						setDefaultByPosition(p);
//						notifyDataSetChanged();
//					}
//				}
//			});
                break;
            case TYPE_OTHER:
                if (null == convertView) {
                    convertView = new PointOrderOtherLayout(context);
                }
                ((PointOrderOtherLayout) convertView)
                        .setOnDataCheckedListener(new OnDataCheckedListener() {

                            @Override
                            public void onChecked(ExchangeSize ckSize) {
                                checkedSize = ckSize;
                                AndroidUtil.showToast(checkedSize.getName());
                            }
                        });
                ((PointOrderOtherLayout) convertView)
                        .setColorCheckedListener(new OnColorCheckedListener() {


                            @Override
                            public void onChecked(ColorInfo colorInfo) {
                                checkedColor = colorInfo;
                                AndroidUtil.showToast(checkedColor.getName());
                            }
                        });
                ((PointOrderOtherLayout) convertView)
                        .setOnPointChangeListener(new OnPointChangeListener() {
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

    private void setDefaultByPosition(int position) {
        int s = addresss.size();
        for (int i = 0; i < s; i++) {
            ExchangeAddress ads = addresss.get(i);
            if (i == position) {
                ads.setIsdefault(ExchangeAddress.IS_DEFAULT);
            } else {
                ads.setIsdefault(ExchangeAddress.NO_DEFAULT);
            }

        }
    }

    class AddressViewHolder {
        private View lay_line;
        private RadioButton rb_check;
        private TextView realname;
        private TextView phone;
        private TextView address;
        public ImageView modAddress;
    }

    public interface OnDataCheckedListener {
        public void onChecked(ExchangeSize checkedSize);
    }

    public interface OnColorCheckedListener {
        public void onChecked(ColorInfo checkedColor);
    }

    public interface OnPointChangeListener {
        public void onPointChange(int point);
    }
}
