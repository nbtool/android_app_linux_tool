/***
 * 初始化ConnectedThread(BluetoothSocket socket)
 * 然后启动线程就能够不断接受数据（可以用handler把接收的数据传出）
 * 当想写入数据则要调用write函数
 * @author LiTao
 */
package com.beautifulzzzz.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.example.third_test.UI_Main;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class BlueToothCommunicate extends Thread {

	private BluetoothSocket mmSocket;
	private InputStream mmInStream;
	private OutputStream mmOutStream;
	private Handler mHandler;
	private boolean state;// 控制进程用

	public BlueToothCommunicate(Handler mHandler) {
		this.mHandler = mHandler;
		state = true;
	}

	public void setSocket(BluetoothSocket socket) {
		mmSocket = socket;
		InputStream tmpIn = null;
		OutputStream tmpOut = null;
		// 获取输入输出流
		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
		}
		mmInStream = tmpIn;
		mmOutStream = tmpOut;
	}

	// 利用线程一直收数据
	public void run() {
		byte[] buffer = new byte[1024];
		int bytes;
		// 循环一直接收
		while (state) {
			try {
				// bytes是返回读取的字符数量，其中数据存在buffer中
				bytes = mmInStream.read(buffer);
				String readMessage = new String(buffer, 0, bytes);
				Log.i("beautifulzzzz", "read: " + bytes + "  mes: "
						+ readMessage);
				UI_Main.mDataPool.push_back(buffer, bytes);
			} catch (IOException e) {
				break;
			}
		}
	}

	// 发送就直接发送，没有用线程
	public void write(byte[] buffer) throws IOException {
		mmOutStream.write(buffer);
	}

	public void cancel() {
		try {
			state = false;// 让死循环停止
			mmSocket.close();
			mmInStream.close();
			mmOutStream.close();
		} catch (IOException e) {
		}
	}
}
