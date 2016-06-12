package com.meishai.ui.fragment.message.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.meishai.R;
import com.meishai.app.widget.layout.MessageCommItemView;
import com.meishai.app.widget.layout.MessageItemView;
import com.meishai.entiy.MessageRespBean;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;

/**
 * 主界面 精选fragment对应的适配器 2.0
 *
 * @author Administrator yl
 */
public class MessageAdapter extends BaseAdapter {
    private final static int TYPE_MESSAGE = 0;//消息
    private final static int TYPE_ITEM = 1;//数据

    private Context mContext;
    private ImageLoader mImageLoader;

    private MessageRespBean mData;



    public MessageAdapter(Context context, ImageLoader imageLoader) {
        mContext = context;
        mImageLoader = imageLoader;
    }


    /**
     * 初始化数据
     *
     * @param homeInfo
     */
    public void setData(MessageRespBean homeInfo) {
        if(homeInfo == null){
            return;
        }
        if(mData == null) {
            mData = homeInfo;
        }
        if(homeInfo.page > mData.page){
            mData.getList().addAll(homeInfo.getList());
        }
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if(mData == null || mData.getList() == null || mData.getList().isEmpty()){
            return 0;
        }

        return mData.getList().size() + 5;
    }

    @Override
    public Object getItem(int position) {
        if(position < 5){
            return "";
        }
        return mData.getList().get(position - 5);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(position < 5){
            return TYPE_MESSAGE;
        }else {
            return TYPE_ITEM;
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object item = getItem(position);
        switch (getItemViewType(position)) {

            case TYPE_MESSAGE:
                switch (position){
                    case 0:
                        convertView = initMessage(convertView,R.drawable.icon_new_fans,mContext.getString(R.string.new_fans),mData.getFans_num());
                        break;
                    case 1:
                        convertView = initMessage(convertView,R.drawable.icon_new_comm,mContext.getString(R.string.new_comm),mData.getCom_num());
                        break;
                    case 2:
                        convertView = initMessage(convertView,R.drawable.icon_new_zan,mContext.getString(R.string.new_zan),mData.getZan_num());
                        break;
                    case 3:
                        convertView = initMessage(convertView,R.drawable.icon_new_collect,mContext.getString(R.string.new_collect),mData.getFav_num());
                        break;
                    case 4:
                        convertView = initMessage(convertView,R.drawable.icon_new_notice,mContext.getString(R.string.new_notify),mData.getNotice_num());
                        break;
                }
                break;
            case TYPE_ITEM:
                MessageRespBean.ListBean bean = (MessageRespBean.ListBean) item;
                MessageCommItemView itemView;
                if(convertView == null){
                    itemView = new MessageCommItemView(mContext);
                    convertView = itemView;
                }else {
                    itemView = (MessageCommItemView) convertView;
                }


                if(position == 5){
                    itemView.mHead.setVisibility(View.VISIBLE);
                }else {
                    itemView.mHead.setVisibility(View.GONE);
                }

                itemView.mImage.setTag(bean.getImage());
                ListImageListener listener = new ListImageListener(itemView.mImage,
                        R.drawable.head_default, R.drawable.head_default,
                        bean.getImage());
                mImageLoader.get(bean.getImage(), listener);

                itemView.mTitle.setText(bean.getTitle());
                itemView.mBrowseNum.setText(bean.getView_num()+"");
                itemView.mLikeNum.setText(bean.getFollow_num()+"");

            default:
                break;
        }

        return convertView;
    }

    private View initMessage(View convertView,int iconResId , String text , int num){
        MessageItemView itemView;
        if(convertView == null){
            itemView = new MessageItemView(mContext);
            convertView = itemView;
        }else {
            itemView = (MessageItemView) convertView;
        }
        itemView.mIcon.setImageResource(iconResId);
        itemView.mText.setText(text);
        if(num != 0){
            itemView.mNum.setVisibility(View.VISIBLE);
            itemView.mNum.setBackgroundColor(mContext.getResources().getColor(R.color.red));
            itemView.mNum.setText(num+"");
        }else {
            itemView.mNum.setVisibility(View.GONE);

        }


        return convertView;
    }

}
