package com.telink.lt.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.telink.lt.R;
import com.telink.lt.ble.Device;
import com.telink.lt.ui.file.FileSelectActivity;
import com.telink.lt.util.TelinkLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * OTA page
 * Created by Administrator on 2017/3/2.
 */

public class OtaFragment extends BaseFragment implements View.OnClickListener {
    private TextView selectFile, info, progress;
    private Button startOta;
    private final static int REQUEST_CODE_GET_FILE = 1;
    private final static int MSG_PROGRESS = 11;
    private final static int MSG_INFO = 12;

    private String mPath;

    private Handler mInfoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_PROGRESS) {
                progress.setText(msg.obj + "%");
            } else if (msg.what == MSG_INFO) {
                info.append("\n" + msg.obj);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ota, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {
        selectFile = (TextView) view.findViewById(R.id.selectFile);
        info = (TextView) view.findViewById(R.id.info);
        progress = (TextView) view.findViewById(R.id.progress);
        startOta = (Button) view.findViewById(R.id.startOta);

        selectFile.setOnClickListener(this);
        startOta.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startOta:
                if (this.mPath == null || this.mPath.equals("")) {
                    Toast.makeText(getActivity(), "select firmware!", Toast.LENGTH_SHORT).show();
                    return;
                }

                byte[] firmware = readFirmware(this.mPath);
                if (firmware == null) {
                    toastMsg("firmware null");
                    return;
                }
                info.setText("start OTA");
                ((DeviceDetailActivity) getActivity()).getDevice().startOta(firmware);
                break;

            case R.id.selectFile:
                startActivityForResult(new Intent(getActivity(), FileSelectActivity.class), REQUEST_CODE_GET_FILE);
                break;
        }
    }


    private byte[] readFirmware(String fileName) {
        try {
            InputStream stream = new FileInputStream(fileName);
            int length = stream.available();
            byte[] firmware = new byte[length];
            stream.read(firmware);
            stream.close();
            return firmware;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK || requestCode != REQUEST_CODE_GET_FILE)
            return;

        this.mPath = data.getStringExtra("path");
        TelinkLog.d(mPath);
        File f = new File(mPath);
        selectFile.setText(f.toString());
    }

    public void onOtaStateChange(Device device, int state) {
        switch (state) {
            case Device.STATE_PROGRESS:
                TelinkLog.d("ota progress : " + device.getOtaProgress());
                mInfoHandler.obtainMessage(MSG_PROGRESS, device.getOtaProgress()).sendToTarget();
                break;
            case Device.STATE_SUCCESS:
                TelinkLog.d("ota success : ");
                mInfoHandler.obtainMessage(MSG_INFO, "ota success").sendToTarget();
                break;
            case Device.STATE_FAILURE:
                TelinkLog.d("ota failure : ");
                mInfoHandler.obtainMessage(MSG_INFO, "ota failure").sendToTarget();
                break;
        }
    }
}
