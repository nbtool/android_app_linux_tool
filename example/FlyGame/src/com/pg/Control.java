package com.pg;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

public class Control {
	Paint paint;//����
	//��������Բ�ε����ĵ�������뾶
	private float smallCenterX,smallCenterY,smallCenterR;
	private float BigCenterX,BigCenterY,BigCenterR;
	//���캯������������λ�ú�СԲ����Բ�뾶
	Control(float centerX,float centerY,float sR,float bR){
		smallCenterX=BigCenterX=centerX;
		BigCenterY=smallCenterY=centerY;
		smallCenterR=sR;
		BigCenterR=bR;
		paint=new Paint(Color.RED);
	}
	//���»ָ�ԭ״̬
	public void reSet(){
		smallCenterX=BigCenterX;
		smallCenterY=BigCenterY;
	}
	//��ͼ����
	public void myDraw(Canvas canvas) {
		//���ƴ�Բ
		paint.setAlpha(0x77);
		canvas.drawCircle(BigCenterX, BigCenterY, BigCenterR,paint);
		//����СԲ
		canvas.drawCircle(smallCenterX, smallCenterY, smallCenterR,paint);
	}
	//��������
	public boolean onTouchEvent(MotionEvent event,Player player) {
		//���û���ָ̧��Ӧ�ûָ�СԲ����ʼλ��
		if (event.getAction() == MotionEvent.ACTION_UP) {
			smallCenterX = BigCenterX;
			smallCenterY = BigCenterY;
			player.setDirect(0);//��λ���ƶ�
		} else {
			player.setDirect(0);//��λ���ƶ�
			int pointX = (int) event.getX();
			int pointY = (int) event.getY();
			double angle=getRad(BigCenterX, BigCenterY, pointX, pointY);//��ȡƫת�ǶȻ���
			//�ж��û������λ���Ƿ��ڴ�Բ��
			if (Math.sqrt(Math.pow((BigCenterX - (int) event.getX()), 2) + Math.pow((BigCenterY - (int) event.getY()), 2)) <= BigCenterR) {
				//��СԲ�����û�����λ���ƶ�
				smallCenterX = pointX;
				smallCenterY = pointY;
			} else {
				setSmallCircleXY(BigCenterX, BigCenterY, BigCenterR,angle);	
			}
			angle=angle/Math.PI*180;//������ת��Ϊ�Ƕ�[����]
			if(angle>=-150 && angle<=-30)player.setDirect(1);
			else if(angle>=30 && angle<=150)player.setDirect(3);
			if(Math.abs(angle)>=120)player.setDirect(2);
			else if(Math.abs(angle)<=60)player.setDirect(4);
		}
		return true;
	}
	/** 
	 * СԲ����ڴ�Բ��Բ���˶�ʱ������СԲ���ĵ������λ��
	 * @param centerX 
	 *            Χ�Ƶ�Բ��(��Բ)���ĵ�X����
	 * @param centerY 
	 *            Χ�Ƶ�Բ��(��Բ)���ĵ�Y����
	 * @param R
	 * 			     Χ�Ƶ�Բ��(��Բ)�뾶
	 * @param rad 
	 *            ��ת�Ļ��� 
	 */
	public void setSmallCircleXY(float centerX, float centerY, float R, double rad) {
		//��ȡԲ���˶���X����   
		smallCenterX = (float) (R * Math.cos(rad)) + centerX;
		//��ȡԲ���˶���Y����  
		smallCenterY = (float) (R * Math.sin(rad)) + centerY;
	}
	/**
	 * �õ�����֮��Ļ���
	 * @param px1    ��һ�����X����
	 * @param py1    ��һ�����Y����
	 * @param px2    �ڶ������X����
	 * @param py2    �ڶ������Y����
	 * @return
	 */
	public double getRad(float px1, float py1, float px2, float py2) {
		//�õ�����X�ľ���  
		float x = px2 - px1;
		//�õ�����Y�ľ���  
		float y = py1 - py2;
		//���б�߳�  
		float Hypotenuse = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		//�õ�����Ƕȵ�����ֵ��ͨ�����Ǻ����еĶ��� ���ڱ�/б��=�Ƕ�����ֵ��  
		float cosAngle = x / Hypotenuse;
		//ͨ�������Ҷ����ȡ����ǶȵĻ���  
		float rad = (float) Math.acos(cosAngle);
		//��������λ��Y����<ҡ�˵�Y��������Ҫȡ��ֵ-0~-180  
		if (py2 < py1) {
			rad = -rad;
		}
		return rad;
	}
}
