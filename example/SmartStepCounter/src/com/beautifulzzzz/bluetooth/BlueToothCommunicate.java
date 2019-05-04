/***
 * ��ʼ��ConnectedThread(BluetoothSocket socket)
 * Ȼ�������߳̾��ܹ����Ͻ������ݣ�������handler�ѽ��յ����ݴ�����
 * ����д��������Ҫ����write����
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
	private boolean state;// ���ƽ�����

	public BlueToothCommunicate(Handler mHandler) {
		this.mHandler = mHandler;
		state = true;
	}

	public void setSocket(BluetoothSocket socket) {
		mmSocket = socket;
		InputStream tmpIn = null;
		OutputStream tmpOut = null;
		// ��ȡ���������
		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
		}
		mmInStream = tmpIn;
		mmOutStream = tmpOut;
	}

	// �����߳�һֱ������
	public void run() {
		byte[] buffer = new byte[1024];
		int bytes;
		// ѭ��һֱ����
		while (state) {
			try {
				// bytes�Ƿ��ض�ȡ���ַ��������������ݴ���buffer��
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

	// ���;�ֱ�ӷ��ͣ�û�����߳�
	public void write(byte[] buffer) throws IOException {
		mmOutStream.write(buffer);
	}

	public void cancel() {
		try {
			state = false;// ����ѭ��ֹͣ
			mmSocket.close();
			mmInStream.close();
			mmOutStream.close();
		} catch (IOException e) {
		}
	}
}
