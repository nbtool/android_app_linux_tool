package com.beautifulzzzz.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

public class BlueToothConnect extends Thread {

	private BluetoothAdapter mBtAdapter;// ¿∂—¿  ≈‰∆˜
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public BluetoothSocket mmSocket;
	private BluetoothDevice mmDevice;
	private Handler mHandler;


	public BlueToothConnect(Handler mHandler) {
		this.mHandler = mHandler;
	}
	
	public void setDevice(String Addr){
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		mmDevice = mBtAdapter.getRemoteDevice(Addr);
		try {
			mmSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
		}
	}

	public void run() {
		setName("ConnectThread");
		try {
			mmSocket.connect();
		} catch (IOException e) {
			try {
				mmSocket.close();
			} catch (IOException e2) {
				
			}
			return;
		}
		//¿∂—¿¡¨Ω”ÕÍ±œ∑¢ÀÕ0x02msg
		Message msg=new Message();
		msg.what = 0x02;
		mHandler.sendMessage(msg);
	}

	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) {
		}
	}
}
