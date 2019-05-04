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

	// 总共的数据种类
	private String[] titles = new String[] { "X-轴", "Y-轴", "Z-轴", "All-轴" };
	// 总共的颜色
	private int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.CYAN,
			Color.YELLOW };
	// 总共的点的类型
	private PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE,
			PointStyle.DIAMOND, PointStyle.TRIANGLE, PointStyle.SQUARE };
	// 总共的XY轴数据序列
	private XYSeries[] mCurrentSeries = new XYSeries[titles.length];
	// 总共的XY轴数据序列渲染器/显示器
	private XYSeriesRenderer[] renderer = new XYSeriesRenderer[titles.length];

	// 标记折线图是否滚动
	public boolean canRun = true;

	public GraphicalView mChartView;

	// 控制图形滚动
	public void letChartMove(int num) {
		// 如果canRun标记为false则不滚动，但是数据仍然接收
		if (canRun == false)
			return;

		// 计算当前X轴最大最小值差,因为缩放按钮能够影响到它
		double dis = mRenderer.getXAxisMax() - mRenderer.getXAxisMin();
		if (num > mRenderer.getXAxisMax()) {// 用这两句能够巧妙控制实现动态折线图，而且还能查看历史数据
			double min = num - dis;
			double max = num;
			mRenderer.setXAxisMin(min);// 最小最大值
			mRenderer.setXAxisMax(max);
		}
	}

	// setChartSettings(mRenderer, "Time", "dBm", 0, 100, -20, 120, Color.WHITE,
	// Color.WHITE);
	// http://blog.csdn.net/chenpig/article/details/7352611
	// API的一些用法：http://www.cnblogs.com/zhanganju/p/3758782.html?utm_source=tuicool
	// 设置表格显示的一些属性
	public void setChartSettings(String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		// 有关对图表的渲染可参看api文档
		mRenderer.setXTitle(xTitle);// 名字
		mRenderer.setYTitle(yTitle);
		mRenderer.setXAxisMin(xMin);// 最小最大值
		mRenderer.setXAxisMax(xMax);
		mRenderer.setYAxisMin(yMin);
		mRenderer.setYAxisMax(yMax);
		mRenderer.setAxesColor(axesColor);// 坐标轴颜色
		mRenderer.setLabelsColor(labelsColor);// 标号颜色
		mRenderer.setShowGrid(true);// 显示网格
		mRenderer.setGridColor(Color.GRAY);
		mRenderer.setXLabels(16);
		mRenderer.setYLabels(20);
		mRenderer.setYLabelsAlign(Align.RIGHT);// 设置标签居Y轴的方向
		mRenderer.setPointSize((float) 2);
		mRenderer.setShowLegend(true);// 下面的标注
		// mRenderer.setZoomButtonsVisible(true);// 放大缩小按钮
		mRenderer.setZoomEnabled(true, false);// 设置缩放,这边是横向可以缩放,竖向不能缩放
		mRenderer.setPanEnabled(true, false);// 设置滑动,这边是横向可以滑动,竖向不可滑动
	}

	// 设置折线的一些属性
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
			renderer[i].setFillPoints(true);// 实心还是空心
			renderer[i].setDisplayChartValues(false);// 不显示值
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

	// 显示第i个折线图
	public void showLine(int i) {
		mDataset.addSeries(mCurrentSeries[i]);
		mRenderer.addSeriesRenderer(renderer[i]);
	}

	// 隐藏第i个折线图
	public void hideLine(int i) {
		mDataset.removeSeries(mCurrentSeries[i]);
		mRenderer.removeSeriesRenderer(renderer[i]);
	}

	// 向第i个折线图中添加(x,y)数据
	public void addData(int i, double x, double y) {
		mCurrentSeries[i].add(x, y);
	}
}
