package com.pg;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Boom {
	
	private Bitmap bmpBoom;//��ըЧ����Դͼ
	private int boomX, boomY;//��ըЧ����λ������
	private int cureentFrameIndex;//��ը�������ŵ�ǰ��֡�±�
	private int totleFrame;//��ըЧ������֡��
	private int frameW, frameH;//ÿ֡�Ŀ��
	public boolean playEnd;//�Ƿ񲥷���ϣ��Ż�����
	//��ըЧ���Ĺ��캯��
	public Boom(Bitmap bmpBoom, int x, int y, int totleFrame) {
		this.bmpBoom = bmpBoom;
		this.boomX = x;
		this.boomY = y;
		this.totleFrame = totleFrame;
		frameW = bmpBoom.getWidth() / totleFrame;
		frameH = bmpBoom.getHeight();
	}
	//��ըЧ������
	public void draw(Canvas canvas, Paint paint) {
		canvas.save();
		canvas.clipRect(boomX, boomY, boomX + frameW, boomY + frameH);
		canvas.drawBitmap(bmpBoom, boomX - cureentFrameIndex * frameW, boomY, paint);
		canvas.restore();
	}
	//��ըЧ�����߼�
	public void logic() {
		if (cureentFrameIndex < totleFrame) {
			cureentFrameIndex++;
		} else {
			playEnd = true;
		}
	}
}
