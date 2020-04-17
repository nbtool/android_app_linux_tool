package com.telink.lt.ui.file;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.telink.lt.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 本地文件选择
 * Created by Administrator on 2017/6/2.
 */

public class LocalFileSelectFragment extends Fragment {
    private ListView lv_file;
    private TextView tv_parent;
    private TextView tv_cur_name; // 当前目录名称
    private FileListAdapter mAdapter;
    private List<File> mFiles = new ArrayList<>();
    private File mCurrentDir;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_file_select, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lv_file = (ListView) view.findViewById(R.id.lv_file);
        tv_cur_name = (TextView) view.findViewById(R.id.tv_cur_name);
        tv_parent = (TextView) view.findViewById(R.id.tv_parent);
        tv_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentDir = mCurrentDir.getParentFile();
                update();
            }
        });

        mCurrentDir = Environment.getExternalStorageDirectory();
//        mCurrentDir = getFilesDir();
        mAdapter = new FileListAdapter(getActivity());
        lv_file.setAdapter(mAdapter);
        lv_file.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mFiles.get(position).isDirectory()) {
                    mCurrentDir = mFiles.get(position);
                    update();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("path", mFiles.get(position).getAbsolutePath());
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
//                    Toast.makeText(FileSelectActivity.this, mFiles.get(position).getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        update();
    }


    private void update() {
        if (mCurrentDir.getParentFile() != null) {
            tv_parent.setVisibility(View.VISIBLE);
        } else {
            tv_parent.setVisibility(View.INVISIBLE);
        }
        tv_cur_name.setText(mCurrentDir.toString());
        File[] files = mCurrentDir.listFiles();
        if (files == null) {
            mFiles.clear();
        } else {
            mFiles = new ArrayList<>(Arrays.asList(files));

            // 排序
            Collections.sort(mFiles, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (o1.isDirectory() && o2.isFile())
                        return -1;
                    if (o1.isFile() && o2.isDirectory())
                        return 1;
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }
        mAdapter.setData(mFiles);
    }

}
