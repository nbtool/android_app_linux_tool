/*************************************************************************
	> File Name: src/com/example/helloandroid/MainActivity.java
	> Author: 
	> Mail: 
	> Created Time: 2018年12月08日 星期六 00时29分00秒
 ************************************************************************/

package com.example.helloandroid;

import android.app.Activity;
import android.os.Bundle;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import java.util.Vector;
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


public class MainActivity extends Activity implements Callback{

    private SurfaceView mSurface;
    private SurfaceHolder mHolder;
    private BluetoothAdapter mBluetoothAdapter;

    private final static int REQUEST_ENABLE_BT = 1;


    //private Message msg ;
    //private Bundle bundle;

    private Vector<String> mDevicesNameVector;
    private Vector<String> mDevicesAddrVector;
    private Vector<Short>  mRSSIVector;
    private Vector<Paint>  mPaint;
    
    //消息句柄(线程里无法进行界面更新，所以要把消息从线程里发送出来在消息句柄里进行处理)
	public Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) 
		{
			Bundle bundle = msg.getData();
			short now = bundle.getShort("msg");
			Log.d("onGet",String.valueOf(now));
			if (msg.what == 0x01) 
			{
				draw();
			}
			doDiscovery();
		}
		//画图像
		private void draw() { 
			Canvas canvas = mHolder.lockCanvas(); 
			canvas.drawRGB(0, 0, 0);
			
			for(int i=(mRSSIVector.size() > 7 ? 7:mRSSIVector.size())-1;i>=0;i--)
			{
                int iRssi = Math.abs(mRSSIVector.get(i));
				float power = (float) ((iRssi-59)/(10*2.0));
				float dis=(float) Math.pow(10, power);

                canvas.drawText(mDevicesAddrVector.get(i)+">>  RSSI: "+mRSSIVector.get(i).toString()+"  距离: "+ dis, 5, 12*i+12, mPaint.get(i));
			    canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2,150+mRSSIVector.get(i), mPaint.get(0)); //画圆圈
			}
	        mHolder.unlockCanvasAndPost(canvas);// 更新屏幕显示内容  
	        mRSSIVector.clear();
	        mDevicesNameVector.clear();
            mDevicesAddrVector.clear();
	    } 
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //msg = new Message();//消息
		///bundle = new Bundle();

		mDevicesNameVector=new Vector<String>();//向量
        mDevicesAddrVector= new Vector<String>();
		mRSSIVector=new Vector<Short>();
		mPaint=new Vector<Paint>();
		Paint paint0 = new Paint();
		paint0.setAntiAlias(true);
		paint0.setStyle(Style.STROKE);
	    paint0.setColor(Color.RED);
	    mPaint.add(paint0);

        mSurface=(SurfaceView)findViewById(R.id.surface);
		mHolder = mSurface.getHolder();
		mHolder.addCallback(this);

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
        //    public static String CONTROL_POINT = "00002a52-0000-1000-8000-00805f9b34fb";//����д����
        ScanFilter filter = new ScanFilter.Builder().setServiceUuid(
                ParcelUuid.fromString(serviceUUIDs[i].toString())).build();
        mBluetoothAdapter.startLeScan(mLeScanCallback);
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
                        mDevicesNameVector.add(device.getName());
                        mDevicesAddrVector.add(device.getAddress());
                        mRSSIVector.add((short)rssi);
                        Log.d("RSSI",device.getName()+"  "+String.valueOf(rssi));
                    }
                });
            }
        };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }
}
