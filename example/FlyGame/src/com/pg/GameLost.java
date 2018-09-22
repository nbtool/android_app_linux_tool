package com.pg;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;


public class GameLost {
	
	private Bitmap bmpGameOver;//��Ϸ����ͼƬ
	private Bitmap bmpButton;//���¿�ʼͼƬ
	private int goX,goY,goStopY;//gameover��ͼλ��
	private int btnX,btnY,btnStopX,btnStopY;//��ť��ͼλ��
	private Boolean isPress;//�Ƿ��˰�ť
	private int state;//��������״̬0-gameover���룻1��ʱ��2restart���룻3��ʱ��4�ȴ�;5restart�ƶ�
	//���캯��
	GameLost(Bitmap bmpGameOver, Bitmap reStart){
		this.bmpGameOver=bmpGameOver;
		this.bmpButton=reStart;
		goX = MySurfaceView.screenW / 2 - bmpGameOver.getWidth() / 2;//����
		goStopY = MySurfaceView.screenH /2 - bmpGameOver.getHeight()*6/5;//�м�ƫ��
		goY=-200;
		btnStopX = MySurfaceView.screenW + 100;
		btnX = MySurfaceView.screenW / 2 - bmpButton.getWidth() / 2;//����
		btnStopY = MySurfaceView.screenH - bmpButton.getHeight()*3/2;//�м�ƫ��
		btnY=MySurfaceView.screenH+50;
		isPress=false;//��ť״̬
		state=0;
	}
	//��ͼ����
	public void draw(Canvas canvas, Paint paint){
		switch(state){
		case 0:
			goY+=20;
			if(goY>=goStopY){
				state=1;
			}
			canvas.drawBitmap(bmpGameOver,goX,goY,paint);
			break;
		case 1:state=2;break;
		case 2:
			btnY-=20;
			if(btnY<=btnStopY){
				state=3;
			}
			canvas.drawBitmap(bmpGameOver,goX,goY,paint);
			canvas.drawBitmap(bmpButton,btnX,btnY,paint);
			break;
		case 3:state=4;
		case 4:
			canvas.drawBitmap(bmpGameOver,goX,goY,paint);
			canvas.drawBitmap(bmpButton,btnX,btnY,paint);
			break;
		case 5:
			btnX+=20;
			if(btnX>=btnStopX){
				isPress = false;
				MySurfaceView.gameState = MySurfaceView.GAME_MENU;//�ı䵱ǰ��Ϸ״̬Ϊ��ʼ��Ϸ
			}
			canvas.drawBitmap(bmpGameOver,goX,goY,paint);
			canvas.drawBitmap(bmpButton,btnX,btnY,paint);
			break;
		default:break;
		}
	}
	//��������
	public void onTouchEvent(MotionEvent event) {
		//if(isPress)return;
		//��ȡ�û���ǰ����λ��
		int pointX = (int) event.getX();
		int pointY = (int) event.getY();
		//���û��ǰ��¶������ƶ�����
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
			//�ж��û��Ƿ����˰�ť
			if (pointX > btnX && pointX < btnX + bmpButton.getWidth()) {
				if (pointY > btnY && pointY < btnY + bmpButton.getHeight()) {
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
			if (pointX > btnX && pointX < btnX + bmpButton.getWidth()) {
				if (pointY > btnY && pointY < btnY + bmpButton.getHeight()) {
					state=5;
					//��ԭButton״̬Ϊδ����״̬
					//isPress = false;
					//MySurfaceView.gameState = MySurfaceView.GAME_MENU;//�ı䵱ǰ��Ϸ״̬Ϊ��ʼ��Ϸ
				}
			}
		}
	}
}
