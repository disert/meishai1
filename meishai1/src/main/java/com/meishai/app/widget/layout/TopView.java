package com.meishai.app.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;

/**
 * 文 件 名：
 * 描    述：顶部view的封装类
 * 作    者：yangling
 * 时    间：2016-06-05
 * 版    权：
 */

public class TopView extends LinearLayout{

    private Context mContext;

    private Button mBack;
    private ImageButton mBackImage;
    private TextView mTitle;
    private ImageButton mMoreImage;
    private Button mMore;

    public TopView(Context context) {
        this(context, null);
    }

    public TopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        init();
    }

    private void init() {
        View.inflate(mContext, R.layout.common_title_layout, this);

        mBack = (Button) findViewById(R.id.back);
        mMore = (Button) findViewById(R.id.more_button);
        mMoreImage = (ImageButton) findViewById(R.id.more_image);
        mBackImage = (ImageButton) findViewById(R.id.back_image);
        mTitle = (TextView) findViewById(R.id.title);

        mBack.setVisibility(GONE);
        mMore.setVisibility(GONE);
        mMoreImage.setVisibility(GONE);
        mBack.setVisibility(GONE);

    }

    public void setBackImageResource(int id){
        mBackImage.setImageResource(id);
    }
    public void setMoreImageResource(int id){
        mMoreImage.setImageResource(id);
    }

    public void setBackVisibility(int visibility){
        mBack.setVisibility(visibility);
    }
    public void setMoreVisibility(int visibility){
        mMore.setVisibility(visibility);
    }
    public void setMoreImageVisibility(int visibility){
        mMoreImage.setVisibility(visibility);
    }
    public void setBackImageVisibility(int visibility){
        mBackImage.setVisibility(visibility);
    }
    public void setTitle(String title){
        mTitle.setText(title);
    }
    public void setBackOnclickListener(OnClickListener listener){
        mBack.setOnClickListener(listener);
    }
    public void setBackImageOnclickListener(OnClickListener listener){
        mBackImage.setOnClickListener(listener);
    }
    public void setMoreOnclickListener(OnClickListener listener){
        mMore.setOnClickListener(listener);
    }
    public void setMoreImageOnclickListener(OnClickListener listener){
        mMoreImage.setOnClickListener(listener);
    }
}
