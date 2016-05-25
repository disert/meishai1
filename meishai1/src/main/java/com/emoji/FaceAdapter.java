package com.emoji;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.meishai.R;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;

public class FaceAdapter extends BaseAdapter {
    private List<MsgEmojiModle> data;
    private LayoutInflater inflater;
    private int size = 0;
    private ImageLoader imageLoader = null;

    public FaceAdapter(Context context, List<MsgEmojiModle> list,
                       ImageLoader imageLoader) {
        this.inflater = LayoutInflater.from(context);
        this.data = list;
        this.size = list.size();
        this.imageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return this.size;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MsgEmojiModle emoji = data.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.face_item, null);
            viewHolder.iv_face = (NetworkImageView) convertView
                    .findViewById(R.id.face_iv);
            viewHolder.face_img = (ImageView) convertView
                    .findViewById(R.id.face_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (emoji.getId() == R.drawable.face_delete_select) {
            convertView.setBackgroundDrawable(null);
            viewHolder.iv_face.setVisibility(View.GONE);
            viewHolder.face_img.setVisibility(View.VISIBLE);
            viewHolder.face_img.setImageResource(emoji.getId());
        } else if (TextUtils.isEmpty(emoji.getText())) {
            convertView.setBackgroundDrawable(null);
            viewHolder.iv_face.setImageDrawable(null);
        } else {
            viewHolder.iv_face.setTag(emoji);
            viewHolder.iv_face.setImageUrl(emoji.getFace(), imageLoader);
            // viewHolder.iv_face.setImageResource(emoji.getId());
        }

        return convertView;
    }

    class ViewHolder {

        public NetworkImageView iv_face;
        public ImageView face_img;
    }
}