package com.broadcast.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by Administrator on 2/2/2017.
 */
public class ChatStickersAdapter extends BaseAdapter {
    private Context mContext;
    private Integer[] mStickerIds;

    // Constructor
    public ChatStickersAdapter(Context c, Integer[] mStickerIds) {
        mContext = c;
        this.mStickerIds = mStickerIds;
    }

    @Override
    public int getCount() {
        if (mStickerIds != null) {
            return mStickerIds.length;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(mStickerIds[position]);
        return imageView;
    }

}

