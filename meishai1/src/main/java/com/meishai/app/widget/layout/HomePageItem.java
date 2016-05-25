package com.meishai.app.widget.layout;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meishai.R;
import com.meishai.app.widget.CircleImageView;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.PostItem;
import com.meishai.entiy.HomePageDatas.PostInfo;
import com.meishai.entiy.PointRewardRespData.PointData;
import com.meishai.entiy.PostItem.ZanUserInfo;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.net.volley.toolbox.ImageLoader.ImageListener;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.home.HomeData;
import com.meishai.ui.fragment.home.HomePageActivity;
import com.meishai.ui.fragment.home.PostShowActivity;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;

/**
 * 个人主页帖子所对应的view
 *
 * @author Administrator yl
 */
public class HomePageItem extends LinearLayout {


    private ImageLoader mImageLoader;
    private PostInfo mItem1;
    private PostInfo mItem2;
    private Context mContext;
    private LinearLayout mRoot;
    private LinearLayout.LayoutParams lp;
    private int width;
    private LinearLayout mLine;

    public HomePageItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();

    }

    public HomePageItem(Context context) {
        this(context, null);
    }

    public void setData(PostInfo item1, PostInfo item2, ImageLoader imageLoader) {

        mItem1 = item1;
        mItem2 = item2;
        mImageLoader = imageLoader;
        setItem(mItem1, mItem2);
    }

    public void setData(List<PostInfo> infos, ImageLoader imageLoader) {
        if (infos == null || infos.isEmpty()) {
            return;
        }
        mItem1 = infos.get(0);
        if (infos.size() > 1) {
            mItem2 = infos.get(1);
        }
        mImageLoader = imageLoader;
        setItem(mItem1, mItem2);
    }

    private void setItem(PostInfo item1, PostInfo item2) {
        mRoot.removeAllViews();
        initData(item1);
        if (item2 == null) {
            View view = new View(mContext);
            view.setLayoutParams(lp);
            mRoot.addView(view);
        } else {
            initData(item2);
        }
    }

    private void initView() {
        View.inflate(mContext, R.layout.home_page_item, this);
        mRoot = (LinearLayout) findViewById(R.id.home_page_item_root);
        mLine = (LinearLayout) findViewById(R.id.home_page_item_line);

        lp = new LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
    }


    private void initData(PostInfo item) {
        if (item == null) {
            return;
        }
//		int px = AndroidUtil.dip2px(10);
//		lp.setMargins(px, px, 0, 0);
        View view = View.inflate(mContext, R.layout.home_page_item_o, null);
        view.setLayoutParams(lp);
        ImageView image = (ImageView) view.findViewById(R.id.home_page_item_image);
        ImageView master = (ImageView) view.findViewById(R.id.home_page_item_master);
        CircleImageView avatar = (CircleImageView) view.findViewById(R.id.home_page_item_avatar);
        TextView name = (TextView) view.findViewById(R.id.home_page_item_name);
        TextView desc = (TextView) view.findViewById(R.id.home_page_item_desc);
        ImageView praiseIcon = (ImageView) view.findViewById(R.id.home_page_item_praise_icon);
        TextView praiseNum = (TextView) view.findViewById(R.id.home_page_item_praise_num);
        LinearLayout praise = (LinearLayout) view.findViewById(R.id.home_page_item_praise);
        LinearLayout avatarContainer = (LinearLayout) view.findViewById(R.id.home_page_item_avatar_container);


        view.setTag(item);
        praise.setTag(item);
        avatar.setTag(item);

        //设置事件
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!(v.getTag() instanceof PostInfo)) {//进行类型判断
                    DebugLog.w("类型不匹配");
                    return;
                }
                PostInfo tag = (PostInfo) v.getTag();
                startPostShow(PostShowActivity.FROM_POST, tag.pid);
            }
        });


        praise.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //点赞
                if (!(v.getTag() instanceof PostInfo)) {//进行类型判断
                    DebugLog.w("类型不匹配");
                    return;
                }
                PostInfo tag = (PostInfo) v.getTag();
                LinearLayout praise = (LinearLayout) v;
                if (tag.iszan == 0) {
                    zan(tag, praise);
                }
            }
        });

        avatarContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),
                        HomePageActivity.class);
                if (!(v.getTag() instanceof PostInfo)) {//进行类型判断
                    DebugLog.w("类型不匹配");
                    return;
                }
                PostInfo tag = (PostInfo) v.getTag();
                intent.putExtra(ConstantSet.USERID, tag.userid);
                getContext().startActivity(intent);
            }
        });

        //设置数据

        //图片的宽度 = (屏幕宽度 - 间距 * 3 ) / 2 - 边框宽度 * 2   或者   屏幕宽度 / 2 - (间距 * 3 / 2 + 边框宽度 * 2)
        int imageWidth = width / 2 - AndroidUtil.dip2px(5 * 3 / 2.0f + 0.3f * 2);
        image.setLayoutParams(new RelativeLayout.LayoutParams(imageWidth, imageWidth));
        ImageListener listener1 = ImageLoader.getImageListener(image,
                R.drawable.image_back_default, R.drawable.image_back_default);
        mImageLoader.get(item.image, listener1, imageWidth, imageWidth);

        avatar.setTag(item.avatar);
        ListImageListener listener = new ListImageListener(avatar,
                R.drawable.head_default, R.drawable.head_default,
                item.avatar);
        mImageLoader.get(item.avatar, listener);
        if (item.isdaren == 1) {
            master.setVisibility(VISIBLE);
        } else {
            master.setVisibility(GONE);
        }

        name.setText(item.username);
        desc.setText(item.description);
        if (item.zan_num > 0) {
            praiseNum.setText(item.zan_num + "");
        } else {
            praiseNum.setText("赞");

        }
        if (item.iszan == 1) {
            praiseIcon.setSelected(true);
            praise.setClickable(false);
        } else {
            praiseIcon.setSelected(false);
            praise.setClickable(true);
        }
        mRoot.addView(view);


    }

    public void setLineVisibility(int visibility) {
        mLine.setVisibility(visibility);
    }

    private void startPostShow(int from, int pid) {
        PostItem postItem = new PostItem();
        postItem.pid = pid;
        Intent intent = PostShowActivity.newIntent(postItem, from);
        // 个人详情页面
        getContext().startActivity(intent);
    }

    private void zan(final PostInfo tag, final LinearLayout praise) {

        if (!MeiShaiSP.getInstance().getUserInfo().isLogin()) {
            getContext().startActivity(LoginActivity.newOtherIntent());
            return;
        }

        String msg = getContext().getResources().getString(
                R.string.network_wait);
        final CustomProgress dlgProgress = CustomProgress.show(
                getContext(), msg, true, null);

        HomeData.getInstance().reqZan(tag.pid,
                new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        dlgProgress.dismiss();

                        DebugLog.d(response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int success = obj.getInt("success");
                            String tips = obj.getString("tips");
                            Toast.makeText(getContext(), tips,
                                    Toast.LENGTH_SHORT).show();
                            if (HomeData.RET_ERROR == success) {
//								String tips = obj.getString("tips");
//								Toast.makeText(getContext(), tips,
//										Toast.LENGTH_SHORT).show();

                            } else {
                                tag.zan_num++;
                                ImageView icon = (ImageView) praise.getChildAt(0);
                                TextView praiseNum = (TextView) praise.getChildAt(1);
                                icon.setSelected(true);
                                praiseNum.setText(tag.zan_num + "");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dlgProgress.dismiss();

                        DebugLog.d(error.toString());
                    }

                });


    }
}
