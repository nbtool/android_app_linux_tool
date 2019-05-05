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

	// ��Ϣ���(�߳����޷����н�����£�����Ҫ����Ϣ���߳��﷢�ͳ�������Ϣ�������д���)
	public Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0x00:
				break;//�����쳣��Ϊ�������豸
			case 0x01:
				mProgressDialog.setTitle("���볢�����������豸�׶�...");
				//����������Զ������Ƿ������ǵ��豸Ȼ��������
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
				if(isFind!=true)mProgressDialog.dismiss();//�ȴ����ڹر�
				break;//�������
			case 0x02:
				mProgressDialog.setTitle("��������ͨ�Ž׶�...");
				//����һ����õ�socket��������ͨ���̲߳������̼߳�������
				mBlueToothCommunicate.setSocket(mBlueToothConnect.mmSocket);
				mBlueToothCommunicate.start();

				mProgressDialog.dismiss();//�ȴ����ڹر�
				mButton1.setText("�Ͽ��ҵ�С����");
				mButton2.setEnabled(true);
				mButton3.setEnabled(true);
				break;//�������
			case 0x03:break;
			case 0x04:break;
			case 0x10:
				if(mBlueToothSearch.getBT()==true && mButton1.getText().equals("�������豸")){ 
					mButton1.setText("�����ҵ�С����");
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
					mButton1.setText("�������豸");
					mButton2.setEnabled(false);
					mButton3.setEnabled(false);
				}
				break;//����״̬�ı�
			default:break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_main);

		//��������֡��ʽ����βΪУ�飬�ڶ���0xEEΪ�����ٶȣ�0xDDΪ��ȡ�ٶȣ��������ٶ�ֵ��
		buffer=new byte[4];
		buffer[0]=(byte) 0xFF;
		buffer[1]=(byte) 0xEE;
		buffer[2]=(byte) 0x00;
		buffer[3]=(byte) 0xAA;
		
		//ʵ�������������ͣ����������ӡ�ͨ�ţ�
		//myHandler������������Ϣ��
		mBlueToothSearch=new BlueToothSearch(this, myHandler);
		mBlueToothConnect=new BlueToothConnect(myHandler);
		mBlueToothCommunicate=new BlueToothCommunicate(myHandler);
		
		mTextView = (TextView)findViewById(R.id.textView1);
		
		mButton1 = (Button) findViewById(R.id.button_start);
		if(mBlueToothSearch.getBT()==true) mButton1.setText("�����ҵ�С����");
		else mButton1.setText("�������豸");
		mButton1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mButton1.getText().equals("�������豸")){
					mBlueToothSearch.clearVector();
					mBlueToothSearch.openBT();
					mButton1.setText("�����ҵ�С����");
				}else if(mButton1.getText().equals("�����ҵ�С����")){
					mBlueToothSearch.clearVector();
					mBlueToothSearch.doDiscovery();
					
					mProgressDialog = ProgressDialog.show(UI_Main.this,"�������������豸�׶�...", "�Ե�һ��~", true);	
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
					mButton1.setText("�����ҵ�С����");
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
