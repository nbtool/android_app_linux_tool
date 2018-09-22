package com.pg;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
//import android.renderscript.Font;
import android.view.KeyEvent;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class Player {
	//���ǵ�Ѫ����Ѫ��λͼ
	private int playerHp =3;//Ĭ��3Ѫ
	private Bitmap bmpPlayerHp;
	public int x, y,x_v,y_v;//���ǵ�������
	private Bitmap bmpPlayer;//��λͼ
	private int speed = 5;//�����ƶ��ٶ�
	private int goals=0;//���Ƿ���
	public int bulletKind=0;//�����ӵ����࣬���ݵ�ǰ������������
	private boolean isUp, isDown, isLeft, isRight;//�����ƶ���ʶ[��������]
	private boolean isL,isR;//����ר��
	private int noCollisionCount = 0;//��ʱ��//��ײ�����޵�ʱ��
	private int noCollisionTime = 60;//�޵�ʱ��
	private boolean isCollision;//�Ƿ���ײ�ı�ʶλ
	//��������
	private SensorManager sm;//����һ��������������
	private Sensor sensor;//����һ��������
	private SensorEventListener mySensorListener;//����һ��������������
	//���ǵĹ��캯��
	@SuppressLint("InlinedApi")
	public Player(Bitmap bmpPlayer, Bitmap bmpPlayerHp) {
		this.bmpPlayer = bmpPlayer;
		this.bmpPlayerHp = bmpPlayerHp;
		x = MySurfaceView.screenW / 2 - bmpPlayer.getWidth() / 2;
		y = MySurfaceView.screenH - bmpPlayer.getHeight();
		x_v=y_v=0;
		goals=0;
		isL=isR=false;
		bulletKind=0;
		/*
		//������
		sm = (SensorManager) MainActivity.instance.getSystemService(Service.SENSOR_SERVICE);//��ȡ����������ʵ��
		sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//ʵ��һ������������ʵ��  
		//ʵ��������������
		mySensorListener = new SensorEventListener() {
			@Override
			//��������ȡֵ�����ı�ʱ����Ӧ�˺���  
			public void onSensorChanged(SensorEvent event) {
				if(Math.abs(event.values[0])<1.5){//���ƶ�
					x_v+=0;
					isR=isL=false;
				}else if(event.values[0]<0){//����
					if(isR)x_v+=2;
					else{
						isR=true;
						x_v=3;
					}
				}else{//����
					if(isL)x_v-=2;
					else{
						isL=true;
						x_v=-3;
					}
				}
				//x>0 ˵����ǰ�ֻ��� x<0�ҷ�    ��-10��10
				//y_v+= event.values[1]/2;//������xֵ��x<0��,y<0�Ϸ�
			}
			@Override
			//�������ľ��ȷ����ı�ʱ��Ӧ�˺���  
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		//Ϊ������ע�������
		sm.registerListener(mySensorListener, sensor, SensorManager.SENSOR_DELAY_GAME);
		 */
	}
	//���ǵĻ�ͼ����
	public void draw(Canvas canvas, Paint paint) {
		//��������
		//�������޵�ʱ��ʱ����������˸
		if (isCollision) {
			//ÿ2����Ϸѭ��������һ������
			if (noCollisionCount % 2 == 0) {
				canvas.drawBitmap(bmpPlayer, x, y, paint);
			}
		} else {
			canvas.drawBitmap(bmpPlayer, x, y, paint);
		}
		//��������Ѫ��
		for (int i = 0; i < playerHp; i++) {
			canvas.drawBitmap(bmpPlayerHp, i * bmpPlayerHp.getWidth(),MySurfaceView.screenH - bmpPlayerHp.getHeight(), paint);
		}
		canvas.drawText("$:"+String.valueOf(goals),playerHp*bmpPlayerHp.getWidth(),MySurfaceView.screenH - bmpPlayerHp.getHeight()+15,paint);
		canvas.drawLine(playerHp*bmpPlayerHp.getWidth(),MySurfaceView.screenH - bmpPlayerHp.getHeight()+25, playerHp*bmpPlayerHp.getWidth()+15,MySurfaceView.screenH - bmpPlayerHp.getHeight()+25, paint);
	}
	//ʵ�尴��
	public void onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			isUp = true;
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			isDown = true;
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			isLeft = true;
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			isRight = true;
		}
	}
	//ʵ�尴��̧��
	public void onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			isUp = false;
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			isDown = false;
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			isLeft = false;
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			isRight = false;
		}
	}
	//���ǵ��߼�
	public void logic() {
		if(goals<100){//���ݷ����趨��������
			bulletKind=0;
		}else if(goals<200){
			bulletKind=1;
		}else if(goals<300){
			bulletKind=2;
		}else if(goals<400){
			bulletKind=3;
		}else if(goals<500){
			bulletKind=4;
		}else if(goals<600){
			bulletKind=5;
		}else if(goals<700){
			bulletKind=6;
		}else if(goals<800){
			bulletKind=7;
		}else if(goals<900){
			bulletKind=8;
		}else if(goals<1000){
			bulletKind=9;
		}else if(goals<1100){
			bulletKind=10;
		}else if(goals<1200){
			bulletKind=11;
		}else if(goals<1300){
			bulletKind=12;
		}else if(goals<1400){
			bulletKind=13;
		}
		//���������ƶ�
		if (isLeft) {
			x -= speed;
		}
		if (isRight) {
			x += speed;
		}
		if (isUp) {
			y -= speed;
		}
		if (isDown) {
			y += speed;
		}
		if(Math.abs(x_v)>5)x_v=x_v/Math.abs(x_v)*5;
		x+=x_v;
		//�ж���ĻX�߽�
		if (x + bmpPlayer.getWidth() >= MySurfaceView.screenW) {
			x = MySurfaceView.screenW - bmpPlayer.getWidth();
			x_v=0;
			isL=isR=false;
		} else if (x <= 0) {
			x = 0;
			x_v=0;
			isL=isR=false;
		}
		//�ж���ĻY�߽�
		if (y + bmpPlayer.getHeight() >= MySurfaceView.screenH) {
			y = MySurfaceView.screenH - bmpPlayer.getHeight();	
		} else if (y <= 0) {
			y = 0;
		}
	
		//�����޵�״̬
		if (isCollision) {
			//��ʱ����ʼ��ʱ
			noCollisionCount++;
			if (noCollisionCount >= noCollisionTime) {
				//�޵�ʱ����󣬽Ӵ��޵�״̬����ʼ��������
				isCollision = false;
				noCollisionCount = 0;
			}
		}
	}
	//��������Ϊ�޵�̬��ʱ��time
	public void setPlayerNoCollision(int time){
		isCollision=true;
	}
	//��������Ѫ��
	public void setPlayerHp(int hp) {
		this.playerHp = hp;
	}
	//��ȡ����Ѫ��
	public int getPlayerHp() {
		return playerHp;
	}
	//�������Ƿ���
	public void addPlayerGoals(int num){
		goals+=num;
	}
	//��ȡ���Ƿ���
	public int getPlayerGoals(){
		return goals;
	}
	//�ж���ײ(������л�)
	public boolean isCollsionWith(Enemy en) {
		//�Ƿ����޵�ʱ��
		if (isCollision == false) {
			int x2 = en.x;
			int y2 = en.y;
			int w2 = en.frameW;
			int h2 = en.frameH;
			if (x >= x2 && x >= x2 + w2) {
				return false;
			} else if (x <= x2 && x + bmpPlayer.getWidth() <= x2) {
				return false;
			} else if (y >= y2 && y >= y2 + h2) {
				return false;
			} else if (y <= y2 && y + bmpPlayer.getHeight() <= y2) {
				return false;
			}
			//��ײ�������޵�״̬
			isCollision = true;
			return true;
			//�����޵�״̬��������ײ
		} else {
			return false;
		}
	}
	//�ж���ײ(������л��ӵ�)
	public boolean isCollsionWith(Bullet bullet) {
		//�Ƿ����޵�ʱ��
		if (isCollision == false) {
			int x2 = bullet.bulletX;
			int y2 = bullet.bulletY;
			int w2 = bullet.bmpBullet.getWidth();
			int h2 = bullet.bmpBullet.getHeight();
			if (x >= x2 && x >= x2 + w2) {
				return false;
			} else if (x <= x2 && x + bmpPlayer.getWidth() <= x2) {
				return false;
			} else if (y >= y2 && y >= y2 + h2) {
				return false;
			} else if (y <= y2 && y + bmpPlayer.getHeight() <= y2) {
				return false;
			}
			//��ײ�������޵�״̬
			isCollision = true;
			return true;
			//�����޵�״̬��������ײ
		} else {
			return false;
		}
	}
	//�����˶�����[0������1up,2,left,3,down,4,right]
	public void setDirect(int dir){
		switch(dir){
		case 0:isLeft=isRight=isUp=isDown=false;break;
		case 1:isUp=true;break;
		case 2:isLeft=true;break;
		case 3:isDown=true;break;
		case 4:isRight=true;break;
		default:break;
		}
	}
}
