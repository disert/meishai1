package com.meishai.app.widget.layout;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meishai.R;
import com.meishai.app.widget.RoundCornerImageView;
import com.meishai.entiy.HomeInfo.DaRen;
import com.meishai.entiy.HomeInfo.DaRen.DaRenData;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.meiwu.FindMasterActivity;
import com.meishai.ui.fragment.home.HomeData;
import com.meishai.ui.fragment.home.HomePageActivity;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.DebugLog;

/**
 * 达人推荐对应的view ,问题是listview下面的数据显示不出来,待解决
 *
 * @author Administrator yl
 */
public class DaRenRecLayout extends LinearLayout {

    private Context mContext;
    private ImageView mMore;
    private TextView mTitle;
    private DaRen mData;
    private ImageLoader mImageLoader;
    private LinearLayout mDarens;
    private LinearLayout mRoot;

    public DaRenRecLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public DaRenRecLayout(Context context) {
        this(context, null);
    }

    private void initView() {
        View convertView = View.inflate(mContext,
                R.layout.daren_recommend_layout, this);
        mMore = (ImageView) convertView.findViewById(R.id.daren_more);
        mTitle = (TextView) convertView.findViewById(R.id.daren_title);
        mDarens = (LinearLayout) convertView.findViewById(R.id.daren_container);
        mRoot = (LinearLayout) convertView.findViewById(R.id.daren_root);
    }

    /**
     * 设置数据
     */
    public void setData(DaRen data, ImageLoader imageLoader) {
        if (mData == data) return;
        mData = data;
        mImageLoader = imageLoader;

        if (mData == null || mData.darendata == null || mData.darendata.size() < 1) {
            mRoot.setVisibility(View.GONE);
            return;
        }
        mRoot.setVisibility(View.VISIBLE);

        // 设置数据
        mTitle.setText(mData.text);
        mMore.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //推荐达人->更多 跳转到发现->达人
                mContext.startActivity(FindMasterActivity.newIntent());
            }
        });
        // 初始化达人推荐中的数据
        setDaRensView();

    }

    private void setDaRensView() {

        mDarens.removeAllViews();
        for (int i = 0; i < mData.darendata.size(); i++) {
            View convertView = View.inflate(mContext,
                    R.layout.daren_recommend_item, null);

            final DaRenData item = mData.darendata.get(i);

            RoundCornerImageView avatar = (RoundCornerImageView) convertView
                    .findViewById(R.id.avatar_iv);
            TextView username = (TextView) convertView
                    .findViewById(R.id.nicename_tv);
            TextView cate = (TextView) convertView.findViewById(R.id.cate_tv);
            final ImageView attention = (ImageView) convertView
                    .findViewById(R.id.attention_ib);

            username.setText(item.username);
            cate.setText(item.intro);

            // 设置关注
            if (item.isattention == 1) {// 如果已经关注了
                attention.setVisibility(View.VISIBLE);
                attention.setImageResource(R.drawable.ic_attention_yes);
                attention.setClickable(false);
            } else {// 没关注
                attention.setVisibility(View.VISIBLE);
                attention.setImageResource(R.drawable.ic_attention_no);
            }
            //关注 ->发送关注请求
            attention.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 发送一个关注请求
                    HomeData.getInstance().reqAttention(getContext(),
                            item.userid + "", new Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    // 处理关注请求的结果
                                    try {
                                        JSONObject obj = new JSONObject(
                                                response);
                                        int success = obj.getInt("success");
                                        if (HomeData.RET_UNLOGIN == success) {
                                            String tips = obj.getString("tips");
                                            Toast.makeText(getContext(), tips,
                                                    Toast.LENGTH_SHORT).show();
                                            attention.setClickable(true);

                                            getContext().startActivity(
                                                    LoginActivity
                                                            .newOtherIntent());

                                        } else {
                                            attention
                                                    .setImageResource(R.drawable.ic_attention_yes);
                                            attention.setClickable(false);
                                            item.isattention = 1;
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }, new ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    DebugLog.d(error.toString());
                                    attention.setClickable(true);
                                }

                            });
                }
            });

            // 加载头像,以及注册它的点击事件
            avatar.setTag(item.avatar);
            ListImageListener listener = new ListImageListener(avatar,
                    R.drawable.head_default, R.drawable.head_default,
                    item.avatar);
            mImageLoader.get(item.avatar, listener);
            //头像 ->达人主页
            avatar.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
//					Intent intent = new Intent(getContext(),
//							HomePageActivity.class);
//					intent.putExtra(ConstantSet.USERID, item.userid);
                    getContext().startActivity(HomePageActivity.newIntent(item.userid + ""));
                }
            });

            // 添加一个达人
            mDarens.addView(convertView);
        }
    }

}
