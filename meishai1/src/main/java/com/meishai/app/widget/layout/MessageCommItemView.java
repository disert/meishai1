package com.meishai.app.widget.layout;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.util.ImageAdapter;

/**
 * Created by Administrator on 2016/6/9.
 */
public class MessageCommItemView extends LinearLayout {
    public LinearLayout mHead;
    public ImageView mImage;
    public TextView mTitle;
    public LinearLayout mBrowse;
    public ImageView mBrowseIcon;
    public TextView mBrowseNum;
    public LinearLayout mLike;
    public ImageView mLikeIcon;
    public TextView mLikeNum;

    public MessageCommItemView(Context context) {
        this(context, null);
    }

    public MessageCommItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.layout_message_comm_item,this);

        mHead = (LinearLayout)findViewById(R.id.message_comm_head);
        mImage = (ImageView)findViewById(R.id.message_comm_image);
        mTitle = (TextView)findViewById(R.id.message_comm_title);
        mBrowse = (LinearLayout)findViewById(R.id.message_comm_browse);
        mBrowseIcon = (ImageView)findViewById(R.id.message_comm_browse_icon);
        mBrowseNum = (TextView)findViewById(R.id.message_comm_browse_num);
        mLike = (LinearLayout)findViewById(R.id.message_comm_like);
        mLikeIcon = (ImageView)findViewById(R.id.message_comm_like_icon);
        mLikeNum = (TextView)findViewById(R.id.message_comm_like_num);
        Point point = ImageAdapter.getViewRealWH(1, 0, 750, 380);
        LayoutParams layoutParams = new LayoutParams(point.x,point.y);
        mImage.setLayoutParams(layoutParams);
    }
}
