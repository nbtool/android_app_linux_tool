/*************************************************************************
	> File Name: src/com/example/helloandroid/MainActivity.java
	> Author: 
	> Mail: 
	> Created Time: 2018年12月08日 星期六 00时29分00秒
 ************************************************************************/

package com.example.helloandroid;

import android.app.Activity;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanFilter;
import android.content.BroadcastReceiver;
import android.text.method.ScrollingMovementMethod;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.Boolean;
import java.util.Formatter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TextView;


public class MainActivity extends Activity{

    private BluetoothAdapter mBluetoothAdapter;

    private final static int REQUEST_ENABLE_BT = 1;
    //public static String UUID_SERVICE = "00010203-0405-0607-0809-0A0B0C0D1910";
    //private Message msg ;java.util.List
    //private Bundle bundle;
    private TextView tv;

    private Vector<String> mDevicesInfoVector;
    private Vector<String> mDevicesAddrVector;
    private Vector<Short>  mRSSIVector;
    private Vector<Paint>  mPaint;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv1);
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());

		mDevicesInfoVector=new Vector<String>();//向量
        mDevicesAddrVector= new Vector<String>();
		mRSSIVector=new Vector<Short>();

		// Get the local Bluetooth adapter
        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        doDiscovery();
    }

    // Start device discover with the BluetoothAdapter
	private void doDiscovery() {
	    // If we're already discovering, stop it
	    if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
	    }
	    // Request discover from BluetoothAdapter
        //use filter not work!!!!!!!!!!
        //UUID[] uuid_arrays = new UUID[1];
        //uuid_arrays[0] = ParcelUuid.fromString(UUID_SERVICE).getUuid();
        //mBluetoothAdapter.startLeScan(uuid_arrays,mLeScanCallback);
        //Log.d("RSSI",uuid_arrays[0].toString() + "  " + UUID.randomUUID().toString());
        mBluetoothAdapter.startLeScan(mLeScanCallback);
	}

    //device filter 
    private Boolean device_filter(BluetoothDevice device){
        Pattern pattern = Pattern.compile("^BC:23:4C:.*");  
        Matcher matcher = pattern.matcher(device.getAddress()); 
        //Log.d("RSSI", matcher.matches() == true ? "T":"F");
        return matcher.matches();
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
        new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi,
                    byte[] scanRecord) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(device_filter(device)){
                            //mDevicesNameVector.add(device.getName());
                            //mDevicesAddrVector.add(device.getAddress());
                            //mRSSIVector.add((short)rssi);
                            //Log.d("RSSI",device.getAddress() + " " + device.getName() + " " + String.valueOf(rssi));
                            
                            Formatter formatter = new Formatter();
                            formatter.format("-----------------------------\r\n");
                            formatter.format("MAC:          %s\r\n", device.getAddress());
                            formatter.format("NAME:         %s\r\n", device.getName());
                            formatter.format("RSSI:         %d\r\n", rssi);
                            formatter.format("BIG_KIND:     %02X\r\n", scanRecord[38]);
                            formatter.format("SMALL_KIND:   %02X\r\n", scanRecord[37]);
                            formatter.format("NODE_ID:      %02d\r\n", scanRecord[40]);
                            formatter.format("PID:          %s\r\n", new String(scanRecord, 42, 8));
                            formatter.format("VERSION:      %c.%c\r\n", scanRecord[50], scanRecord[51]);
                            Log.d("RSSI",formatter.toString());
                            //tv.append(formatter.toString());

                            int index = mDevicesAddrVector.indexOf(device.getAddress());
                            if(index == -1){//NEW
                                mDevicesAddrVector.add(device.getAddress());
                                mDevicesInfoVector.add(formatter.toString());
                                Log.d("RSSI","XXXX -1");
                            }else{//OLD
                                mDevicesInfoVector.set(index,formatter.toString());
                                Log.d("RSSI",String.valueOf(index));
                            }
                            
                            tv.setText(mDevicesInfoVector.toString());


                       

                          
                            //for (byte b : scanRecord) {
                            //    formatter.format("%02x", b);
                            //}
                            //String hex = formatter.toString();
                           // Log.d("RSSI",hex);
                        }
                    }
                });
            }
        };
}
