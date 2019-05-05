package com.example.second_test;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.beautifulzzzz.bluetooth.BlueToothCommunicate;
import com.beautifulzzzz.bluetooth.BlueToothConnect;
import com.beautifulzzzz.bluetooth.BlueToothSearch;

public class UI_Main extends Activity {
	
	private ProgressDialog mProgressDialog;
	private Button mButton1,mButton2,mButton3;
	private TextView mTextView;
	private byte[] buffer;
	
	public BlueToothSearch mBlueToothSearch;
	public BlueToothConnect mBlueToothConnect;
	public BlueToothCommunicate mBlueToothCommunicate;

	// 消息句柄(线程里无法进行界面更新，所以要把消息从线程里发送出来在消息句柄里进行处理)
	public Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0x00:
				break;//出现异常或为搜索到设备
			case 0x01:
				mProgressDialog.setTitle("进入尝试连接蓝牙设备阶段...");
				//当搜索完毕自动查找是否是我们的设备然后尝试连接
				boolean isFind=false;
				for(int i=0;i<mBlueToothSearch.mNameVector.size();i++){
					if(mBlueToothSearch.mNameVector.get(i).equals("HC-06")){
						Log.i("beautifulzzzz",mBlueToothSearch.mNameVector.get(i));
						mBlueToothConnect.setDevice(mBlueToothSearch.mAddrVector.get(i));
						mBlueToothConnect.start();
						isFind=true;
						break;
					}
				}
				if(isFind!=true)mProgressDialog.dismiss();//等待窗口关闭
				break;//搜索完毕
			case 0x02:
				mProgressDialog.setTitle("进入启动通信阶段...");
				//将上一步获得的socket传给蓝牙通信线程并启动线程监听数据
				mBlueToothCommunicate.setSocket(mBlueToothConnect.mmSocket);
				mBlueToothCommunicate.start();

				mProgressDialog.dismiss();//等待窗口关闭
				mButton1.setText("断开我的小风扇");
				mButton2.setEnabled(true);
				mButton3.setEnabled(true);
				break;//连接完毕
			case 0x03:break;
			case 0x04:break;
			case 0x10:
				if(mBlueToothSearch.getBT()==true && mButton1.getText().equals("打开蓝牙设备")){ 
					mButton1.setText("连接我的小风扇");
				}else if(mBlueToothSearch.getBT()==false){
					if(mBlueToothConnect!=null){
						mBlueToothConnect.cancel();
						mBlueToothConnect=null;
						mBlueToothConnect=new BlueToothConnect(myHandler);
					}
					if(mBlueToothCommunicate!=null){
						mBlueToothCommunicate.cancel();
						mBlueToothCommunicate=null;
						mBlueToothCommunicate=new BlueToothCommunicate(myHandler);
					}
					mButton1.setText("打开蓝牙设备");
					mButton2.setEnabled(false);
					mButton3.setEnabled(false);
				}
				break;//蓝牙状态改变
			default:break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_main);

		//控制命令帧格式（首尾为校验，第二：0xEE为设置速度，0xDD为获取速度，第三：速度值）
		buffer=new byte[4];
		buffer[0]=(byte) 0xFF;
		buffer[1]=(byte) 0xEE;
		buffer[2]=(byte) 0x00;
		buffer[3]=(byte) 0xAA;
		
		//实例化蓝牙三剑客（搜索、连接、通信）
		//myHandler是用来反馈信息的
		mBlueToothSearch=new BlueToothSearch(this, myHandler);
		mBlueToothConnect=new BlueToothConnect(myHandler);
		mBlueToothCommunicate=new BlueToothCommunicate(myHandler);
		
		mTextView = (TextView)findViewById(R.id.textView1);
		
		mButton1 = (Button) findViewById(R.id.button_start);
		if(mBlueToothSearch.getBT()==true) mButton1.setText("连接我的小风扇");
		else mButton1.setText("打开蓝牙设备");
		mButton1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mButton1.getText().equals("打开蓝牙设备")){
					mBlueToothSearch.clearVector();
					mBlueToothSearch.openBT();
					mButton1.setText("连接我的小风扇");
				}else if(mButton1.getText().equals("连接我的小风扇")){
					mBlueToothSearch.clearVector();
					mBlueToothSearch.doDiscovery();
					
					mProgressDialog = ProgressDialog.show(UI_Main.this,"进入搜索蓝牙设备阶段...", "稍等一下~", true);	
				}else{
					if(mBlueToothConnect!=null){
						mBlueToothConnect.cancel();
						mBlueToothConnect=null;
						mBlueToothConnect=new BlueToothConnect(myHandler);
					}
					if(mBlueToothCommunicate!=null){
						mBlueToothCommunicate.cancel();
						mBlueToothCommunicate=null;
						mBlueToothCommunicate=new BlueToothCommunicate(myHandler);
					}
					mButton1.setText("连接我的小风扇");
					mButton2.setEnabled(false);
					mButton3.setEnabled(false);
				}
			}
		});
		
		mButton2=(Button) findViewById(R.id.button_add);
		mButton2.setEnabled(false);
		mButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(buffer[2]<(byte) 0x0A){
					buffer[2]++;
					try {
						mBlueToothCommunicate.write(buffer);
						mTextView.setText(new Integer(buffer[2]).toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		mButton3=(Button) findViewById(R.id.button_cut);
		mButton3.setEnabled(false);
		mButton3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(buffer[2]>(byte) 0x00){
					buffer[2]--;
					try {
						mBlueToothCommunicate.write(buffer);
						mTextView.setText(new Integer(buffer[2]).toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}
