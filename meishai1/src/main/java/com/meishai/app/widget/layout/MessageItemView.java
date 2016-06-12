package com.meishai.app.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.CircleTextView;

/**
 * Created by Administrator on 2016/6/9.
 *
 *消息的item的封装view
 */
public class MessageItemView extends LinearLayout {

    public RelativeLayout mRoot;
    public ImageView mIcon;
    public TextView mText;
    public ImageView mRight;
    public CircleTextView mNum;

    public MessageItemView(Context context) {
        this(context, null);
    }

    public MessageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.layout_message_item,this);

        mRoot = (RelativeLayout)findViewById(R.id.message_item_root);
        mIcon = (ImageView)findViewById(R.id.message_item_icon);
        mText = (TextView)findViewById(R.id.message_item_text);
        mRight = (ImageView)findViewById(R.id.message_item_right);
        mNum = (CircleTextView)findViewById(R.id.message_item_num);


    }
}
