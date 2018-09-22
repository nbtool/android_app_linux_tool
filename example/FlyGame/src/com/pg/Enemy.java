package com.pg;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Enemy {
	public int type;//���˵������ʶ
	public static final int TYPE_FLY = 1;//�����
	public static final int TYPE_DUCKL = 2;//Ư����(���������˶�)
	public static final int TYPE_DUCKR = 3;//(���������˶�)
	public static final int TYPE_BOSS =4;//BOSS
	public static int PH=400;//Ѫ��[]
	private static final int BOSS_STATE_H = 5;//ƽ��
	private static final int BOSS_STATE_V_DOWN = 6;//������
	private static final int BOSS_STATE_V_UP   = 7;//������
	private static final int BOSS_STATE_FIRE   = 8;//�Ŵ���
	private static final int BOSS_STATE_CUT    = 9;//������
	public int ph;//BOSSѪ��
	private int bosscount;//BOSS��ʱ��
	private int state,oldstate;//BOSS״̬
	public Bitmap bmpEnemy;//����ͼƬ��Դ
	public int x, y;//��������
	public int frameW, frameH;//����ÿ֡�Ŀ��
	private int frameIndex;//���˵�ǰ֡�±�
	private int speed,speed_boss_y;//���˵��ƶ��ٶ�
	public boolean isDead;//�жϵ����Ƿ��Ѿ�����
	static public int createEnemyTime = 50;//ÿ�����ɵ��˵�ʱ��(����)
	static public int createBulletTime= 40;//ÿ�����ɵ����ӵ���ʱ��
	public Enemy(Bitmap bmpEnemy, int enemyType, int x, int y) {
		this.bmpEnemy = bmpEnemy;
		frameW = bmpEnemy.getWidth() / 10;
		frameH = bmpEnemy.getHeight();
		this.type = enemyType;
		this.x = x;
		this.y = y;
		switch (type) {//��ͬ����ĵ����ٶȲ�ͬ
		case TYPE_FLY://�����
			speed = 25;
			break;
		case TYPE_DUCKL:
			speed = 3;
			break;
		case TYPE_DUCKR:
			speed = 3;
			break;
		case TYPE_BOSS:
			speed = 4;
			speed_boss_y=0;
			ph=PH;
			bosscount=0;
			state=BOSS_STATE_H;
			break;
		}
	}//���˵Ĺ��캯��
	
	static public void reset(){
		createEnemyTime = 50;//ÿ�����ɵ��˵�ʱ��(����)
		createBulletTime= 40;//ÿ�����ɵ����ӵ���ʱ��
	}//�������ݺ���
	public void draw(Canvas canvas, Paint paint) {
		canvas.save();
		canvas.clipRect(x, y, x + frameW, y + frameH);
		canvas.drawBitmap(bmpEnemy, x - frameIndex * frameW, y, paint);
		canvas.restore();
	}//���˻�ͼ����

	public void logic() {
		switch (type) {//��ͬ����ĵ���ӵ�в�ͬ��AI�߼�
		case TYPE_FLY:
			if (isDead == false) {
				//���ٳ��֣����ٷ���
				speed -= 1;
				y += speed;
				if (y <= -200) {
					isDead = true;
				}
			}
			break;
		case TYPE_DUCKL:
			if (isDead == false) {
				//б���½��˶�
				x += speed / 2;
				y += speed;
				if (x > MySurfaceView.screenW) {
					isDead = true;
				}
			}
			break;
		case TYPE_DUCKR:
			if (isDead == false) {
				//б���½��˶�
				x -= speed / 2;
				y += speed;
				if (x < -50) {
					isDead = true;
				}
			}
			break;
		case TYPE_BOSS:
			if(isDead==false){
				switch(state){
				case BOSS_STATE_H://ƽ��
					x += speed / 2;
					if (x<-100 || x > MySurfaceView.screenW+30){
						speed=-speed;
					}
					break;
				case BOSS_STATE_V_DOWN:
					//���ٳ��֣����ٷ���
					speed -= 1;
					y += speed;
					break;
				case BOSS_STATE_V_UP:
					//���ٳ��֣����ٷ���
					speed -= 1;
					y += speed;
					break;
				case BOSS_STATE_FIRE:
					break;
				case BOSS_STATE_CUT://�����в��ƶ�
					bosscount++;
					if(bosscount>10){
						bosscount=0;
						state=BOSS_STATE_H;//�ָ�ԭ��״̬
					}
					break;
				default:break;
				}
			}
		break;
		}
	}//�����߼�AI

	public boolean isCollsionWith(Bullet bullet) {
		int x2 = bullet.bulletX;
		int y2 = bullet.bulletY;
		int w2 = bullet.bmpBullet.getWidth();
		int h2 = bullet.bmpBullet.getHeight();
		if (x >= x2 && x >= x2 + w2) {
			return false;
		} else if (x <= x2 && x + frameW <= x2) {
			return false;
		} else if (y >= y2 && y >= y2 + h2) {
			return false;
		} else if (y <= y2 && y + frameH <= y2) {
			return false;
		}
		//������ײ����������
		if(type==TYPE_BOSS && ph!=0){//BOSS�������Ѫ
			//oldstate=state;// 
			//state=BOSS_STATE_CUT;//�����оͲ��ܶ�
			//bosscount=0;//��ʼʱ
			ph--;
		}else isDead = true;
		return true;
	}//�ж���ײ(�����������ӵ���ײ)
	
	public double getLength(int x0,int y0){
		return Math.sqrt((x0-x)*(x0-x)+(y0-y)*(y0-y));
	}//��ȡ�����ľ��뺯��[�������׷�ٵ�]
}

