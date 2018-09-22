package com.pg;

import java.util.Vector;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class Bullet {
	public Bitmap bmpBullet,xuanBmp;//�ӵ�ͼƬ��Դ,��ת���
	public int bulletX, bulletY;//�ӵ�������
	public int speed,speedX;	//�ӵ����ٶ�
	public int bulletType;//�ӵ��������Լ�����
	public static final int BULLET_PLAYER = -1;	//���ǵ�
	public static final int BULLET_PLAYER1= 0; //���Ǹ��ٵ�
	public static final int BULLET_DUCK = 1;//Ư�����
	public static final int BULLET_FLY = 2;//����ֵ�
	public boolean isDead;//�ӵ��Ƿ����� �Ż�����
	private int angle=0;//��ת��[0-360����ֵΪ0��˳ʱ���]
	static public int num=0;//���ٵ�������
	
	public Bullet(Bitmap bmpBullet, int bulletX, int bulletY, int bulletType) {
		this.bmpBullet = bmpBullet;
		this.bulletX = bulletX;
		this.bulletY = bulletY;
		this.bulletType = bulletType;
		switch (bulletType) {//��ͬ���ӵ������ٶȲ�һ
		case BULLET_PLAYER:
			speed = 4;
			break;
		case BULLET_PLAYER1:
			speed = 3;
			speedX= 0;
			angle=0;
			num++;
			break;
		case BULLET_DUCK:
			speed = 3;
			break;
		case BULLET_FLY:
			speed = 4;
			break;
		}
	}//�ӵ���ǰ����

	//�ӵ��Ļ���
	public void draw(Canvas canvas, Paint paint) {
		Matrix matrix = new Matrix();
	    matrix.postRotate(angle);   /*��תangle��*/
	    int width = bmpBullet.getWidth();
	    int height = bmpBullet.getHeight();
	    xuanBmp = Bitmap.createBitmap(bmpBullet, 0, 0, width, height, matrix, true);
		canvas.drawBitmap(xuanBmp, bulletX, bulletY, paint);
	}

	//�ӵ����߼�
	public void logic(Vector<Enemy> vcEnemy) {
		switch (bulletType) {//��ͬ���ӵ������߼���һ
		case BULLET_PLAYER://���ǵ��ӵ���ֱ�����˶�
			bulletY -= speed;
			if (bulletY < -50) {
				isDead = true;
			}
			break;
		case BULLET_PLAYER1:
			double minLength=100000;
			int findPos=-1;
			for (int i=0;i<vcEnemy.size();i++){//���뵱ǰ�ӵ�����ĵ����±�[���ӵ�ǰ��ĵ�����]
				if(vcEnemy.elementAt(i).y<bulletY){
					double curLength=vcEnemy.elementAt(i).getLength(bulletX, bulletY);
					if(curLength<minLength){
						minLength=curLength;
						findPos=i;
					}
				}
			}
			if(findPos!=-1){//��Ŀ�����x������ٶ�
				double tan=1.0*(vcEnemy.elementAt(findPos).x-bulletX)/(vcEnemy.elementAt(findPos).y-bulletY);
				angle=-(int)(Math.atan(tan)*180/3.1415926);
				//speedX=(int)(speed*tan);
				if(tan<0)speedX=-speed*2;
				else speedX=speed*2;
			}else{
				speedX=0;
				angle=0;
			}
			bulletY -= speed;
			if (bulletY < -50) {
				isDead = true;
				num--;
			}
			bulletX -= speedX;
			if(bulletX<2){
				bulletX=2;
				speedX=0;
			}else if(bulletX>MySurfaceView.screenW-12){
				bulletX=MySurfaceView.screenW-12;
				speedX=0;				
			}
			break;
		case BULLET_DUCK://Ư���������ֵ��ӵ����Ǵ�ֱ�����˶�
		case BULLET_FLY:
			bulletY += speed;
			if (bulletY > MySurfaceView.screenH) {
				isDead = true;
			}
			break;
		}
	}
}
