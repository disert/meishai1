package com.meishai.app.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.SKUResqData.Blurb;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ImageLoader.ImageListener;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;

/**
 * 美物-专场详情-item 2.0
 *
 * @author Administrator yl
 */
public class MeiWuSpecialShowItem extends LinearLayout {


    private ImageLoader mImageLoader;
    private Blurb mItem1;
    private Blurb mItem2;
    private Context mContext;
    private LinearLayout mRoot;
    private LinearLayout.LayoutParams mLp;

    public MeiWuSpecialShowItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();

    }

    public MeiWuSpecialShowItem(Context context) {
        this(context, null);
    }

    public void setData(Blurb item1, Blurb item2, ImageLoader imageLoader) {

        mItem1 = item1;
        mItem2 = item2;
        mImageLoader = imageLoader;
        setItem(mItem1, mItem2);
    }

    private void setItem(Blurb item1, Blurb item2) {
        mRoot.removeAllViews();
        initData(mItem1);
        if (mItem2 == null) {
            View view = new View(mContext);
            view.setLayoutParams(mLp);
            mRoot.addView(view);
        } else {
            initData(mItem2);
        }
    }

    private void initView() {
        View.inflate(mContext, R.layout.home_page_item, this);
        mRoot = (LinearLayout) findViewById(R.id.home_page_item_root);

        mLp = new LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
    }


    private void initData(Blurb item) {
        if (item == null) {
            return;
        }
        int childCount = mRoot.getChildCount();
        if (childCount == 2) {
            mRoot.removeAllViews();
        }

        View view = View.inflate(mContext, R.layout.meiwu_sku_item, null);
        view.setLayoutParams(mLp);
        ImageView image = (ImageView) view.findViewById(R.id.meiwu_sku_image);
        ImageView promise = (ImageView) view.findViewById(R.id.meiwu_sku_promise);
        TextView desc = (TextView) view.findViewById(R.id.meiwu_sku_desc);
        TextView price = (TextView) view.findViewById(R.id.meiwu_sku_price);
        view.findViewById(R.id.meiwu_sku_ispick).setVisibility(GONE);
        final TextView likeNum = (TextView) view.findViewById(R.id.meiwu_sku_like_num);


        view.setTag(item);

        //设置事件
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Blurb tag = (Blurb) v.getTag();
//	8			startPostShow(PostShowActivity.FROM_POST, tag.pid);
//				Intent intent = MeiWuGoodsDetailActivity.newIntent(tag.pid);
//				mContext.startActivity(intent);
                MeiWuReq.buyReq(mContext, tag.pid, tag.itemurl, tag.istao);
//				mContext.startActivity(MeishaiWebviewActivity.newIntent(tag.itemurl));
            }
        });


        //设置数据

        ImageListener listener1 = ImageLoader.getImageListener(image,
                R.drawable.place_default, R.drawable.place_default);
        mImageLoader.get(item.image, listener1);

        String space = "      ";
        if (item.ispromise == 1) {
            promise.setVisibility(VISIBLE);
            desc.setText(space + item.title);
        } else {
            promise.setVisibility(GONE);
            desc.setText(item.title);
        }
        price.setText(item.price);
        likeNum.setText(item.props);

        mRoot.addView(view);


    }


}
