package com.telink.lt.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.telink.lt.R;
import com.telink.lt.ble.AdvDevice;
import com.telink.lt.ble.Device;
import com.telink.lt.ui.file.FileSelectActivity;
import com.telink.lt.util.TelinkLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * 设备详情
 * connect --> discoverService --> display
 * Created by Administrator on 2017/2/20.
 */
public class DeviceDetailActivity extends BaseActivity implements View.OnClickListener {
    private Device mDevice;
    private int mConnectState = BluetoothGatt.STATE_DISCONNECTED;

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
                if (msg.obj.equals("ota complete")){
                    Toast.makeText(DeviceDetailActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    showFinishDialog();
                }
            }
        }
    };

    private void showFinishDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage("OTA完成，是否返回扫描页面").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                setResult(RESULT_OK);
                finish();
            }
        });
        builder.show();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        Intent intent = getIntent();
        AdvDevice advDevice;
        if (intent.hasExtra("device")) {
            advDevice = intent.getParcelableExtra("device");
        } else {
            toastMsg("device null !");
            finish();
            return;
        }

        initViews();
        mDevice = new Device(advDevice.device, advDevice.scanRecord, advDevice.rssi);
        mDevice.setDeviceStateCallback(deviceCallback);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            if (advDevice.device != null) {
                actionBar.setTitle(advDevice.device.getName() == null || advDevice.device.getName().equals("")
                        ? "Unknown device" : advDevice.device.getName());
            }
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        connectToggle();
    }

    private void initViews() {
        selectFile = (TextView) findViewById(R.id.selectFile);
        info = (TextView) findViewById(R.id.info);
        progress = (TextView) findViewById(R.id.progress);
        startOta = (Button) findViewById(R.id.startOta);

        selectFile.setOnClickListener(this);
        startOta.setOnClickListener(this);
    }

    private void connectToggle() {
        TelinkLog.w(TAG + " # startConnect");
        if (mConnectState == BluetoothGatt.STATE_CONNECTED) {
            mDevice.disconnect();
        } else if (mConnectState == BluetoothGatt.STATE_DISCONNECTED) {
            mDevice.connect(this);
            mConnectState = BluetoothGatt.STATE_CONNECTING;
        }
        invalidateOptionsMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDevice != null) {
            mDevice.setDeviceStateCallback(null);
            if (this.mConnectState == BluetoothGatt.STATE_CONNECTED) {
                mDevice.disconnect();
            }
        }

    }

    public void showDiscoveringDialog() {
        showWaitingDialog("Discovering services...");
    }


    private Handler handler = new Handler();
    private Device.DeviceStateCallback deviceCallback = new Device.DeviceStateCallback() {
        @Override
        public void onConnected(Device device) {
            TelinkLog.w(TAG + " # onConnected");
            mConnectState = BluetoothGatt.STATE_CONNECTED;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDiscoveringDialog();
                    invalidateOptionsMenu();
                }
            });
           /* handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDevice.disconnect();
                }
            }, 5000);*/
        }

        @Override
        public void onDisconnected(Device device) {
            TelinkLog.w(TAG + " # onDisconnected");
            mConnectState = BluetoothGatt.STATE_DISCONNECTED;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mServiceListFragment.clearListData();
                    toastMsg("device disconnected");
                    invalidateOptionsMenu();
                    dismissWaitingDialog();
                }
            });
        }

        @Override
        public void onServicesDiscovered(Device device, final List<BluetoothGattService> services) {
            TelinkLog.w(TAG + " # onServicesDiscovered");
            UUID serviceUUID = null;
            for (BluetoothGattService service: services){
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()){
                    if (characteristic.getUuid().equals(Device.CHARACTERISTIC_UUID_WRITE)){
                        serviceUUID = service.getUuid();
                        break;
                    }
                }
            }

            if (serviceUUID != null){
                device.SERVICE_UUID = serviceUUID;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mServiceListFragment.clearListData();
//                    toastMsg("device onServicesDiscovered");
                    dismissWaitingDialog();

//                    invalidateOptionsMenu();
                }
            });
        }

        @Override
        public void onOtaStateChanged(Device device, int state) {
            TelinkLog.w(TAG + " # onOtaStateChanged");
            switch (state) {
                case Device.STATE_PROGRESS:
                    TelinkLog.d("ota progress : " + device.getOtaProgress());
                    mInfoHandler.obtainMessage(MSG_PROGRESS, device.getOtaProgress()).sendToTarget();
                    break;
                case Device.STATE_SUCCESS:
                    TelinkLog.d("ota success : ");
                    mInfoHandler.obtainMessage(MSG_INFO, "ota complete").sendToTarget();
                    break;
                case Device.STATE_FAILURE:
                    TelinkLog.d("ota failure : ");
                    mInfoHandler.obtainMessage(MSG_INFO, "ota failure").sendToTarget();
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device_detail, menu);
        if (mConnectState == BluetoothGatt.STATE_CONNECTED) {
            menu.findItem(R.id.menu_connect_state).setTitle(R.string.state_connected);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else if (mConnectState == BluetoothGatt.STATE_DISCONNECTED) {
            menu.findItem(R.id.menu_connect_state).setTitle(R.string.state_disconnected);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else if (mConnectState == BluetoothGatt.STATE_CONNECTING) {
            menu.findItem(R.id.menu_connect_state).setTitle(R.string.state_connecting);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect_state:
                connectToggle();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Device getDevice() {
        return mDevice;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startOta:
                if (mConnectState != BluetoothGatt.STATE_CONNECTED) {
                    Toast.makeText(this, "device disconnected!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (this.mPath == null || this.mPath.equals("")) {
                    Toast.makeText(this, "select firmware!", Toast.LENGTH_SHORT).show();
                    return;
                }

                byte[] firmware = readFirmware(this.mPath);
                if (firmware == null) {
                    toastMsg("firmware null");
                    return;
                }
                info.setText("start OTA");
                mDevice.startOta(firmware);
                break;

            case R.id.selectFile:
                /*if (mConnectState != BluetoothGatt.STATE_CONNECTED){
                    Toast.makeText(this, "device disconnected!", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                startActivityForResult(new Intent(this, FileSelectActivity.class), REQUEST_CODE_GET_FILE);
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


}
