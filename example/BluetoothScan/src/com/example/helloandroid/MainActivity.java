/*************************************************************************
	> File Name: src/com/example/helloandroid/MainActivity.java
	> Author: 
	> Mail: 
	> Created Time: 2018年09月17日 星期一 00时29分00秒
 ************************************************************************/

package com.example.helloandroid;

import android.app.Activity;
import android.os.Bundle;
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
    private BluetoothAdapter mBtAdapter;
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
			    canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2,150+mRSSIVector.get(i), mPaint.get(i)); //画圆圈
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
	    Paint paint1 = new Paint();
		paint1.setAntiAlias(true);
		paint1.setStyle(Style.STROKE);
	    paint1.setColor(Color.GREEN);
	    mPaint.add(paint1);
	    Paint paint2 = new Paint();
		paint2.setAntiAlias(true);
		paint2.setStyle(Style.STROKE);
	    paint2.setColor(Color.BLUE);
	    mPaint.add(paint2);
	    Paint paint3 = new Paint();
		paint3.setAntiAlias(true);
		paint3.setStyle(Style.STROKE);
	    paint3.setColor(Color.YELLOW);
	    mPaint.add(paint3);
	    Paint paint4 = new Paint();
		paint4.setAntiAlias(true);
		paint4.setStyle(Style.STROKE);
	    paint4.setColor(Color.WHITE);
	    mPaint.add(paint4);
	    Paint paint5 = new Paint();
		paint5.setAntiAlias(true);
		paint5.setStyle(Style.STROKE);
	    paint5.setColor(Color.LTGRAY);
	    mPaint.add(paint5);
	    Paint paint6 = new Paint();
		paint6.setAntiAlias(true);
		paint6.setStyle(Style.STROKE);
	    paint6.setColor(Color.CYAN);
	    mPaint.add(paint6);

        mSurface=(SurfaceView)findViewById(R.id.surface);
		mHolder = mSurface.getHolder();
		mHolder.addCallback(this);

        // Register for broadcasts when a device is discovered
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);
		// Register for broadcasts when discovery has finished
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);

		// Get the local Bluetooth adapter
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		doDiscovery();
    }

    // Start device discover with the BluetoothAdapter
	private void doDiscovery() {
	    // If we're already discovering, stop it
	    if (mBtAdapter.isDiscovering()) {
	        mBtAdapter.cancelDiscovery();
	    }
	    // Request discover from BluetoothAdapter
	    mBtAdapter.startDiscovery();
	}


    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    //【查找蓝牙设备】
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	Log.d("onReceive","OK");
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDevicesNameVector.add(device.getName());
                mDevicesAddrVector.add(device.getAddress());
                short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                mRSSIVector.add(rssi);
                Log.d("RSSI",device.getName()+"  "+String.valueOf(rssi));
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                if (mDevicesNameVector.size() != 0) {
                	Message msg = new Message();//消息
            		Bundle bundle = new Bundle();
                    bundle.clear();Log.d("onReceive","1");
                    msg.what = 0x01;//消息类别
                    bundle.putShort("msg",(short) 0);Log.d("onReceive","2");
        			msg.setData(bundle);Log.d("onReceive","3");
        			myHandler.sendMessage(msg);Log.d("onReceive","4");
                }
            }
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
