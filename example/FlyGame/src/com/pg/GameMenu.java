package com.pg;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

public class GameMenu {
	private Bitmap bmpMenu;//�˵�����ͼ
	private Bitmap bmpButton[]=new Bitmap[2];//��ťͼƬ��Դ(���º�δ����ͼ)
	private int btnX, btnY;//��ť������
	private Boolean isPress;//��ť�Ƿ��±�ʶλ
	private int change;//ͼƬ�л�
	public GameMenu(Bitmap bmpMenu, Bitmap bmpButton, Bitmap bmpButtonPress) {
		this.bmpMenu = bmpMenu;
		this.bmpButton[0] = bmpButton;
		this.bmpButton[1] = bmpButtonPress;
		//X���У�Y
		btnX = MySurfaceView.screenW / 2 - bmpButton.getWidth() / 2;
		btnY = MySurfaceView.screenH/2 + bmpButton.getHeight();
		change=0;
		isPress = false;
	}//�˵���ʼ��
	public void draw(Canvas canvas, Paint paint) {
		//���Ʋ˵�����ͼ
		canvas.drawBitmap(bmpMenu, 0, 0, paint);
		//����δ���°�ťͼ
		if (isPress) {//�����Ƿ��»��Ʋ�ͬ״̬�İ�ťͼ
			canvas.drawBitmap(bmpButton[change], btnX, btnY, paint);
			btnY+=4;
			if(btnY>MySurfaceView.screenH - bmpButton[0].getHeight()){
				//��ԭButton״̬Ϊδ����״̬
				isPress = false;
				MySurfaceView.gameState = MySurfaceView.GAMEING;//�ı䵱ǰ��Ϸ״̬Ϊ��ʼ��Ϸ
				paint.setColor(Color.WHITE);
			}
		}else{
			canvas.drawBitmap(bmpButton[change], btnX, btnY, paint);
		}
		change=(change+1)%2;
	}//�˵���ͼ����
	//�˵������¼���������Ҫ���ڴ���ť�¼�
	public void onTouchEvent(MotionEvent event) {
		if(isPress)return;
		//��ȡ�û���ǰ����λ��
		int pointX = (int) event.getX();
		int pointY = (int) event.getY();
		//���û��ǰ��¶������ƶ�����
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
			//�ж��û��Ƿ����˰�ť
			if (pointX > btnX && pointX < btnX + bmpButton[0].getWidth()) {
				if (pointY > btnY && pointY < btnY + bmpButton[0].getHeight()) {
					isPress = true;
				} else {
					isPress = false;
				}
			} else {
				isPress = false;
			}
			//���û���̧����
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			//̧���ж��Ƿ�����ť����ֹ�û��ƶ�����
			if (pointX > btnX && pointX < btnX + bmpButton[0].getWidth()) {
				if (pointY > btnY && pointY < btnY + bmpButton[0].getHeight()) {
					//��ԭButton״̬Ϊδ����״̬
					//isPress = false;
					//MySurfaceView.gameState = MySurfaceView.GAMEING;//�ı䵱ǰ��Ϸ״̬Ϊ��ʼ��Ϸ
				}
			}
		}
	}
}
