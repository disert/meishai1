package com.meishai.app.widget.layout;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.CommentInfo;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.home.HomePageActivity;

/***
 * 评论的item
 *
 * @author li
 */
public class PostCommentLayout extends LinearLayout {

    private RelativeLayout lay_comment;
    private ImageLoader mImageLoader;
    private CommentInfo mData;

    private ImageView avatar;
    private ImageView master;
    private ImageButton attention;
    private TextView username;
    private TextView addtime;
    private TextView louid;
    private TextView content;
    private GridView picGrid;
    private TextView area;
    private ImageView vip;
    private TextView replyName;
    private LinearLayout reply;
    private TextView replyContent;


    public PostCommentLayout(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.post_comment_item, this, true);
        lay_comment = (RelativeLayout) v.findViewById(R.id.lay_comment);
        avatar = (ImageView) v.findViewById(R.id.avatar_iv);
        master = (ImageView) v.findViewById(R.id.master_iv);
        attention = (ImageButton) v.findViewById(R.id.attention_ib);
        username = (TextView) v.findViewById(R.id.username_tv);
        addtime = (TextView) v.findViewById(R.id.addtime_tv);
        louid = (TextView) v.findViewById(R.id.louid_tv);
        content = (TextView) v.findViewById(R.id.content_tv);
        picGrid = (GridView) v.findViewById(R.id.pic_gridview);
        area = (TextView) v.findViewById(R.id.area_tv);
        vip = (ImageView) v.findViewById(R.id.vip_iv);

        reply = (LinearLayout) v.findViewById(R.id.reply_ll);
        reply.setVisibility(GONE);
        replyContent = (TextView) v.findViewById(R.id.reply_content);
        replyName = (TextView) v.findViewById(R.id.reply_name);
    }

    public View getItemLayout() {
        return lay_comment;
    }

    public void setData(CommentInfo info, ImageLoader imageLoader) {
        if (mData == info) {
            return;
        }
        mData = info;
        mImageLoader = imageLoader;

        avatar.setTag(mData.avatar);
        ListImageListener l = new ListImageListener(avatar, 0, 0, mData.avatar);
        mImageLoader.get(mData.avatar, l);

        if (mData.isdaren == 1) {
            master.setVisibility(View.VISIBLE);
        } else {
            master.setVisibility(View.INVISIBLE);
        }

        username.setText(mData.username);

        avatar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HomePageActivity.class);
                intent.putExtra(ConstantSet.USERID, mData.userid);
                getContext().startActivity(intent);
            }
        });

        username.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HomePageActivity.class);
                intent.putExtra("ConstantSet.USERID", mData.userid);
                getContext().startActivity(intent);
            }
        });

        addtime.setText(mData.addtime);
        String f_louid = getContext().getString(R.string.f_louid);
        louid.setText(String.format(f_louid, mData.louid));
        content.setText(mData.content);
        if (mData.isarea == 1 && !TextUtils.isEmpty(mData.areaname)) {
            area.setText(mData.areaname);
            area.setVisibility(VISIBLE);
        } else {
            area.setVisibility(GONE);
        }
        //显示会员等级
        setVipResId(mData.groupid);

        if (mData.isattention == 1) {
            attention.setImageResource(R.drawable.ic_attention_yes);
        } else {
            attention.setImageResource(R.drawable.ic_attention_no);
        }

        if (mData.redata == null) {
            reply.setVisibility(GONE);
        } else {
            reply.setVisibility(VISIBLE);
            replyName.setText(mData.redata.username + ":");
            replyContent.setText(mData.redata.content);
        }

//		PicAdapter picAdapter = new PicAdapter(getContext(), mImageLoader, mData.pics, mData.pid);
//		picGrid.setAdapter(picAdapter);
    }

    private void setVipResId(int groupId) {
        switch (groupId) {
            case 1:
                vip.setImageResource(R.drawable.v1);
                break;
            case 2:
                vip.setImageResource(R.drawable.v2);
                break;
            case 3:
                vip.setImageResource(R.drawable.v3);
                break;
            case 4:
                vip.setImageResource(R.drawable.v4);
                break;
            case 5:
                vip.setImageResource(R.drawable.v5);
                break;
            case 6:
                vip.setImageResource(R.drawable.v6);
                break;
            case 7:
                vip.setImageResource(R.drawable.v7);
                break;
            default:
                vip.setVisibility(View.INVISIBLE);
                return;
        }
        vip.setVisibility(View.VISIBLE);
    }

}
