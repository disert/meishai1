package com.meishai.ui.fragment.home.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.CircleNetWorkImageView;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CatalogInfo;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.home.ChannelShowFragmentActivity;
import com.meishai.ui.fragment.home.HomeCateActivity.RigthCatalogListener;
import com.meishai.ui.fragment.home.req.CateReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;

/**
 * 美晒分类->右边分类列表
 *
 * @author sh
 */
public class HomeCatalogAdapter extends BaseAdapter {
    private Context mContext = null;
    private List<CatalogInfo> catalogInfos = null;
    private LayoutInflater inflater = null;
    private ImageLoader imageLoader = null;
    private CustomProgress mProgressDialog = null;
    private RigthCatalogListener rigthCatalogListener = null;

    public HomeCatalogAdapter(Context context, List<CatalogInfo> catalogInfos) {
        super();
        this.mContext = context;
        this.catalogInfos = catalogInfos;
        inflater = LayoutInflater.from(context);
        imageLoader = VolleyHelper.getImageLoader(mContext);
    }

    public void setRigthCatalogListener(
            RigthCatalogListener rigthCatalogListener) {
        this.rigthCatalogListener = rigthCatalogListener;
    }

    public void setCatalogInfos(List<CatalogInfo> catalogInfos) {
        this.catalogInfos = catalogInfos;
    }

    @Override
    public int getCount() {
        return catalogInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return catalogInfos.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.home_cate_catalog_item,
                    null);
            holder.lay_cate = (RelativeLayout) convertView
                    .findViewById(R.id.lay_cate);
            holder.image = (CircleNetWorkImageView) convertView
                    .findViewById(R.id.image);
            holder.image.setRoundness(6f);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.desc = (TextView) convertView.findViewById(R.id.desc);
            holder.isadd = (ImageButton) convertView.findViewById(R.id.isadd);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CatalogInfo catalogInfo = catalogInfos.get(position);
        holder.image.setImageUrl(catalogInfo.getImage(), imageLoader);
        holder.name.setText(catalogInfo.getName());
        holder.desc.setText(catalogInfo.getDesc());
        holder.isadd.setTag(catalogInfo.getCid());
        if (catalogInfo.getIsadd() == CatalogInfo.HAS_ADD) {
            holder.isadd.setImageResource(R.drawable.ic_round_remove);
            holder.isadd.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int cid = (Integer) v.getTag();
                    delcatalog(cid);
                }
            });
        } else {
            holder.isadd.setImageResource(R.drawable.ic_round_add);
            holder.isadd.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int cid = (Integer) v.getTag();
                    addcatalog(cid);
                }
            });
        }

        holder.lay_cate.setTag(position);
        holder.lay_cate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int p = (Integer) v.getTag();
                CatalogInfo catalog = catalogInfos.get(p);
                Intent intent = ChannelShowFragmentActivity.newIntent(
                        catalog.getCid(), catalog.getImage(), catalog.getName());
                mContext.startActivity(intent);

            }
        });
        return convertView;
    }

    /**
     * 分类id
     *
     * @param cid
     */
    private void addcatalog(int cid) {
        String message = "正在添加，请稍候...";
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress
                    .show(mContext, message, true, null);
        } else {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("cid", String.valueOf(cid));
        CateListener cateListener = new CateListener();
        CateReq.addcatalog(mContext, data, cateListener, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    private void delcatalog(int cid) {
        String message = "正在删除，请稍候...";
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress
                    .show(mContext, message, true, null);
        } else {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("cid", String.valueOf(cid));
        CateListener cateListener = new CateListener();
        CateReq.delcatalog(mContext, data, cateListener, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    class CateListener implements Listener<RespData> {

        @Override
        public void onResponse(RespData response) {
            if (null != mProgressDialog) {
                mProgressDialog.hide();
            }
            if (response.isSuccess()) {
                if (null != rigthCatalogListener) {
                    rigthCatalogListener.onClick();
                }
            } else if (response.isLogin()) {
                mContext.startActivity(LoginActivity.newOtherIntent());
            } else {
                AndroidUtil.showToast(response.getTips());
            }
        }
    }

    class ViewHolder {
        private RelativeLayout lay_cate;
        private CircleNetWorkImageView image;
        private TextView name;
        private TextView desc;
        private ImageButton isadd;
    }

}
