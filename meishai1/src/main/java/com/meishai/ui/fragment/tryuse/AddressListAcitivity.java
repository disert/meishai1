package com.meishai.ui.fragment.tryuse;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.ExchangeAddress;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.usercenter.UserAddressActivity;
import com.meishai.ui.fragment.usercenter.UserReceiveAddressActivity;
import com.meishai.ui.fragment.usercenter.req.MemberReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件名：AddressListAcitivity
 * 描    述：送货地址的列表
 * 作    者：yl
 * 时    间：2015/12/19
 * 版    权：
 */
public class AddressListAcitivity extends BaseActivity {

    private TextView mTitle;

    private PullToRefreshListView mListView;
    //底部的控制按钮
    private Button mButton;

    private List<ExchangeAddress> mAddressList;

    private int mAid;
    private AddressListAdapter mAdapter;

    /**
     * 选择地址使用的intent,需要传入一个aid
     *
     * @param aid
     * @return
     */
    public static Intent newIntent(int aid) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                AddressListAcitivity.class);
        intent.putExtra("aid", aid);
        return intent;
    }

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                AddressListAcitivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_list_layout);
        mAid = getIntent().getIntExtra("aid", -1);
        initView();
    }


    private void initView() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitle = (TextView) findViewById(R.id.title);
        mButton = (Button) findViewById(R.id.address_ctrl);
        if (mAid != -1) {
            mTitle.setText("选择收货地址");
            mButton.setText("管理收货地址");
        } else {
            mButton.setText("新增收货地址");
            mTitle.setText("管理收货地址");
        }
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAid != -1) {//选择收货地址
                    startActivity(AddressListAcitivity.newIntent());
                } else {//管理收货地址
                    startActivity(UserReceiveAddressActivity.newAddIntent());
                }
            }
        });
//        mMore = (ImageView)findViewById(R.id.more);

        mListView = (PullToRefreshListView) findViewById(R.id.listview);
        mListView.setMode(PullToRefreshBase.Mode.DISABLED);
        mAdapter = new AddressListAdapter();
        mListView.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        showProgress("", getString(R.string.network_wait));
        MemberReq.address(this, data, new Response.Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                hideProgress();
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<ExchangeAddress>>() {
                    }.getType();
                    mAddressList = GsonHelper
                            .parseObject(GsonHelper.toJson(response.getData()),
                                    type);
                    if (null != mAddressList && !mAddressList.isEmpty()) {
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                AndroidUtil.showToast(AddressListAcitivity.this.getString(R.string.reqFailed));
            }
        });
    }

    public void failt(Object ojb) {

    }

    public void updateUI(Object obj) {

    }

    class AddressListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mAddressList != null && !mAddressList.isEmpty()) {
                return mAddressList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mAddressList != null && !mAddressList.isEmpty()) {
                return mAddressList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AddressHolder holder;
            ExchangeAddress item = (ExchangeAddress) getItem(position);
            if (convertView == null) {
                holder = new AddressHolder();
                convertView = View.inflate(AddressListAcitivity.this, R.layout.address_item, null);
                holder.address = (TextView) convertView.findViewById(R.id.address);
                holder.name = (TextView) convertView.findViewById(R.id.realname);
                holder.phone = (TextView) convertView.findViewById(R.id.phone);
                holder.isDefault = (TextView) convertView.findViewById(R.id.is_default);
                holder.isSelected = (ImageView) convertView.findViewById(R.id.is_selected);


                convertView.setTag(holder);

            } else {
                holder = (AddressHolder) convertView.getTag();
            }
            holder.data = item;
            if (item != null) {
                String addr = item.getProvince() + " " + item.getCity() + " " + item.getAddress();
                holder.address.setText(addr);
                holder.name.setText(item.getRealname());
                holder.phone.setText(item.getPhone());
                if (item.getIsdefault() == 1) {
                    holder.isDefault.setVisibility(View.VISIBLE);
                } else {
                    holder.isDefault.setVisibility(View.GONE);
                }
                if (mAid != -1) {
                    if (item.getAid() == mAid) {
                        holder.isSelected.setVisibility(View.VISIBLE);
                    } else {
                        holder.isSelected.setVisibility(View.INVISIBLE);
                    }
                } else {
                    holder.isSelected.setVisibility(View.VISIBLE);
                    holder.isSelected.setImageResource(R.drawable.more_right_arrow);
                }

            } else {
                //默认值
                holder.address.setText("null");
                holder.name.setText("null");
                holder.phone.setText("null");
//                holder.isDefault.setVisibility(View.GONE);
//                holder.isSelected.setImageResource(R.drawable.place_default);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddressHolder addressHolder = (AddressHolder) v.getTag();
                    if (addressHolder != null) {
                        //返回选择结果
                        ExchangeAddress data = addressHolder.data;
                        if (mAid != -1) {//选择收货地址--那么就返回一个结果
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("address", data);
                            Intent intent = new Intent();
                            intent.putExtras(bundle);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {//管理收货地址--那么跳转到编辑地址页面
                            startActivity(UserReceiveAddressActivity.newModIntent(data));
                        }
                    }

                }
            });


            return convertView;
        }
    }

    class AddressHolder {
        TextView name;
        TextView phone;
        TextView isDefault;
        TextView address;
        ImageView isSelected;
        ExchangeAddress data;

    }
}
