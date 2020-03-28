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
import android.view.View.OnClickListener;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	private final static String TAG = MainActivity.class.getSimpleName();

	private LeDeviceList mLeDeviceList;//自定义一个用来存储搜索到的蓝牙设备的List
	private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    //ListView
	private ListView lview;
	private List<Map<String, Object>> data_list;
	private SimpleAdapter sim_adapter;
	// ICON
	private int[] icon = { R.drawable.icon_01, R.drawable.icon_02,
			R.drawable.icon_03, R.drawable.icon_04, R.drawable.icon_05,
			R.drawable.icon_06, R.drawable.icon_07, R.drawable.icon_08,
			R.drawable.icon_09, R.drawable.icon_10, R.drawable.icon_11,
			R.drawable.icon_12, R.drawable.icon_13, R.drawable.icon_14 };

	private Button[] btn = new Button[8];

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btn[0] = (Button) findViewById(R.id.button1);
		btn[0].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				scanLeDevice();
			}
		});

        //init list view
        lview = (ListView) findViewById(R.id.listView1);
        data_list = new ArrayList<Map<String, Object>>();

		String[] from = { "image", "text" };
		int[] to = { R.id.image, R.id.text };
		sim_adapter = new SimpleAdapter(this, data_list, R.layout.item, from,
				to);

		lview.setAdapter(sim_adapter);
		lview.setOnItemClickListener(new ItemClickListener());

		mLeDeviceList = new LeDeviceList();
		// 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		
		//为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
	}
	
    // Start device discover with the BluetoothAdapter
	private void scanLeDevice() {
        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        mBluetoothAdapter.startLeScan(mLeScanCallback);
	}

	/*
	 * Device scan callback.
	 */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = 
        new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, 
                byte[] scanRecord) {
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
                mLeDevices.add(device);
                Log.i(TAG,device.getName()+" "+device.getAddress());

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("image", icon[0]);
                map.put("text", device.getAddress() + " " + device.getName());
                data_list.add(map);
                sim_adapter.notifyDataSetChanged();
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

    public void open_connect_activity(int index){
        final Intent intent = new Intent(this, DeviceControlActivity.class);

        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        
        //将所有搜索到的设备传输给DeviceControlActivity
        int num_device=mLeDeviceList.getCount();
        intent.putExtra("num_device",Integer.toString(num_device));
        for(int i=0;i<num_device;i++){
            final BluetoothDevice device=mLeDeviceList.getDevice(i);
            intent.putExtra("device_name: "+i, device.getName());
            intent.putExtra("device_address: "+i, device.getAddress());
        }

        startActivity(intent);
    }

	// 当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件
	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
													// click happened
				View arg1,// The view within the AdapterView that was clicked
				int arg2,// The position of the view in the adapter
				long arg3// The row id of the item that was clicked
		) {
			// 在本例中arg2=arg3
			HashMap<String, Object> item = (HashMap<String, Object>) arg0
					.getItemAtPosition(arg2);
			// 显示所选Item的ItemText
			setTitle((String) item.get("text"));// the item is map,you can
												// seethe function getData,if
												// want get the value, just use
												// .get(key) to get the value
            open_connect_activity(arg2);
        }
    }
}
