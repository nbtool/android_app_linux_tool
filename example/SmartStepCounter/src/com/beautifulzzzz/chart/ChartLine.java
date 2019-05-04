package com.beautifulzzzz.chart;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.example.third_test.R;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ChartLine {
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

	// �ܹ�����������
	private String[] titles = new String[] { "X-��", "Y-��", "Z-��", "All-��" };
	// �ܹ�����ɫ
	private int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.CYAN,
			Color.YELLOW };
	// �ܹ��ĵ������
	private PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE,
			PointStyle.DIAMOND, PointStyle.TRIANGLE, PointStyle.SQUARE };
	// �ܹ���XY����������
	private XYSeries[] mCurrentSeries = new XYSeries[titles.length];
	// �ܹ���XY������������Ⱦ��/��ʾ��
	private XYSeriesRenderer[] renderer = new XYSeriesRenderer[titles.length];

	// �������ͼ�Ƿ����
	public boolean canRun = true;

	public GraphicalView mChartView;

	// ����ͼ�ι���
	public void letChartMove(int num) {
		// ���canRun���Ϊfalse�򲻹���������������Ȼ����
		if (canRun == false)
			return;

		// ���㵱ǰX�������Сֵ��,��Ϊ���Ű�ť�ܹ�Ӱ�쵽��
		double dis = mRenderer.getXAxisMax() - mRenderer.getXAxisMin();
		if (num > mRenderer.getXAxisMax()) {// ���������ܹ��������ʵ�ֶ�̬����ͼ�����һ��ܲ鿴��ʷ����
			double min = num - dis;
			double max = num;
			mRenderer.setXAxisMin(min);// ��С���ֵ
			mRenderer.setXAxisMax(max);
		}
	}

	// setChartSettings(mRenderer, "Time", "dBm", 0, 100, -20, 120, Color.WHITE,
	// Color.WHITE);
	// http://blog.csdn.net/chenpig/article/details/7352611
	// API��һЩ�÷���http://www.cnblogs.com/zhanganju/p/3758782.html?utm_source=tuicool
	// ���ñ����ʾ��һЩ����
	public void setChartSettings(String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		// �йض�ͼ�����Ⱦ�ɲο�api�ĵ�
		mRenderer.setXTitle(xTitle);// ����
		mRenderer.setYTitle(yTitle);
		mRenderer.setXAxisMin(xMin);// ��С���ֵ
		mRenderer.setXAxisMax(xMax);
		mRenderer.setYAxisMin(yMin);
		mRenderer.setYAxisMax(yMax);
		mRenderer.setAxesColor(axesColor);// ��������ɫ
		mRenderer.setLabelsColor(labelsColor);// �����ɫ
		mRenderer.setShowGrid(true);// ��ʾ����
		mRenderer.setGridColor(Color.GRAY);
		mRenderer.setXLabels(16);
		mRenderer.setYLabels(20);
		mRenderer.setYLabelsAlign(Align.RIGHT);// ���ñ�ǩ��Y��ķ���
		mRenderer.setPointSize((float) 2);
		mRenderer.setShowLegend(true);// ����ı�ע
		// mRenderer.setZoomButtonsVisible(true);// �Ŵ���С��ť
		mRenderer.setZoomEnabled(true, false);// ��������,����Ǻ����������,����������
		mRenderer.setPanEnabled(true, false);// ���û���,����Ǻ�����Ի���,���򲻿ɻ���
	}

	// �������ߵ�һЩ����
	public void setLineSettings() {
		for (int i = 0; i < titles.length; i++) {
			// create a new series of data
			mCurrentSeries[i] = new XYSeries(titles[i]);
			mDataset.addSeries(mCurrentSeries[i]);
			// create a new renderer for the new series
			renderer[i] = new XYSeriesRenderer();
			mRenderer.addSeriesRenderer(renderer[i]);
			// set some renderer properties
			renderer[i].setPointStyle(styles[i]);
			renderer[i].setColor(colors[i]);
			renderer[i].setFillPoints(true);// ʵ�Ļ��ǿ���
			renderer[i].setDisplayChartValues(false);// ����ʾֵ
			renderer[i].setDisplayChartValuesDistance(10);
		}
	}

	public void setChartViewSetting(final Activity activity) {
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) activity
					.findViewById(R.id.chart);
			mChartView = ChartFactory.getLineChartView(activity, mDataset,
					mRenderer);
			// enable the chart click events
			mRenderer.setClickEnabled(true);
			mRenderer.setSelectableBuffer(10);
			mChartView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// handle the click event on the chart
					SeriesSelection seriesSelection = mChartView
							.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {
						// Toast.makeText(activity, "No chart element",
						// Toast.LENGTH_SHORT).show();
					} else {
						// display information of the clicked point
						Toast.makeText(
								activity,
								"Chart element in series index "
										+ seriesSelection.getSeriesIndex()
										+ " data point index "
										+ seriesSelection.getPointIndex()
										+ " was clicked"
										+ " closest point value X="
										+ seriesSelection.getXValue() + ", Y="
										+ seriesSelection.getValue(),
								Toast.LENGTH_SHORT).show();
					}
				}
			});
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		} else {
			mChartView.repaint();
		}
	}

	// ��ʾ��i������ͼ
	public void showLine(int i) {
		mDataset.addSeries(mCurrentSeries[i]);
		mRenderer.addSeriesRenderer(renderer[i]);
	}

	// ���ص�i������ͼ
	public void hideLine(int i) {
		mDataset.removeSeries(mCurrentSeries[i]);
		mRenderer.removeSeriesRenderer(renderer[i]);
	}

	// ���i������ͼ�����(x,y)����
	public void addData(int i, double x, double y) {
		mCurrentSeries[i].add(x, y);
	}
}
