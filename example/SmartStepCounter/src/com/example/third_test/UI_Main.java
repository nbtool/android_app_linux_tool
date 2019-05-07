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

	// 蓝牙三剑客
	public BlueToothSearch mBlueToothSearch;
	public BlueToothConnect mBlueToothConnect;
	public BlueToothCommunicate mBlueToothCommunicate;

	// 数据池
	public static DataPool mDataPool;

	// 等待dialog
	//private ProgressDialog mProgressDialog;
    private ListViewDialog mListViewDialog;

	// 折线图类
	private ChartLine mChartLine;

	// 几个控件
	private Button mNewSeries, mStopRun;
	private CheckBox[] mCheckBox;
	private int[] mCheckBoxId = new int[] { R.id.checkBox1, R.id.checkBox2,
			R.id.checkBox3, R.id.checkBox4 };
	private TextView mTextView, mTextView2;
	private SeekBar mSeekBar;

	// 全局变量
	private int mUpperLimit = 0;// 记步上限
	private int mNum = 0;// 总记步数
	private int mTime = 0;// 横轴时间

	// 消息句柄(线程里无法进行界面更新，所以要把消息从线程里发送出来在消息句柄里进行处理)
	public Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x00:
				break;// 出现异常或为搜索到设备
			case 0x01:
				//mProgressDialog.setTitle("进入尝试连接蓝牙设备阶段...");
				// 当搜索完毕自动查找是否是我们的设备然后尝试连接
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
					//mProgressDialog.dismiss();// 等待窗口关闭
				break;// 搜索完毕
			case 0x02:
				//mProgressDialog.setTitle("进入启动通信阶段...");
				// 将上一步获得的socket传给蓝牙通信线程并启动线程监听数据
				mBlueToothCommunicate.setSocket(mBlueToothConnect.mmSocket);
				mBlueToothCommunicate.start();
				//mProgressDialog.dismiss();// 等待窗口关闭
				mNewSeries.setText("断开我的小手环");
				break;// 连接完毕
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
					if (all > mUpperLimit) {// 记步-和加速度超过设定上限则记步
						mNum++;
						mTextView2.setText("当前记步数为: " + mNum);
					}
					mTime += 1;
					mChartLine.letChartMove(mTime);// 控制图形滚动
					mChartLine.mChartView.repaint();
				}
				break;
			case 0x10:
				if (mBlueToothSearch.getBT() == true
						&& mNewSeries.getText().equals("打开蓝牙设备")) {
					mNewSeries.setText("连接我的小手环");
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
					mNewSeries.setText("打开蓝牙设备");
				}
				break;// 蓝牙状态改变
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

		// 实例化蓝牙三剑客（搜索、连接、通信）
		// myHandler是用来反馈信息的
		mBlueToothSearch = new BlueToothSearch(this, myHandler);
		mBlueToothConnect = new BlueToothConnect(myHandler);
		mBlueToothCommunicate = new BlueToothCommunicate(myHandler);

		mDataPool = new DataPool(20000);

		mChartLine = new ChartLine();
		// 设置图标显示的基本属性
		mChartLine.setChartSettings("Time", "", 0, 100, -20000, 20000,
				Color.WHITE, Color.WHITE);
		// 设置四个折线图的属性
		mChartLine.setLineSettings();

		ChartThread.start();// 启动图标更新线程

		// 开始绘制按钮及点击监听
		mNewSeries = (Button) findViewById(R.id.new_series);
		if (mBlueToothSearch.getBT() == true)
			mNewSeries.setText("连接我的小手环");
		else
			mNewSeries.setText("打开蓝牙设备");
		mNewSeries.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (mNewSeries.getText().equals("打开蓝牙设备")) {
					mBlueToothSearch.clearVector();
					mBlueToothSearch.openBT();
					mNewSeries.setText("连接我的小手环");
				} else if (mNewSeries.getText().equals("连接我的小手环")) {
					mBlueToothSearch.clearVector();
					mBlueToothSearch.doDiscovery();
					//mProgressDialog = ProgressDialog.show(UI_Main.this,
				//			"进入搜索蓝牙设备阶段...", "稍等一下~", true);
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

					mNewSeries.setText("连接我的小手环");
				}
			}
		});

		// 图标滚动与不滚动切换
		mStopRun = (Button) findViewById(R.id.stop_run);
		mStopRun.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mChartLine.canRun = !mChartLine.canRun;
				mStopRun.setText(mChartLine.canRun ? "Stop" : "Run");
			}
		});

		// 控制显示几条折线的checkBox
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
			// 当用户对拖动条的拖动的动作完成时触发
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mUpperLimit = mSeekBar.getProgress() * 200;
				mTextView.setText("当前记步上限为: " + mUpperLimit);
			}

			// 当用户对拖动条进行拖动时触发
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				mTextView.setText("<拖动条>拖动中...");
			}

			// 当拖动条的值发生改变的时触发
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mTextView.setText("设置记步上限为：" + progress * 200);
			}
		});
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// buttonView 选中状态发生改变的那个按钮
		// isChecked 表示按钮新的状态（true/false）
		for (int i = 0; i < 4; i++) {
			if (mCheckBox[i] == buttonView) {
				if (isChecked) {
					mChartLine.showLine(i);// 显示第i个折线
				} else {
					mChartLine.hideLine(i);// 隐藏第i个折线
				}
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 设置ChartView
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
					// mChartLine.letChartMove(num);// 控制图形滚动
					// mChartLine.mChartView.repaint();
					// 周期性发送更新Chart的消息（因为UI不能放在这个里面更新）
					Message msg = new Message();
					msg.what = 0x04;
					myHandler.sendMessage(msg);
				} catch (InterruptedException e) {
				}
			}
		}
	};
}
