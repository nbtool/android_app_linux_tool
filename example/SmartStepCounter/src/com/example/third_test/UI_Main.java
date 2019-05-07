package com.example.third_test;

import com.beautifulzzzz.Data.DataPool;
import com.beautifulzzzz.bluetooth.BlueToothCommunicate;
import com.beautifulzzzz.bluetooth.BlueToothConnect;
import com.beautifulzzzz.bluetooth.BlueToothSearch;
import com.beautifulzzzz.chart.ChartLine;
import com.example.third_test.ListViewDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class UI_Main extends Activity implements OnCheckedChangeListener {

	// ����������
	public BlueToothSearch mBlueToothSearch;
	public BlueToothConnect mBlueToothConnect;
	public BlueToothCommunicate mBlueToothCommunicate;

	// ���ݳ�
	public static DataPool mDataPool;

	// �ȴ�dialog
	//private ProgressDialog mProgressDialog;
    private ListViewDialog mListViewDialog;

	// ����ͼ��
	private ChartLine mChartLine;

	// �����ؼ�
	private Button mNewSeries, mStopRun;
	private CheckBox[] mCheckBox;
	private int[] mCheckBoxId = new int[] { R.id.checkBox1, R.id.checkBox2,
			R.id.checkBox3, R.id.checkBox4 };
	private TextView mTextView, mTextView2;
	private SeekBar mSeekBar;

	// ȫ�ֱ���
	private int mUpperLimit = 0;// �ǲ�����
	private int mNum = 0;// �ܼǲ���
	private int mTime = 0;// ����ʱ��

	// ��Ϣ���(�߳����޷����н�����£�����Ҫ����Ϣ���߳��﷢�ͳ�������Ϣ�������д���)
	public Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x00:
				break;// �����쳣��Ϊ�������豸
			case 0x01:
				//mProgressDialog.setTitle("���볢�����������豸�׶�...");
				// ����������Զ������Ƿ������ǵ��豸Ȼ��������
				boolean isFind = false;
				for (int i = 0; i < mBlueToothSearch.mNameVector.size(); i++) {
					if (mBlueToothSearch.mNameVector.get(i)
							.equals("LiTaoLanYa")) {
						Log.i("beautifulzzzz",
								mBlueToothSearch.mNameVector.get(i));
						mBlueToothConnect
								.setDevice(mBlueToothSearch.mAddrVector.get(i));
						mBlueToothConnect.start();
						isFind = true;
						break;
					}
				}
				if (isFind != true)
					//mProgressDialog.dismiss();// �ȴ����ڹر�
				break;// �������
			case 0x02:
				//mProgressDialog.setTitle("��������ͨ�Ž׶�...");
				// ����һ����õ�socket��������ͨ���̲߳������̼߳�������
				mBlueToothCommunicate.setSocket(mBlueToothConnect.mmSocket);
				mBlueToothCommunicate.start();
				//mProgressDialog.dismiss();// �ȴ����ڹر�
				mNewSeries.setText("�Ͽ��ҵ�С�ֻ�");
				break;// �������
			case 0x03:
				break;
			case 0x04:
				if (mDataPool.ask() == true) {
					int all = (int) Math.sqrt(mDataPool.X * mDataPool.X
							+ mDataPool.Y * mDataPool.Y + mDataPool.Z
							* mDataPool.Z) - 16000;
					mChartLine.addData(0, mTime, mDataPool.X);
					mChartLine.addData(1, mTime, mDataPool.Y);
					mChartLine.addData(2, mTime, mDataPool.Z);
					mChartLine.addData(3, mTime, all);
					if (all > mUpperLimit) {// �ǲ�-�ͼ��ٶȳ����趨������ǲ�
						mNum++;
						mTextView2.setText("��ǰ�ǲ���Ϊ: " + mNum);
					}
					mTime += 1;
					mChartLine.letChartMove(mTime);// ����ͼ�ι���
					mChartLine.mChartView.repaint();
				}
				break;
			case 0x10:
				if (mBlueToothSearch.getBT() == true
						&& mNewSeries.getText().equals("�������豸")) {
					mNewSeries.setText("�����ҵ�С�ֻ�");
				} else if (mBlueToothSearch.getBT() == false) {
					if (mBlueToothConnect != null) {
						mBlueToothConnect.cancel();
						mBlueToothConnect = null;
						mBlueToothConnect = new BlueToothConnect(myHandler);
					}
					if (mBlueToothCommunicate != null) {
						mBlueToothCommunicate.cancel();
						mBlueToothCommunicate = null;
						mBlueToothCommunicate = new BlueToothCommunicate(
								myHandler);
					}
					mNewSeries.setText("�������豸");
				}
				break;// ����״̬�ı�
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_main);
        
        mListViewDialog = new ListViewDialog(this);

		// ʵ�������������ͣ����������ӡ�ͨ�ţ�
		// myHandler������������Ϣ��
		mBlueToothSearch = new BlueToothSearch(this, myHandler);
		mBlueToothConnect = new BlueToothConnect(myHandler);
		mBlueToothCommunicate = new BlueToothCommunicate(myHandler);

		mDataPool = new DataPool(20000);

		mChartLine = new ChartLine();
		// ����ͼ����ʾ�Ļ�������
		mChartLine.setChartSettings("Time", "", 0, 100, -20000, 20000,
				Color.WHITE, Color.WHITE);
		// �����ĸ�����ͼ������
		mChartLine.setLineSettings();

		ChartThread.start();// ����ͼ������߳�

		// ��ʼ���ư�ť���������
		mNewSeries = (Button) findViewById(R.id.new_series);
		if (mBlueToothSearch.getBT() == true)
			mNewSeries.setText("�����ҵ�С�ֻ�");
		else
			mNewSeries.setText("�������豸");
		mNewSeries.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (mNewSeries.getText().equals("�������豸")) {
					mBlueToothSearch.clearVector();
					mBlueToothSearch.openBT();
					mNewSeries.setText("�����ҵ�С�ֻ�");
				} else if (mNewSeries.getText().equals("�����ҵ�С�ֻ�")) {
					mBlueToothSearch.clearVector();
					mBlueToothSearch.doDiscovery();
					//mProgressDialog = ProgressDialog.show(UI_Main.this,
				//			"�������������豸�׶�...", "�Ե�һ��~", true);
                    mListViewDialog.show();
				} else {
					if (mBlueToothConnect != null) {
						mBlueToothConnect.cancel();
						mBlueToothConnect = null;
						mBlueToothConnect = new BlueToothConnect(myHandler);
					}
					if (mBlueToothCommunicate != null) {
						mBlueToothCommunicate.cancel();
						mBlueToothCommunicate = null;
						mBlueToothCommunicate = new BlueToothCommunicate(
								myHandler);
					}

					mNewSeries.setText("�����ҵ�С�ֻ�");
				}
			}
		});

		// ͼ������벻�����л�
		mStopRun = (Button) findViewById(R.id.stop_run);
		mStopRun.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mChartLine.canRun = !mChartLine.canRun;
				mStopRun.setText(mChartLine.canRun ? "Stop" : "Run");
			}
		});

		// ������ʾ�������ߵ�checkBox
		mCheckBox = new CheckBox[4];
		for (int i = 0; i < 4; i++) {
			mCheckBox[i] = (CheckBox) findViewById(mCheckBoxId[i]);
			mCheckBox[i].setChecked(true);
			mCheckBox[i].setOnCheckedChangeListener(this);
		}

		mSeekBar = (SeekBar) findViewById(R.id.seekbar);
		mTextView = (TextView) findViewById(R.id.textview);
		mTextView2 = (TextView) findViewById(R.id.textview2);
		mSeekBar.setSecondaryProgress(mUpperLimit);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			// ���û����϶������϶��Ķ������ʱ����
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mUpperLimit = mSeekBar.getProgress() * 200;
				mTextView.setText("��ǰ�ǲ�����Ϊ: " + mUpperLimit);
			}

			// ���û����϶��������϶�ʱ����
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				mTextView.setText("<�϶���>�϶���...");
			}

			// ���϶�����ֵ�����ı��ʱ����
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mTextView.setText("���üǲ�����Ϊ��" + progress * 200);
			}
		});
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// buttonView ѡ��״̬�����ı���Ǹ���ť
		// isChecked ��ʾ��ť�µ�״̬��true/false��
		for (int i = 0; i < 4; i++) {
			if (mCheckBox[i] == buttonView) {
				if (isChecked) {
					mChartLine.showLine(i);// ��ʾ��i������
				} else {
					mChartLine.hideLine(i);// ���ص�i������
				}
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// ����ChartView
		mChartLine.setChartViewSetting(this);
	}

	private Thread ChartThread = new Thread() {
		public void run() {
			while (true) {
				try {
					sleep(100);
					// for (int i = 0; i < 4; i++) {
					// mChartLine.addData(i,num,(int) (20000 *
					// Math.random()*(Math.random()>0.5 ? -1:1)));
					// }
					// num += 1;
					// mChartLine.letChartMove(num);// ����ͼ�ι���
					// mChartLine.mChartView.repaint();
					// �����Է��͸���Chart����Ϣ����ΪUI���ܷ������������£�
					Message msg = new Message();
					msg.what = 0x04;
					myHandler.sendMessage(msg);
				} catch (InterruptedException e) {
				}
			}
		}
	};
}
