package com.example.bt4test;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	private final static String TAG = MainActivity.class.getSimpleName();

	private LeDeviceList mLeDeviceList;//自定义一个用来存储搜索到的蓝牙设备的List
	private BluetoothAdapter mBluetoothAdapter;
	private Handler mHandler;// 搜索时用于延时的
	private boolean mScanning;// 标记是否正在搜索
	private static final long SCAN_PERIOD = 10000; // 10秒后停止查找搜索.
    private static final int REQUEST_ENABLE_BT = 1;

	private Button[] btn = new Button[8];

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btn[0] = (Button) findViewById(R.id.button1);
		btn[0].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				scanLeDevice(true);
			}
		});

		btn[1] = (Button) findViewById(R.id.button2);
		btn[1].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btn2_click();
			}
		});

		
		mHandler = new Handler();
		mLeDeviceList = new LeDeviceList();
		// 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		
		//为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
	}
	
	/*
	 * 点击button将搜到的设备名和设备地址传送给DeviceControlActivity
	 */
	void btn2_click() {
	
	 	final Intent intent = new Intent(this, DeviceControlActivity.class);
	    
	    //将所有搜索到的设备传输给DeviceControlActivity
		int num_device=mLeDeviceList.getCount();
		intent.putExtra("num_device",Integer.toString(num_device));
		for(int i=0;i<num_device;i++){
			final BluetoothDevice device=mLeDeviceList.getDevice(i);
			intent.putExtra("device_name: "+i, device.getName());
			intent.putExtra("device_address: "+i, device.getAddress());
	    }
	    
	    if (mScanning) {
	        mBluetoothAdapter.stopLeScan(mLeScanCallback);
	        mScanning = false;
	    }

        Log.i("ABC",intent.toString());
	    startActivity(intent);
	}

	/*
	 * 用来搜索蓝牙设备的函数SCAN_PERIOD=10s周期
	 * 信息接收在mLeScanCallback回调函数中
	 */
	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					invalidateOptionsMenu();
				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	/*
	 * Device scan callback.
	 */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	mLeDeviceList.addDevice(device);
                }
            });
        }
    };
    
    /**
     * @author LiTao
     * 自定义的一个用来存储搜索到的蓝牙设备的一个类
     * Adapter for holding devices found through scanning.
     */
    private class LeDeviceList{
        private ArrayList<BluetoothDevice> mLeDevices;
 

        public LeDeviceList() {
            mLeDevices = new ArrayList<BluetoothDevice>();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                if("".equals(device.getName()))return;
                mLeDevices.add(device);
                Log.i(TAG,device.getName()+" "+device.getAddress());
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        public int getCount() {
            return mLeDevices.size();
        }
    }

}
