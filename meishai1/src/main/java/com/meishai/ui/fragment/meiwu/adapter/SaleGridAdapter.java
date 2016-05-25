package com.meishai.ui.fragment.meiwu.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.Bargains;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;
import com.meishai.ui.fragment.meiwu.FindSaleWebviewActivity;

public class SaleGridAdapter extends BaseAdapter {

    private Context context;
    private List<Bargains> aBargains;
    private ImageLoader imageLoader = null;

    public SaleGridAdapter(Context mContext, List<Bargains> bargains) {
        super();
        this.context = mContext;
        this.aBargains = bargains;
        imageLoader = VolleyHelper.getImageLoader(mContext);
    }

    public void setaBargains(List<Bargains> aBargains) {
        this.aBargains = aBargains;
    }

    @Override
    public int getCount() {
        return aBargains.size();
    }

    @Override
    public Object getItem(int position) {
        return aBargains.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SaleViewHolder holder = null;
        if (null == convertView) {
            holder = new SaleViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.find_sale_item, null);
            holder.thumb = (NetworkImageView) convertView
                    .findViewById(R.id.thumb);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.discount = (TextView) convertView
                    .findViewById(R.id.discount);
            holder.to_taobao = (Button) convertView
                    .findViewById(R.id.to_taobao);
            convertView.setTag(holder);
        } else {
            holder = (SaleViewHolder) convertView.getTag();
        }
        Bargains bargains = aBargains.get(position);
        holder.thumb.setDefaultImageResId(R.drawable.ob_db);
        holder.thumb.setErrorImageResId(R.drawable.ob_db);
        holder.thumb.setImageUrl(bargains.getThumb(), imageLoader);
        holder.title.setText(bargains.getTitle());
        String findPrice = context.getString(R.string.find_price);
        holder.price.setText(String.format(findPrice, bargains.getPrice()));
        String findDiscount = context.getString(R.string.find_discount);
        holder.discount.setText(String.format(findDiscount,
                bargains.getDiscount()));
        holder.discount.getPaint().setAntiAlias(true);// 抗锯齿
        holder.discount.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.to_taobao.setTag(bargains);
        holder.to_taobao.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bargains bargains = (Bargains) v.getTag();
                Intent intent = new Intent(context,
                        FindSaleWebviewActivity.class);
                intent.putExtra("bargains", bargains);
                context.startActivity(intent);
            }
        });
        // holder.discount.getPaint().setFlags(
        // Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        return convertView;
    }

    class SaleViewHolder {
        private NetworkImageView thumb;
        private TextView title;
        private TextView price;
        private TextView discount;
        private Button to_taobao;
    }

}
