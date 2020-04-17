package com.telink.lt.ui.file;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.telink.lt.R;
import com.telink.lt.ui.WaitingDialog;
import com.telink.lt.util.TelinkLog;
import com.telink.lt.web.TelinkHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 网盘文件选择
 * Created by Administrator on 2017/6/2.
 */

public class WebFileSelectFragment extends Fragment implements View.OnClickListener, Callback {
    private ListView lv_file;
    private FileListAdapter mAdapter;
    private List<File> mFiles = new ArrayList<>();
    private File fileDir;
    private EditText et_file_name;

    private WaitingDialog mWaitingDialog;


    protected void showWaitingDialog(String tip) {
        if (mWaitingDialog == null) {
            mWaitingDialog = new WaitingDialog(getActivity());
        }
        mWaitingDialog.setWaitingText(tip);
        if (!mWaitingDialog.isShowing()) {
            mWaitingDialog.show();
        }
    }

    protected void dismissWaitingDialog() {
        if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
            mWaitingDialog.dismiss();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_file_select, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lv_file = (ListView) view.findViewById(R.id.lv_file);

        fileDir = new File(getActivity().getFilesDir().getPath() + File.separator + "bin");

//        fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "telink_ota_sdk310"+ File.separator + "bin");
        TelinkLog.d("fileDir:" + fileDir.getAbsolutePath());
        mAdapter = new FileListAdapter(getActivity());
        lv_file.setAdapter(mAdapter);
        lv_file.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("path", mFiles.get(position).getAbsolutePath());
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });

        et_file_name = (EditText) view.findViewById(R.id.et_file_name);

        view.findViewById(R.id.download).setOnClickListener(this);
        update();
    }


    private void update() {
        File[] files = fileDir.listFiles();
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

    private int failCount = 0;
    String fileName = "";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download:
                failCount = 0;
                fileName = et_file_name.getText().toString().trim();
                try {
                    String url = TelinkHttpClient.Base_Url_0.replace(TelinkHttpClient.Replace_Element, fileName);
                    TelinkLog.d("download url:" + url);
                    TelinkHttpClient.getInstance().downloadFile(url, "download", this);
                    Toast.makeText(getActivity(), "正在下载文件", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void retry() {
        if (failCount > 1){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissWaitingDialog();
                    Toast.makeText(getActivity(), "文件下载失败", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        TelinkLog.d("WebFileSelectFragment#retry");
        try {
            String url = TelinkHttpClient.Base_Url_1.replace(TelinkHttpClient.Replace_Element, fileName);
            TelinkLog.d("download url:" + url);
            TelinkHttpClient.getInstance().downloadFile(url, "download", this);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onFailure(Request request, IOException e) {
        TelinkLog.d("WebFileSelectFragment#onFailure");
        failCount++;
        retry();
    }

    @Override
    public void onResponse(Response response) throws IOException {
        TelinkLog.d("WebFileSelectFragment#onResponse");


        if (response.isSuccessful()) {
            byte[] body = response.body().bytes();
            if (!isCorrect(body))return;
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            File file = new File(fileDir, fileName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            byte[] buf = new byte[2048];
            int len = 0;
            FileOutputStream fos = null;
            try {
                long total = response.body().contentLength();

                TelinkLog.e("DownloadBin#Total------>" + total);

                fos = new FileOutputStream(file);
                /*while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }*/
                fos.write(body);
                fos.flush();

                TelinkLog.w("bin 文件下载成功 onResponse");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissWaitingDialog();
                        Toast.makeText(getActivity(), "文件下载成功", Toast.LENGTH_SHORT).show();
                        update();
                    }
                });
            } catch (IOException e) {

                failCount++;
                retry();
                TelinkLog.w("bin 文件下载失败 onResponse");
                TelinkLog.e(e.toString());
            } finally {
                try {
                    /*if (is != null) {
                        is.close();
                    }*/
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    TelinkLog.e(e.toString());
                }
            }
        } else {
            failCount++;
            retry();
        }
    }

    private boolean isCorrect(byte[] bytes)  {
        if (bytes.length < 28) {
            return false;
        }
        byte[] lenBytes = new byte[4];
        System.arraycopy(bytes, 24, lenBytes, 0, 4);
        int len = com.telink.lt.util.Arrays.bytesToInt(lenBytes, 0);
        if (bytes.length == len) {
            TelinkLog.w("Download file correct");
            return true;
        } else {
            TelinkLog.w("Download file error");
            return false;
        }
    }
}
