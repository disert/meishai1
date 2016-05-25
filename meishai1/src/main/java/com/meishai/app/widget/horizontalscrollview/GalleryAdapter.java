package com.meishai.app.widget.horizontalscrollview;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.album.utils.ImageLoader;
import com.album.utils.ImageLoader.Type;
import com.meishai.R;
import com.meishai.entiy.ImageData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.toolbox.ListImageListener;

public class GalleryAdapter extends
        RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    public interface OnDelClickLitener {
        void onDelClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    private OnDelClickLitener mDelClickLitener;
    private com.meishai.net.volley.toolbox.ImageLoader mImageLoader = null;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public void setOnDelClickLitener(OnDelClickLitener mDelClickLitener) {
        this.mDelClickLitener = mDelClickLitener;
    }

    private LayoutInflater mInflater;
    private List<ImageData> mDatas;
    private int itemResid = R.layout.activity_index_gallery_item;

    public GalleryAdapter(Context context, List<ImageData> datats) {
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
        mImageLoader = VolleyHelper.getImageLoader(context);
    }

    public GalleryAdapter(Context context, List<ImageData> datats, int itemResid) {
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
        mImageLoader = VolleyHelper.getImageLoader(context);
        this.itemResid = itemResid;
    }

    public void setImgDatas(List<ImageData> mDatas) {
        this.mDatas = mDatas;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }

        ImageView mImg;
        ImageButton mIbDel;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(itemResid, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mImg = (ImageView) view
                .findViewById(R.id.id_index_gallery_item_image);
        viewHolder.mIbDel = (ImageButton) view.findViewById(R.id.ib_del);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        ImageData data = mDatas.get(i);
        // if (0 == i) {
        // RelativeLayout.LayoutParams layoutParams = (LayoutParams)
        // viewHolder.mImg
        // .getLayoutParams();
        // layoutParams.leftMargin = 1;
        // }
        if (ImageData.IMAGE_DEF == data.getType()) {
            viewHolder.mImg.setImageResource(R.drawable.release_camera);
            viewHolder.mIbDel.setVisibility(View.GONE);
        } else if (ImageData.IMAGE_LOCAL == data.getType()) {
            ImageLoader.getInstance(3, Type.LIFO).loadImage(data.getPath(),
                    viewHolder.mImg);
            viewHolder.mIbDel.setVisibility(View.VISIBLE);
        } else if (ImageData.IMAGE_NETWORK == data.getType()) {
            viewHolder.mIbDel.setVisibility(View.VISIBLE);
            viewHolder.mImg.setTag(data.getPath());
            ListImageListener listener = new ListImageListener(viewHolder.mImg,
                    R.drawable.head_default, R.drawable.head_default,
                    data.getPath());
            mImageLoader.get(data.getPath(), listener);
        }
        if (mDelClickLitener != null) {
            viewHolder.mIbDel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mDelClickLitener.onDelClick(v, i);
                }
            });
        }

        if (mOnItemClickLitener != null) {
            viewHolder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(viewHolder.itemView, i);
                }
            });
        }
    }
}