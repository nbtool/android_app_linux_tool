package com.telink.lt.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
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
public class AdvDeviceListActivity extends BaseActivity {

    private ListView lv_devices;
    private DeviceListAdapter mListAdapter;
    private List<AdvDevice> mDeviceList = new ArrayList<>();
    private final Handler mScanHandler = new Handler();
    private BluetoothAdapter mBluetoothAdapter;
    private final static long SCAN_PERIOD = 10 * 1000;
    private boolean mScanning = false;
    private static final int REQUEST_DETAIL = 1;

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
                startActivityForResult(new Intent(AdvDeviceListActivity.this, DeviceDetailActivity.class)
                        .putExtra("device", mDeviceList.get(position)), REQUEST_DETAIL);
                if (mScanning) {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(scanCallback);
                }
            }
        });

        if (!isSupport(getApplicationContext())) {
            Toast.makeText(this, "ble not support", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }
//        scanToggle(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DETAIL && resultCode == RESULT_OK){
            scanToggle(true);
        }
    }

    private BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            TelinkLog.w("scan:" + device.getName() + " mac:" + device.getAddress() + " rssi:" + rssi + " record:  " + Arrays.bytesToHexString(scanRecord, ":"));
            for (final AdvDevice advDevice : mDeviceList) {
                if (device.getAddress().equals(advDevice.device.getAddress())) {
                    if (advDevice.rssi != rssi) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                advDevice.rssi = rssi;
                                mListAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                    return;
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDeviceList.add(new AdvDevice(device, rssi, scanRecord));
                    mListAdapter.notifyDataSetChanged();
                }
            });
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
            mBluetoothAdapter.stopLeScan(scanCallback);
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
        synchronized (this) {
            if (mBluetoothAdapter == null) {
                BluetoothManager manager = (BluetoothManager) context
                        .getSystemService(Context.BLUETOOTH_SERVICE);
                this.mBluetoothAdapter = manager.getAdapter();
            }
        }

        return this.mBluetoothAdapter;
    }

    private Runnable scanTask = new Runnable() {
        @Override
        public void run() {
            if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
                TelinkLog.i("ADV#stopScan");
                mScanning = false;
                mBluetoothAdapter.stopLeScan(scanCallback);
                invalidateOptionsMenu();
            }
        }
    };

    private int scanDelay = 0 * 1000;

    private void scanToggle(final boolean enable) {
        mScanHandler.removeCallbacks(scanTask);
        if (enable) {
            TelinkLog.i("ADV#startScan");
            mScanning = true;
            mDeviceList.clear();
            mListAdapter.notifyDataSetChanged();
            mScanHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.startLeScan(scanCallback);
                    mScanHandler.postDelayed(scanTask, SCAN_PERIOD);
                }
            }, scanDelay);
        } else {
            TelinkLog.i("ADV#scanToggle#stopScan");
            mScanning = false;
            mBluetoothAdapter.stopLeScan(scanCallback);
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
        BluetoothManager manager = (BluetoothManager) this
                .getSystemService(Context.BLUETOOTH_SERVICE);
        List<BluetoothDevice> devices = manager.getConnectedDevices(BluetoothProfile.GATT);
        Toast.makeText(this, "当前连接设备个数" + devices.size(), Toast.LENGTH_SHORT).show();

    }
}
