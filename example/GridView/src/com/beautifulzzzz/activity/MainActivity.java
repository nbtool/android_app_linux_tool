package com.beautifulzzzz.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {

	private GridView gview;
	private List<Map<String, Object>> data_list;
	private SimpleAdapter sim_adapter;
	// ICON
	private int[] icon = { R.drawable.icon_01, R.drawable.icon_02,
			R.drawable.icon_03, R.drawable.icon_04, R.drawable.icon_05,
			R.drawable.icon_06, R.drawable.icon_07, R.drawable.icon_08,
			R.drawable.icon_09, R.drawable.icon_10, R.drawable.icon_11,
			R.drawable.icon_12, R.drawable.icon_13, R.drawable.icon_14 };
	private String[] iconName = { "ͨ茶叶", "汉堡", "肉", "香肠", "披萨", "虾", "水果", "鱼",
			"面包", "蟹", "鸡腿", "根茎蔬菜", "蛋糕", "酒" };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		gview = (GridView) findViewById(R.id.gridView1);
		data_list = new ArrayList<Map<String, Object>>();

		getData();

		String[] from = { "image", "text" };
		int[] to = { R.id.image, R.id.text };
		sim_adapter = new SimpleAdapter(this, data_list, R.layout.item, from,
				to);

		gview.setAdapter(sim_adapter);
		gview.setOnItemClickListener(new ItemClickListener());
	}

	public List<Map<String, Object>> getData() {
		for (int i = 0; i < icon.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", icon[i]);
			map.put("text", iconName[i]);
			data_list.add(map);
		}

		return data_list;
	}

	// 当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件
	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
													// click happened
				View arg1,// The view within the AdapterView that was clicked
				int arg2,// The position of the view in the adapter
				long arg3// The row id of the item that was clicked
		) {
			// 在本例中arg2=arg3
			HashMap<String, Object> item = (HashMap<String, Object>) arg0
					.getItemAtPosition(arg2);
			// 显示所选Item的ItemText
			setTitle((String) item.get("text"));// the item is map,you can
												// seethe function getData,if
												// want get the value, just use
												// .get(key) to get the value
		}
	}
}
