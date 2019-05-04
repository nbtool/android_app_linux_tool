package com.beautifulzzzz.bluetooth;

import java.util.Vector;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BlueToothSearch {
	public BluetoothAdapter mBtAdapter;// 蓝牙适配器
	private static final int ENABLE_BLUETOOTH = 10;
	// 分别用于存储设备名地址名称和RSSI的向量
	public Vector<String> mNameVector;
	public Vector<String> mAddrVector;

	public boolean BTState;

	private Handler mHandler;
	private Activity activity;

	public BlueToothSearch(Activity activity, Handler mHandler) {
		this.mHandler = mHandler;
		this.activity = activity;

		mNameVector = new Vector<String>();// 向量
		mAddrVector = new Vector<String>();

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		activity.registerReceiver(mReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		activity.registerReceiver(mReceiver, filter);
		activity.registerReceiver(mReceiver, filter);

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		new BTStateThread().start();// 蓝牙状态监听
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				mNameVector.add(device.getName());
				mAddrVector.add(device.getAddress());

				Log.i("beautifulzzzz_BTS", "find a device: " + device.getName()
						+ " " + device.getAddress());
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				// 蓝牙搜索完毕发送0x01msg
				Message msg = new Message();
				msg.what = 0x01;
				mHandler.sendMessage(msg);

				Log.i("beautifulzzzz_BTS", "discovery finished");
			}
		}
	};

	public void doDiscovery() {
		if (mBtAdapter.isDiscovering()) {
			mBtAdapter.cancelDiscovery();
		}
		mBtAdapter.startDiscovery();
	}

	public boolean getBT() {
		return BTState;
	}

	public void openBT() {
		// 如果没有打开则打开
		if (!mBtAdapter.isEnabled()) {
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			activity.startActivityForResult(intent, ENABLE_BLUETOOTH);
		}
	}

	public void clearVector() {
		mNameVector.clear();
		mAddrVector.clear();
	}

	class BTStateThread extends Thread {
		public void run() {
			boolean oldBTState;
			while (true) {
				try {
					Thread.sleep(1000);
					oldBTState = BTState;
					BTState = mBtAdapter.isEnabled();
					if (oldBTState != BTState) {// 一旦蓝牙状态改变就发送消息
						// 蓝牙状态改变发送0x10消息
						Message msg = new Message();
						msg.what = 0x10;
						mHandler.sendMessage(msg);
					}
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
