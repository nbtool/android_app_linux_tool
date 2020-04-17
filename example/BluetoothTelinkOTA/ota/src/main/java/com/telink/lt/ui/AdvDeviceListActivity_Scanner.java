package com.telink.lt.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.telink.lt.R;
import com.telink.lt.adapter.DeviceListAdapter;
import com.telink.lt.ble.AdvDevice;
import com.telink.lt.util.Arrays;
import com.telink.lt.util.TelinkLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 广播设备列表页面
 * Created by Administrator on 2017/2/20.
 */
public class AdvDeviceListActivity_Scanner extends BaseActivity {

    private ListView lv_devices;
    private DeviceListAdapter mListAdapter;
    private List<AdvDevice> mDeviceList = new ArrayList<>();
    private final Handler mScanHandler = new Handler();
    private BluetoothAdapter mBluetoothAdapter;
    private final static long SCAN_PERIOD = 300 * 1000;
    private boolean mScanning = false;
    private BluetoothLeScanner scanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private EditText interval;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mBluetoothAdapter != null) {
                TelinkLog.w("mBluetoothAdapter state : " + mBluetoothAdapter.getState());
                mScanHandler.postDelayed(this, 1000);
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_device_list);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle("AdvDevices");
        }
        lv_devices = (ListView) findViewById(R.id.lv_devices);
        mListAdapter = new DeviceListAdapter(this, mDeviceList);
        lv_devices.setAdapter(mListAdapter);
        lv_devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(AdvDeviceListActivity_Scanner.this, DeviceDetailActivity.class)
                        .putExtra("device", mDeviceList.get(position)));
                /*if (mScanning) {
                    mScanning = false;
//                    mBluetoothAdapter.stopLeScan(leScanCallback);
                    scanner.stopScan(scanCallback);
                }*/
            }
        });

        interval = (EditText) findViewById(R.id.interval);

        if (!isSupport(getApplicationContext())) {
            Toast.makeText(this, "ble not support", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }
        scanner = mBluetoothAdapter.getBluetoothLeScanner();
        settings = new ScanSettings.Builder()
//                .setReportDelay(0)
//                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        filters = new ArrayList<ScanFilter>();
//        mScanHandler.postDelayed(runnable, 500);
//        scanToggle(true);

    }


    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            TelinkLog.d("scan:" + device.getName());
            for (AdvDevice advDevice : mDeviceList) {
                if (device.getAddress().equals(advDevice.device.getAddress())) return;
            }
            mDeviceList.add(new AdvDevice(device, rssi, scanRecord));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            TelinkLog.d("scan " + result.getDevice().getName() + " ---- " + Arrays.bytesToHexString(result.getScanRecord().getBytes(), ":"));
            for (AdvDevice advDevice : mDeviceList) {
                if (result.getDevice().getAddress().equals(advDevice.device.getAddress())) return;
            }
            mDeviceList.add(new AdvDevice(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes()));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        checkBleState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mScanning && mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
//            mBluetoothAdapter.stopLeScan(leScanCallback);
            scanner.stopScan(scanCallback);
        }
    }

    private void checkBleState() {
        if (this.mBluetoothAdapter != null
                && !this.mBluetoothAdapter.isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("开启蓝牙，体验智能灯!");
            builder.setCancelable(false);
            builder.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("enable", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    enable(getApplicationContext());
                }
            });
            builder.show();
        }
    }

    public boolean isSupport(Context context) {
        return this.getAdapter(context) != null;
    }

    public boolean enable(Context context) {
        BluetoothAdapter mAdapter = getAdapter(context);
        if (mAdapter == null)
            return false;
        if (mAdapter.isEnabled())
            return true;
        return mBluetoothAdapter.enable();
    }

    public BluetoothAdapter getAdapter(Context context) {
        if (mBluetoothAdapter == null) {
            BluetoothManager manager = (BluetoothManager) context
                    .getSystemService(Context.BLUETOOTH_SERVICE);
            this.mBluetoothAdapter = manager.getAdapter();
        }

        return this.mBluetoothAdapter;
    }

    private Runnable scanTask = new Runnable() {
        @Override
        public void run() {
            scanToggle(false);
        }
    };


    private void scanToggle(final boolean enable) {
        mScanHandler.removeCallbacks(scanTask);
        if (enable) {
            TelinkLog.i("ADV#scanner#startScan");
            scanner = mBluetoothAdapter.getBluetoothLeScanner();
            scanner.startScan(null, settings, scanCallback);
            mScanning = true;
            mDeviceList.clear();
            mListAdapter.notifyDataSetChanged();
//            mBluetoothAdapter.startLeScan(leScanCallback);
//            int inter = Integer.parseInt(interval.getText().toString());
            mScanHandler.postDelayed(scanTask, SCAN_PERIOD);
        } else {
            TelinkLog.i("ADV#scanToggle#stopScan");
            mScanning = false;
//            mBluetoothAdapter.stopLeScan(leScanCallback);
            scanner.stopScan(scanCallback);
            scanToggle(true);
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                scanToggle(true);
                break;
            case R.id.menu_stop:
                scanToggle(false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refresh(View view) {
//        scanToggle(true);
        scanner.flushPendingScanResults(scanCallback);
    }

}
