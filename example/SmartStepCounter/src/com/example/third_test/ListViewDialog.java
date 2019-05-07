package com.example.third_test;

import android.app.Dialog;
import android.content.Context;
import android.widget.ListView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.view.Window;
import android.view.WindowManager;
import android.util.DisplayMetrics;
import java.util.List;
import java.util.ArrayList;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.Button;


public class ListViewDialog extends Dialog {

    private final Context mContext;
    private ListView lv ;//声明一个列表
    private List<String> list ;//声明一个List容器
    private ArrayAdapter<String> aa ;

	private Button mBtnNo, mBtnYes;

    public ListViewDialog(Context context) {
        super(context);
        mContext = context;
        initView();
        initListView();
        initButton();
    }

    private void initView() {
        View contentView = View.inflate(mContext, R.layout.content_dialog, null);
        lv = (ListView) contentView.findViewById(R.id.lv);
        setContentView(contentView);
    }

    private void initListView() {
        list = new ArrayList<String>();//实例化List
        //往容器中添加数据
        //list.add("Item1");
        //list.add("Item2");
        //list.add("Item3");
        //实例适配器
        //第一个参数：Context
        //第二个参数：ListView中每一行布局样式
        //android.R.layout.simple_list_item_1：系统中每行只显示一行文字布局
        //第三个参数：列表数据容器
        aa = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1,list);
        lv.setAdapter(aa);//将适配器数据映射ListView上
        //为列表添加监听
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Toast.makeText(mContext, "当前选中列表项的下标为："+arg2, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void add_item(String item){
        list.add(item);
        aa.notifyDataSetChanged();
        setHeight();
    }

    private void initButton(){
        mBtnNo = (Button) findViewById(R.id.btn_no);
        mBtnYes = (Button) findViewById(R.id.btn_yes);
        mBtnNo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
                add_item("xxxx");
            }
        });

        mBtnYes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            return;
        }
        setHeight();
    }

    private void setHeight() {
        Window window = getWindow();
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        WindowManager.LayoutParams attributes = window.getAttributes();
        if (window.getDecorView().getHeight() >= (int) (displayMetrics.heightPixels * 0.6)) {
            attributes.height = (int) (displayMetrics.heightPixels * 0.6);
        }
        window.setAttributes(attributes);
    }
}
