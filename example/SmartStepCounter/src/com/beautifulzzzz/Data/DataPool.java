package com.beautifulzzzz.Data;

/**
 * 数据池：将蓝牙串口收到的数据放入一个环形数组pool中保存 每次取数据只去最新的40个 并在ask中解析最新的信息保存在X\Y\Z中
 * 
 * @author LiTao
 *
 */
public class DataPool {

	private int p_write;// 正在写的位置
	private int pool_size;// 池子容量
	private byte[] pool;// 池子
	public int X, Y, Z;
	private boolean haveData;// 是否有新的数据

	/**
	 * 数据池构造函数
	 * 
	 * @param pool_size
	 *            数据池大小 一般要保证一个取周期中数据不被覆盖
	 */
	public DataPool(int pool_size) {
		p_write = 0;
		haveData = false;
		this.pool_size = pool_size;
		pool = new byte[pool_size];
	}

	/**
	 * 询问当前值
	 * 
	 * @return 如果解析到则返回真,数据存放在公有成员变量X、Y、Z中
	 */
	public boolean ask() {
		if (haveData == false)
			return false;
		haveData = false;

		byte[] str = new byte[40];
		int data_X, data_Y, data_Z;

		int i = 0;// 立刻将相应的40个字符复制出来
		int p_read_from = p_write - 40;

		while (i < 40) {
			str[i] = pool[(p_read_from + pool_size) % pool_size];
			i++;
			p_read_from++;
		}
		i = 39;
		while (i > 18 && str[i] != '$')
			i--;
		if (i == 18)
			return false;
		i--;
		data_Z = 0;
		for (int j = 4; j > -1; j--) {
			data_Z *= 10;
			data_Z += (str[i - j] - '0');
		}
		if (str[i - 5] == '-')
			data_Z = -data_Z;
		i -= 6;

		data_Y = 0;
		for (int j = 4; j > -1; j--) {
			data_Y *= 10;
			data_Y += (str[i - j] - '0');
		}
		if (str[i - 5] == '-')
			data_Y = -data_Y;
		i -= 6;

		data_X = 0;
		for (int j = 4; j > -1; j--) {
			data_X *= 10;
			data_X += (str[i - j] - '0');
		}
		if (str[i - 5] == '-')
			data_X = -data_X;

		X = data_X;
		Y = data_Y;
		Z = data_Z;
		return true;
	}

	/**
	 * 向数据池中插入length长的数据
	 * 
	 * @param data
	 *            数据
	 * @param length
	 *            长度
	 */
	public void push_back(byte[] data, int length) {
		for (int i = 0; i < length; i++) {
			pool[p_write++] = data[i];
			if (p_write == pool_size)
				p_write = 0;
		}
		haveData = true;
	}
}
