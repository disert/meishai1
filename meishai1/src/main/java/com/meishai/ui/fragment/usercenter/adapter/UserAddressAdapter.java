package com.meishai.ui.fragment.usercenter.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.ExchangeAddress;
import com.meishai.entiy.ExchangeSize;
import com.meishai.ui.fragment.usercenter.UserReceiveAddressActivity;

/**
 * @author sh
 */
public class UserAddressAdapter extends BaseAdapter {
    private final static int TYPE_ADDRESS = 0;
    private final static int TYPE_OTHER = 1;
    private Context context;
    private List<ExchangeAddress> addresss;
    // 当前选中的索引位置
    private int CURRENT_CHECKED = 0;

    public UserAddressAdapter(Context context, List<ExchangeAddress> addresss) {
        super();
        this.context = context;
        this.addresss = addresss;
    }

    public void setAddresss(List<ExchangeAddress> addresss) {
        this.addresss = addresss;
    }

    @Override
    public int getCount() {
        return addresss.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return addresss.get(position);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case TYPE_ADDRESS:
                AddressViewHolder holder = null;
                if (null == convertView) {
                    holder = new AddressViewHolder();
                    LayoutInflater inflater = LayoutInflater.from(context);
                    convertView = inflater.inflate(R.layout.find_point_order_item,
                            null);
                    holder.lay_line = (View) convertView
                            .findViewById(R.id.lay_line);
                    holder.lay_info = (LinearLayout) convertView
                            .findViewById(R.id.lay_info);
                    holder.rb_check = (RadioButton) convertView
                            .findViewById(R.id.rb_check);
                    holder.realname = (TextView) convertView
                            .findViewById(R.id.realname);
                    holder.phone = (TextView) convertView.findViewById(R.id.phone);
                    holder.address = (TextView) convertView
                            .findViewById(R.id.address);
                    convertView.setTag(holder);
                } else {
                    holder = (AddressViewHolder) convertView.getTag();
                }
                if (0 == position) {
                    holder.lay_line.setVisibility(View.VISIBLE);
                }
                final ExchangeAddress ads = addresss.get(position);
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
                //TODO 有显示省份和城市
                holder.address.setText(ads.getProvince() + ads.getCity()
                        + ads.getAddress());
                holder.rb_check.setTag(position);
                holder.rb_check.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int p = (Integer) v.getTag();
                        if (CURRENT_CHECKED != p) {
                            setDefaultByPosition(p);
                            notifyDataSetChanged();
                        }
                    }
                });
                holder.lay_info.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = UserReceiveAddressActivity
                                .newModIntent(ads);
                        context.startActivity(intent);
                    }
                });
                break;
            case TYPE_OTHER:
                OperViewHolder operViewHolder = null;
                if (null == convertView) {
                    operViewHolder = new OperViewHolder();
                    LayoutInflater inflater = LayoutInflater.from(context);
                    convertView = inflater.inflate(
                            R.layout.user_receive_address_other, null);
                    operViewHolder.btn_add = (Button) convertView
                            .findViewById(R.id.btn_add);
                    convertView.setTag(operViewHolder);
                } else {
                    operViewHolder = (OperViewHolder) convertView.getTag();
                }
                operViewHolder.btn_add
                        .setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent intent = UserReceiveAddressActivity
                                        .newAddIntent();
                                context.startActivity(intent);
                            }
                        });
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
        private LinearLayout lay_info;
        private RadioButton rb_check;
        private TextView realname;
        private TextView phone;
        private TextView address;
    }

    class OperViewHolder {
        private Button btn_add;
    }

    public interface OnDataCheckedListener {
        public void onChecked(ExchangeSize checkedSize);
    }
}
