package com.meishai.ui.fragment.meiwu;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
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
import com.meishai.entiy.CateResqData;
import com.meishai.entiy.CateResqData.CateData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ImageLoader.ImageListener;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;

/**
 * 美物 - 分类  现改为搜索的分类和关键词
 *
 * @author yl
 */
public class MeiWuCateFragment extends BaseFragment {
    private View view;
    private GridView mGvCate;
    private CateResqData mResqData;
    private CateAdapter mCateAdapter;
    private GridView mGvKeys;
    private LinearLayout mKeysContainer;
    private int mShowMode = 0;
    private KeysAdapter mKeyAdapter;
    private OnKeySelectedListener mKeySelectedListener;

    public static final int MODE_CATE = 0;
    public static final int MODE_KEY = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.meiwu_cate, null);
        this.initView();
        this.getRequestData();
        return view;
    }

    private void initView() {
        mGvCate = (GridView) view.findViewById(R.id.meiwu_cate_gridview);
        mCateAdapter = new CateAdapter();
        mGvCate.setAdapter(mCateAdapter);

        mGvKeys = (GridView) view.findViewById(R.id.gv_keys);
        mKeyAdapter = new KeysAdapter();
        mGvKeys.setAdapter(mKeyAdapter);

        mKeysContainer = (LinearLayout) view.findViewById(R.id.keys);
        mGvCate.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                CateData item = (CateData) mCateAdapter.getItem(position);
                Intent intent = MeiWuCateDetailActivity.newIntent(item.cid);
                startActivity(intent);
            }
        });

        mGvKeys.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mKeySelectedListener != null) {
                    String key = mResqData.keys.get(position).key;
//						AndroidUtil.showToast(key);
                    mKeySelectedListener.keySelected(key);
                }
            }
        });

    }

    public void setOnKeySelectedListener(OnKeySelectedListener listener) {
        mKeySelectedListener = listener;
    }

    public interface OnKeySelectedListener {
        void keySelected(String key);
    }

    @Override
    public void onResume() {
        super.onResume();
        show();
    }

    private void getRequestData() {
        showProgress("", getString(R.string.network_wait));
        MeiWuReq.CateReq(1, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                checkRespData(response);
                hideProgress();
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(R.string.reqFailed);
                hideProgress();
            }
        });
    }

    protected void checkRespData(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            int success = obj.getInt("success");
            if (success == 1) {
                mResqData = GsonHelper
                        .parseObject(response, CateResqData.class);
                mCateAdapter.notifyDataSetChanged();
            } else {
                AndroidUtil.showToast(R.string.reqFailed);

            }

        } catch (JSONException e) {
            DebugLog.e(e.getMessage());
            e.printStackTrace();
        }
    }

    public void setShowMode(int showMode) {
        mShowMode = showMode;
        if (mGvKeys != null && mGvCate != null) {
            show();
        }
    }

    public void show() {
        if (mShowMode == MODE_CATE) {
            mKeysContainer.setVisibility(View.GONE);
            mGvCate.setVisibility(View.VISIBLE);
        } else if (mShowMode == MODE_KEY) {
            mGvCate.setVisibility(View.GONE);
            mKeysContainer.setVisibility(View.VISIBLE);
        }
    }

    class CateAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mResqData == null || mResqData.list == null
                    || mResqData.list.size() == 0) {
                return 0;
            }
            return mResqData.list.size();
        }

        @Override
        public Object getItem(int position) {
            return mResqData.list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            CateData item = (CateData) getItem(position);
            if (convertView == null) {
                holder = new Holder();
                convertView = View.inflate(mContext, R.layout.meiwu_cate_item,
                        null);
                holder.avatar = (ImageView) convertView
                        .findViewById(R.id.meiwu_cate_avatar);
                holder.catname = (TextView) convertView
                        .findViewById(R.id.meiwu_cate_catname);
                holder.desc = (TextView) convertView
                        .findViewById(R.id.meiwu_cate_desc);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.catname.setText(item.catname);
            holder.desc.setText(item.desc);
            holder.avatar.setTag(item.image);
            ImageListener listener1 = ImageLoader.getImageListener(
                    holder.avatar, R.drawable.place_default,
                    R.drawable.place_default);
            getImageLoader().get(item.image, listener1);

            return convertView;
        }
    }

    class Holder {
        ImageView avatar;
        TextView catname;
        TextView desc;
    }

    //	@Override
//	public void setUserVisibleHint(boolean isVisibleToUser) {
//		super.setUserVisibleHint(isVisibleToUser);
//		if (isVisibleToUser) {
//			if(mResqData == null){
//				getRequestData();
//			}
//		} else {
//			// 相当于Fragment的onPause
//		}
//	}
    class KeysAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mResqData == null || mResqData.list == null
                    || mResqData.keys.size() == 0) {
                return 0;
            }
            return mResqData.keys.size();
        }

        @Override
        public Object getItem(int position) {
            return mResqData.keys.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = null;
            CateResqData.Key item = (CateResqData.Key) getItem(position);
            if (convertView == null) {
                convertView = View.inflate(mContext,
                        R.layout.release_item, null);
                tv = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(tv);
            } else {
                tv = (TextView) convertView.getTag();
            }
            tv.setText(item.key);

            return convertView;
        }
    }
}
