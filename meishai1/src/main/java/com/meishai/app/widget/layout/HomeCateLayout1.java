package com.meishai.app.widget.layout;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.HomeInfo.CateInfo;
import com.meishai.entiy.ShareData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;
import com.meishai.ui.dialog.ShareInviteDialog;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.home.DaysTopicActivity;
import com.meishai.ui.fragment.home.HomeTopicListActivity;
import com.meishai.ui.fragment.home.HotActionActivity;
import com.meishai.ui.fragment.home.req.HomeReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.SkipUtils;

/**
 * 主页分类页面 2.0
 *
 * @author Administrator yl
 */
public class HomeCateLayout1 extends LinearLayout {

    private Context mContext;
    private View mConvertView;
    private List<CateInfo> mCateList;
    private ImageLoader mImageLoader;
    private GridView mGridView;
    private CateGridAdapter mAdapter;
    private ShareInviteDialog mSharePop;

    public HomeCateLayout1(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public HomeCateLayout1(Context context) {
        this(context, null);
    }

    private void initView() {

        mSharePop = new ShareInviteDialog(mContext);

        mConvertView = View.inflate(mContext,
                R.layout.home_handpick_cate_item1, this);
        mGridView = (GridView) mConvertView.findViewById(R.id.cate_gridview);

        mAdapter = new CateGridAdapter(mContext);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                boolean login = MeiShaiSP.getInstance().getUserInfo()
                        .isLogin();
                CateInfo info = mCateList.get(position);
                // TODO 小应用的类型处理
                if (position == 3) {
                    //热门活动
//					Intent intent = HotActionActivity.newIntent();
                    Intent intent = DaysTopicActivity.newIntent(mContext);
                    mContext.startActivity(intent);

                } else if ("invite".equals(info.type)) {
                    // 邀请好友 TODO 改成h5页面
                    if (!login) {// 没登陆
                        getContext().startActivity(
                                LoginActivity.newOtherIntent());
                    } else {// 登陆了
                        SkipUtils.skip(mContext, "h5", "", 0, info.h5data);
                    }
//					mSharePop.show();
                } else {
                    // 其他
                    SkipUtils.skip(mContext, info.type, info.value, info.tempid, info.h5data);
                }

            }
        });
    }

    public void setData(List<CateInfo> cateList, ShareData sharedata,
                        ImageLoader imageLoader) {
        if (cateList == null || cateList.size() < 1) {
            return;
        }
        mSharePop.initShareParams(sharedata);

        mCateList = cateList;
        mImageLoader = imageLoader;

        mAdapter.notifyDataSetChanged();
    }

    private class CateGridAdapter extends BaseAdapter {

        private LayoutInflater inflate;

        public CateGridAdapter(Context context) {
            inflate = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {

            if (null == mCateList) {
                return 0;
            }
            return mCateList.size();
        }

        @Override
        public Object getItem(int position) {
            if (position < mCateList.size()) {
                return mCateList.get(position);

            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null == convertView) {
                convertView = inflate.inflate(R.layout.handpick_cate_item1,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (NetworkImageView) convertView
                        .findViewById(R.id.cate_iv);
                holder.name = (TextView) convertView.findViewById(R.id.cate_tv);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            CateInfo item = (CateInfo) getItem(position);
            holder.image.setImageUrl(item.image, mImageLoader);
            holder.image.setScaleType(ImageView.ScaleType.FIT_XY);
            // mImageLoader.get(item.image, listener);

            holder.name.setText(item.text);

            return convertView;
        }

        private class ViewHolder {
            private NetworkImageView image;
            private TextView name;
        }
    }
}
