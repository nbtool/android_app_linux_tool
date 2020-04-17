package com.telink.lt.ui.file;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.telink.lt.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/8.
 */
public class FileListAdapter extends BaseAdapter {

    private Context mContext;
    private List<File> mFiles;

    public FileListAdapter(Context context) {
        this.mContext = context;
        mFiles = new ArrayList<>();
    }

    public void setData(List<File> files){
        this.mFiles = files;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFiles == null ? 0 : mFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return mFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_file_list, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.iv_right = (ImageView) convertView.findViewById(R.id.iv_right);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (mFiles.get(position).isDirectory()) {
            // 目录
            holder.iv_icon.setImageResource(R.drawable.folder);
            holder.iv_right.setVisibility(View.VISIBLE);
        } else {
            holder.iv_right.setVisibility(View.GONE);
            // 文件
            if (mFiles.get(position).getName().endsWith(".bin")) {
                holder.iv_icon.setImageResource(R.drawable.file_right);
            } else {
                holder.iv_icon.setImageResource(R.drawable.file);
            }
        }
        holder.tv_name.setText(mFiles.get(position).getName());
        return convertView;
    }

    class ViewHolder {
        public TextView tv_name;
        public ImageView iv_icon;
        public ImageView iv_right;
    }
}