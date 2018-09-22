package com.pg;

import java.util.Random;
import java.util.Vector;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class MySurfaceView extends SurfaceView implements Callback, Runnable {
	private SurfaceHolder sfh;
	private Paint paint;
	private Thread th;
	private boolean flag;
	private Canvas canvas;
	public static int screenW, screenH;
	//������Ϸ״̬����
	public static final int GAME_MENU = 0;//��Ϸ�˵�
	public static final int GAMEING = 1;//��Ϸ��
	public static final int GAME_WIN = 2;//��Ϸʤ��[NO]
	public static final int GAME_LOST = 3;//��Ϸʧ��
	public static final int GAME_PAUSE = -1;//��Ϸ�˵�
	//��ǰ��Ϸ״̬(Ĭ�ϳ�ʼ����Ϸ�˵�����)
	public static int gameState = GAME_MENU;
	//����һ��Resourcesʵ�����ڼ���ͼƬ
	private Resources res = this.getResources();
	//������Ϸ��Ҫ�õ���ͼƬ��Դ(ͼƬ����)
	private Bitmap bmpBackGround;//��Ϸ����
	private Bitmap bmpBoom;//��ըЧ��
	private Bitmap bmpButton;//��Ϸ��ʼ��ť
	private Bitmap bmpButtonPress;//��Ϸ��ʼ��ť�����
	private Bitmap bmpEnemyDuck;//����Ư����
	private Bitmap bmpEnemyFly;//���������
	private Bitmap bmpEnemyBoss;//BOSS
	private Bitmap bmpGameOver;//��Ϸʧ�ܱ���
	private Bitmap bmpGameReStart;//���¿�ʼ
	private Bitmap bmpPlayer;//��Ϸ����
	private Bitmap bmpPlayerHp;//����Ѫ��
	private Bitmap bmpMenu;//�˵�����
	public static Bitmap bmpBullet;//�ӵ�
	public static Bitmap bmpEnemyBullet;//�����ӵ�
	public static Bitmap bmpBossBullet;//Boss�ӵ�
	//��������
	private GameMenu gameMenu;//����һ���˵�����
	private GameBg backGround;//����һ��������Ϸ��������
	private Player player;//�������Ƕ���
	private Vector<Enemy> vcEnemy;//����һ����������
	private int count;//������
	//�������飺1��2��ʾ���˵�����
	//��ά�����ÿһά����һ�����
	private int enemyArray[][] = { { 1, 2,1 }, { 1, 1}, { 1, 3, 1, 2 }, { 1, 2 }, { 2, 3 }, { 3, 1, 3 }, { 2, 2 }, { 1, 2 }, { 2, 2 }, { 1, 3, 1, 1 }, { 2, 1 },
			{ 1, 3 }, { 2, 1 },{ 1, 3, 1, 1 },{ 3, 3, 3, 3 }};
	private int enemyArrayIndex;//��ǰȡ��һά������±�
	private Random random;//���˿⣬Ϊ�����ĵ��˸����漴����
	private Vector<Bullet> vcBullet;//�����ӵ�����
	private int countEnemyBullet;//����ӵ��ļ�����
	private Vector<Bullet> vcBulletPlayer;//�����ӵ�����
	private int countPlayerBullet;//����ӵ��ļ�����
	private Vector<Boom> vcBoom;//��ըЧ������	
	private Control control;//�����ֱ�
	private GameLost gamelost;//��Ϸ����
	public MySurfaceView(Context context) {
		super(context);		
		sfh = this.getHolder();
		sfh.addCallback(this);
		paint = new Paint(Color.RED);
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		//���ñ�������
		this.setKeepScreenOn(true);
	}//��ʼ������
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		screenW = this.getWidth();
		screenH = this.getHeight();
		initGame();
		flag = true;
		th = new Thread(this);//ʵ���߳�
		th.start();//�����߳�
	}//SurfaceView��ͼ��������Ӧ�˺���
	private void initGame() {
		//������Ϸ�����̨���½�����Ϸʱ����Ϸ������!
		//����Ϸ״̬���ڲ˵�ʱ���Ż�������Ϸ
		if (gameState == GAME_MENU) {
			//������Ϸ��Դ
			bmpBackGround = BitmapFactory.decodeResource(res, R.drawable.background);
			bmpBoom = BitmapFactory.decodeResource(res, R.drawable.boom);
			bmpButton = BitmapFactory.decodeResource(res, R.drawable.button);
			bmpButtonPress = BitmapFactory.decodeResource(res, R.drawable.button_press);
			bmpEnemyDuck = BitmapFactory.decodeResource(res, R.drawable.enemy_duck);
			bmpEnemyFly = BitmapFactory.decodeResource(res, R.drawable.enemy_fly);
			bmpEnemyBoss = BitmapFactory.decodeResource(res, R.drawable.enemy_pig);
			bmpGameOver = BitmapFactory.decodeResource(res, R.drawable.gameover);
			bmpGameReStart = BitmapFactory.decodeResource(res, R.drawable.restart);
			bmpPlayer = BitmapFactory.decodeResource(res, R.drawable.player);
			bmpPlayerHp = BitmapFactory.decodeResource(res, R.drawable.hp);
			bmpMenu = BitmapFactory.decodeResource(res, R.drawable.menu);
			bmpBullet = BitmapFactory.decodeResource(res,R.drawable.bullet);
			bmpEnemyBullet = BitmapFactory.decodeResource(res, R.drawable.bullet_enemy);
			
			vcBoom = new Vector<Boom>();//��ըЧ������ʵ��
			vcBullet = new Vector<Bullet>();//�����ӵ�����ʵ��
			vcBulletPlayer = new Vector<Bullet>();//�����ӵ�����ʵ��
			gameMenu = new GameMenu(bmpMenu, bmpButton, bmpButtonPress);//��1����ʵ��
			backGround = new GameBg(bmpBackGround);//ʵ����Ϸ����
			player = new Player(bmpPlayer, bmpPlayerHp);//ʵ������
			vcEnemy = new Vector<Enemy>();//ʵ����������
			random = new Random();//ʵ�������
			control=new Control(screenW-35,screenH-35,10,20);//����
			gamelost=new GameLost(bmpGameOver,bmpGameReStart);//��Ϸ����
		}
		control.reSet();//СԲ��λ
		Enemy.reset();//��������
		Bullet.num=0;
	}//�Զ����ʼ����Ϸ
	public void myDraw() {
		try {
			canvas = sfh.lockCanvas();
			if (canvas != null) {
				canvas.drawColor(Color.WHITE);
				switch (gameState){//��ͼ����������Ϸ״̬��ͬ���в�ͬ����
				case GAME_MENU://��ʼ״̬
					gameMenu.draw(canvas, paint);//�˵��Ļ�ͼ����
					break;
				case GAMEING://��Ϸ������
					backGround.draw(canvas, paint);//��Ϸ����
					player.draw(canvas, paint);//���ǻ�ͼ����
					for (int i=0;i<vcEnemy.size();i++) {//���˻���
						vcEnemy.elementAt(i).draw(canvas,paint);
					}
					for (int i=0;i<vcBullet.size();i++){//�����ӵ�����
						vcBullet.elementAt(i).draw(canvas, paint);
					}
					for (int i=0; i<vcBulletPlayer.size();i++) {
						vcBulletPlayer.elementAt(i).draw(canvas, paint);
					}//���������ӵ�����
					for (int i=0; i<vcBoom.size();i++) {
						vcBoom.elementAt(i).draw(canvas, paint);
					}//��ըЧ������
					control.myDraw(canvas);//�ֱ�����
					break;
				case GAME_PAUSE://��Ϸ���״̬
					break;
				case GAME_LOST://��Ϸ�����ͼ
					backGround.draw(canvas, paint);//��Ϸ����
					//player.draw(canvas, paint);//���ǻ�ͼ����
					for (int i=0;i<vcEnemy.size();i++) {//���˻���
						vcEnemy.elementAt(i).draw(canvas,paint);
					}
					for (int i=0;i<vcBullet.size();i++){//�����ӵ�����
						vcBullet.elementAt(i).draw(canvas, paint);
					}
					for (int i=0; i<vcBulletPlayer.size();i++) {
						vcBulletPlayer.elementAt(i).draw(canvas, paint);
					}//���������ӵ�����
					for (int i=0; i<vcBoom.size();i++) {
						vcBoom.elementAt(i).draw(canvas, paint);
					}//��ըЧ������
					//control.myDraw(canvas);//�ֱ�����
					gamelost.draw(canvas, paint);
					if(gameState==GAME_MENU){
						initGame();//������Ϸ
						enemyArrayIndex = 0;//���ù������
					}
					break;	
				}
			}
		} catch (Exception e){
			// TODO: handle exception
		} finally {
			if (canvas != null)
				sfh.unlockCanvasAndPost(canvas);
		}
	}//OnDraw��ͼ����
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (gameState) {//���������¼�����������Ϸ״̬��ͬ���в�ͬ����
		case GAME_MENU://�˵��Ĵ����¼�����
			gameMenu.onTouchEvent(event);
			break;
		case GAMEING://��Ϸ������
			control.onTouchEvent(event,player);//�ֱ�����
			break;
		case GAME_PAUSE://��Ϸ���
			break;
		case GAME_WIN://ʤ��
			break;
		case GAME_LOST://���
			gamelost.onTouchEvent(event);
			if(gameState==GAME_MENU){
				initGame();//������Ϸ
				enemyArrayIndex = 0;//���ù������
			}
			break;
		}
		return true;
	}//������������
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//����back���ذ���,������Ϸ
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//��Ϸʤ����ʧ�ܡ�����ʱ��Ĭ�Ϸ��ز˵�
			if (gameState == GAMEING || gameState == GAME_WIN || gameState == GAME_LOST) {
				gameState = GAME_MENU;
				initGame();//������Ϸ
				enemyArrayIndex = 0;//���ù������
			} else if (gameState == GAME_MENU) {//��ǰ��Ϸ״̬�ڲ˵����棬Ĭ�Ϸ��ذ����˳���Ϸ
				MainActivity.instance.finish();
				System.exit(0);
			}
			//��ʾ�˰����Ѵ������ٽ���ϵͳ����
			//�Ӷ�������Ϸ�������̨
			return true;
		}
		//���������¼�����������Ϸ״̬��ͬ���в�ͬ����
		switch (gameState) {
		case GAME_MENU:
			break;
		case GAMEING://������
			player.onKeyDown(keyCode,event);//���ǵİ��������¼�
			break;
		case GAME_PAUSE:
			break;
		case GAME_WIN:
			break;
		case GAME_LOST:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}//�������¼���
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		//����back���ذ���
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//��Ϸʤ����ʧ�ܡ�����ʱ��Ĭ�Ϸ��ز˵�
			if (gameState == GAMEING || gameState == GAME_WIN || gameState == GAME_LOST) {
				gameState = GAME_MENU;
			}
			//��ʾ�˰����Ѵ������ٽ���ϵͳ����
			//�Ӷ�������Ϸ�������̨
			return true;
		}
		//���������¼�����������Ϸ״̬��ͬ���в�ͬ����
		switch (gameState) {
		case GAME_MENU:
			break;
		case GAMEING:
			//����̧���¼�
			player.onKeyUp(keyCode, event);
			break;
		case GAME_PAUSE:
			break;
		case GAME_WIN:
			break;
		case GAME_LOST:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}//����̧�����
	private void logic() {
		switch (gameState) {//�߼����������Ϸ״̬��ͬ���в�ͬ����
		case GAME_MENU:
			break;
		case GAMEING:
			backGround.logic();//�����߼�
			player.logic();//�����߼�
			//begin-----�����߼�
			for (int i = 0; i < vcEnemy.size(); i++) {//�����߼�
				Enemy en = vcEnemy.elementAt(i);
				//��Ϊ����������ӵ��� ����ô�Ե���isDead�ж���
				//�����������ô�ʹ�������ɾ��,�����������Ż����ã�
				if (en.isDead) {
					vcEnemy.removeElementAt(i);
				} else {
					en.logic();
				}
			}
			//���ɵ���
			count++;
			if (count % Enemy.createEnemyTime == 0) {
				for (int i = 0; i < enemyArray[enemyArrayIndex].length; i++) {
					if (enemyArray[enemyArrayIndex][i] == 1){//�����
						int x = random.nextInt(screenW - 100) + 50;
						vcEnemy.addElement(new Enemy(bmpEnemyFly, 1, x, -50));
					} else if (enemyArray[enemyArrayIndex][i] == 2) {//Ư������
						int y = random.nextInt(20);
						vcEnemy.addElement(new Enemy(bmpEnemyDuck, 2, -50, y));
					} else if (enemyArray[enemyArrayIndex][i] == 3) {//Ư������
						int y = random.nextInt(20);
						vcEnemy.addElement(new Enemy(bmpEnemyDuck, 3, screenW + 50, y));
					} else if(enemyArray[enemyArrayIndex][i] == 4){//Boss
						vcEnemy.addElement(new Enemy(bmpEnemyBoss,4,-100,5));
					}
				}
				enemyArrayIndex=enemyArrayIndex+1;//15�����Ч��....һ�ֹ�ȥ�����Ѷ�
				if(enemyArrayIndex>=15){
					enemyArrayIndex=0;
					if(Enemy.createBulletTime>5 
							&& Enemy.createBulletTime>=Enemy.createEnemyTime)
						Enemy.createBulletTime-=5;
					else if(Enemy.createEnemyTime>5 
							&& Enemy.createBulletTime<=Enemy.createEnemyTime)
						Enemy.createEnemyTime-=5;
				}				
			}
			//������������ǵ���ײ
			for (int i = 0; i < vcEnemy.size(); i++) {
				if (player.isCollsionWith(vcEnemy.elementAt(i))) {
					player.setPlayerHp(player.getPlayerHp() - 1);//������ײ������Ѫ��-1
					if (player.getPlayerHp() <= -1) {//������Ѫ��С��0���ж���Ϸʧ��
						gameState = GAME_LOST;
					}
				}
			}
			//ÿ2�����һ�������ӵ�
			countEnemyBullet++;
			if (countEnemyBullet % Enemy.createBulletTime == 0) {
				for (int i=0;i<vcEnemy.size();i++){
					Enemy en=vcEnemy.elementAt(i);
					int bulletType=0;
					switch(en.type){//��ͬ���͵��˲�ͬ���ӵ����й켣
					case Enemy.TYPE_FLY://�����
						bulletType = Bullet.BULLET_FLY;
						break;
					case Enemy.TYPE_DUCKL://Ư����
					case Enemy.TYPE_DUCKR:
						bulletType = Bullet.BULLET_DUCK;
						break;
					case Enemy.TYPE_BOSS://boss���ӵ�
						bulletType = Bullet.BULLET_DUCK;//////������������������
						break;
					}
					vcBullet.add(new Bullet(bmpEnemyBullet, en.x + 10, en.y + 20, bulletType));
				}
			}
			for (int i = 0; i < vcBullet.size(); i++) {//��������ӵ��߼�
				Bullet b = vcBullet.elementAt(i);
				if (b.isDead) {
					vcBullet.removeElement(b);
				} else {
					b.logic(vcEnemy);
				}
			}
			for (int i = 0; i < vcBullet.size(); i++) {//��������ӵ���������ײ
				if (player.isCollsionWith(vcBullet.elementAt(i))) {
					player.setPlayerHp(player.getPlayerHp() - 1);//������ײ������Ѫ��-1
					if (player.getPlayerHp() <= -1) {
						gameState = GAME_LOST;
					}//������Ѫ��С��0���ж���Ϸʧ��
				}
			}
			for (int i = 0; i < vcBulletPlayer.size(); i++) {//���������ӵ��������ײ
				Bullet blPlayer = vcBulletPlayer.elementAt(i);//ȡ�������ӵ�������ÿ��Ԫ��
				for (int j = 0; j < vcEnemy.size(); j++) {
					//��ӱ�ըЧ��
					if (vcEnemy.elementAt(j).isCollsionWith(blPlayer)) {//ȡ������������ÿ��Ԫ�������ӵ������ж�
						vcBoom.add(new Boom(bmpBoom, vcEnemy.elementAt(j).x, vcEnemy.elementAt(j).y, 7));
						switch(vcEnemy.elementAt(j).type){
						case 1://�����
							player.addPlayerGoals(20);
							break;
						case 2:;//Ư����
						case 3:
							player.addPlayerGoals(10);
							break;
						case 4://Boss
							break;
						default:break;
						}
					}
				}
			}
			//-------------end-�����߼�
			//ÿ1�����һ�������ӵ�
			countPlayerBullet++;
			if (countPlayerBullet % 20 == 0) {
				switch(player.bulletKind){//�������ѡ��
				case 0://�����ӵ�
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 20, Bullet.BULLET_PLAYER));
					break;
				case 1://˫���ӵ�
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 10, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 20, player.y - 20, Bullet.BULLET_PLAYER));
					break;
				case 2://˫����ͨ+1������
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 10, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 20, player.y - 20, Bullet.BULLET_PLAYER));
					if(Bullet.num==0)vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER1));
					break;
				case 3://�����ӵ�+1������
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 8, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 23, player.y - 20, Bullet.BULLET_PLAYER));
					if(Bullet.num==0)vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER1));
					break;
				case 4://3��+2������
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 8, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 23, player.y - 20, Bullet.BULLET_PLAYER));
					if(Bullet.num<2)vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER1));
					break;
				case 5://4��2����
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 4, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 11, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 18, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 25, player.y - 20, Bullet.BULLET_PLAYER));
					if(Bullet.num<=2)vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER1));
					break;
				case 6://5��2����
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 3, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 9, player.y - 22, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 21, player.y - 22, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 27, player.y - 20, Bullet.BULLET_PLAYER));
					if(Bullet.num<=2)vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER1));
					break;
				case 7://ȫ����
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER1));
					break;
				case 8://˫ȫ����
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 10, player.y - 20, Bullet.BULLET_PLAYER1));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 20, player.y - 20, Bullet.BULLET_PLAYER1));
				case 9://����˫����
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 8, player.y - 20, Bullet.BULLET_PLAYER1));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 23, player.y - 20, Bullet.BULLET_PLAYER1));
					break;
				case 10://4��+2ȫ����
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 4, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 11, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 18, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 25, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 4, player.y - 25, Bullet.BULLET_PLAYER1));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 25, player.y - 25, Bullet.BULLET_PLAYER1));
					break;
				case 11://5��+2ȫ����
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 3, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 9, player.y - 22, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 21, player.y - 22, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 27, player.y - 20, Bullet.BULLET_PLAYER));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 3, player.y - 25, Bullet.BULLET_PLAYER1));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 27, player.y - 25, Bullet.BULLET_PLAYER1));
					break;
				default://5��ȫ����
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 3, player.y - 20, Bullet.BULLET_PLAYER1));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 9, player.y - 22, Bullet.BULLET_PLAYER1));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 15, player.y - 25, Bullet.BULLET_PLAYER1));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 21, player.y - 22, Bullet.BULLET_PLAYER1));
					vcBulletPlayer.add(new Bullet(bmpBullet, player.x + 27, player.y - 20, Bullet.BULLET_PLAYER1));
					break;
				}				
			}
			//���������ӵ��߼�
			for (int i = 0; i < vcBulletPlayer.size(); i++) {
				Bullet b = vcBulletPlayer.elementAt(i);
				if (b.isDead) {
					vcBulletPlayer.removeElement(b);
				} else {
					b.logic(vcEnemy);
				}
			}
			//��ըЧ���߼�
			for (int i = 0; i < vcBoom.size(); i++) {
				Boom boom = vcBoom.elementAt(i);
				if (boom.playEnd) {
					//������ϵĴ�������ɾ��
					vcBoom.removeElementAt(i);
				} else {
					vcBoom.elementAt(i).logic();
				}
			}
			break;
		case GAME_PAUSE:
			break;
		case GAME_WIN:
			break;
		case GAME_LOST:
			break;
		}
	}//��Ϸ�߼�
	@Override
	public void run() {
		while (flag) {
			long start = System.currentTimeMillis();
			myDraw();
			logic();
			long end = System.currentTimeMillis();
			try {
				if (end - start < 50) {//ʱ����⴦��
					Thread.sleep(50 - (end - start));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}//run����
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}//SurfaceView��ͼ״̬�����ı䣬��Ӧ�˺���
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		flag = false;
	}//SurfaceView��ͼ����ʱ����Ӧ�˺���
}
