package com.beautifulzzzz.Data;

/**
 * ���ݳأ������������յ������ݷ���һ����������pool�б��� ÿ��ȡ����ֻȥ���µ�40�� ����ask�н������µ���Ϣ������X\Y\Z��
 * 
 * @author LiTao
 *
 */
public class DataPool {

	private int p_write;// ����д��λ��
	private int pool_size;// ��������
	private byte[] pool;// ����
	public int X, Y, Z;
	private boolean haveData;// �Ƿ����µ�����

	/**
	 * ���ݳع��캯��
	 * 
	 * @param pool_size
	 *            ���ݳش�С һ��Ҫ��֤һ��ȡ���������ݲ�������
	 */
	public DataPool(int pool_size) {
		p_write = 0;
		haveData = false;
		this.pool_size = pool_size;
		pool = new byte[pool_size];
	}

	/**
	 * ѯ�ʵ�ǰֵ
	 * 
	 * @return ����������򷵻���,���ݴ���ڹ��г�Ա����X��Y��Z��
	 */
	public boolean ask() {
		if (haveData == false)
			return false;
		haveData = false;

		byte[] str = new byte[40];
		int data_X, data_Y, data_Z;

		int i = 0;// ���̽���Ӧ��40���ַ����Ƴ���
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
	 * �����ݳ��в���length��������
	 * 
	 * @param data
	 *            ����
	 * @param length
	 *            ����
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
